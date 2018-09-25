package com.saritasa.plugins.teamDevelopment;

import com.intellij.notification.NotificationListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.saritasa.plugins.common.exceptions.PluginException;
import com.saritasa.plugins.common.services.NotificationsService;
import com.saritasa.plugins.common.services.ProjectEnvironmentService;
import com.saritasa.plugins.teamDevelopment.dto.FileRevisionData;
import com.saritasa.plugins.teamDevelopment.services.FileRevisionsService;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Team development plugin core. Can check file for team development intersection.
 */
public class TeamDevelopmentPlugin {
    private Project project;

    public TeamDevelopmentPlugin(Project project) {
        this.project = project;
    }

    /**
     * Checks file for team development. Notifies about result.
     *
     * @param psiFile File to check
     */
    public void checkFileIntersection(PsiFile psiFile) {
        NotificationsService notificationsService = ServiceManager.getService(NotificationsService.class);
        FileRevisionsService fileRevisionsService = ServiceManager.getService(psiFile.getProject(), FileRevisionsService.class);
        ProjectEnvironmentService projectEnvironmentService = ServiceManager.getService(psiFile.getProject(), ProjectEnvironmentService.class);

        try {
            List<FileRevisionData> revisionsList = fileRevisionsService.registerNewRevision(psiFile);

            List<String> sameFileDevelopers = new ArrayList<>();

            for (FileRevisionData fileRevisionData : revisionsList) {
                if (fileRevisionData.getDeveloperName().equals(projectEnvironmentService.getDeveloperName())) {
                    continue;
                }
                sameFileDevelopers.add(fileRevisionData.getDeveloperName());
            }

            String checkResult = "This file not edited by other developers";

            NotificationListener notificationListener = null;

            if (!sameFileDevelopers.isEmpty()) {
                checkResult = String.join("<br>", sameFileDevelopers);
                checkResult = checkResult.concat("<br><br><a href='#someLink'>Some link</a>");
                notificationListener = (notification, hyperlinkEvent) -> {
                    if (!hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        return;
                    }

                    if (hyperlinkEvent.getDescription().equals("#someLink")) {
                        notificationsService.warning("Action clicked", "My congratulations!");
                    }
                };
            }

            notificationsService.information("Existing revisions:", checkResult, notificationListener);
        } catch (PluginException exception) {
            notificationsService.warning("Team Development Plugin", exception.getMessage());
        }
    }
}
