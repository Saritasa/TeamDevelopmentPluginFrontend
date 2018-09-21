package com.saritasa.plugins.teamDevelopment;

import com.intellij.notification.NotificationListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.saritasa.plugins.common.exceptions.PluginException;
import com.saritasa.plugins.common.services.NotificationsService;
import com.saritasa.plugins.common.services.ProjectEnvironmentService;
import com.saritasa.plugins.teamDevelopment.dto.FileRevisionData;
import com.saritasa.plugins.teamDevelopment.services.FileRevisionsService;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.List;

public class CheckCurrentFileAction extends AnAction {

    /**
     * Checks whether this action enabled or not.
     *
     * @param e Update action to check state for
     * @return Enabled or not
     */
    private boolean actionEnabled(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        return psiFile != null && editor != null;
    }

    @Override
    public void update(AnActionEvent e) {
        boolean actionEnabled = this.actionEnabled(e);
        e.getPresentation().setEnabled(actionEnabled);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (!this.actionEnabled(e) || e.getProject() == null) {
            return;
        }

        NotificationsService notificationsService = ServiceManager.getService(NotificationsService.class);
        FileRevisionsService fileRevisionsService = ServiceManager.getService(e.getProject(), FileRevisionsService.class);
        ProjectEnvironmentService projectEnvironmentService = ServiceManager.getService(e.getProject(), ProjectEnvironmentService.class);

        try {
            PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

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