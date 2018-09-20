package com.saritasa.teamDevelopment;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.saritasa.teamDevelopment.common.NotificationsManager;
import com.saritasa.teamDevelopment.common.ProjectEnvironment;
import com.saritasa.teamDevelopment.common.exceptions.PluginException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CheckCurrentFileAction extends AnAction {

    private final NotificationsManager notificationManager;
    private final ProjectEnvironment projectEnvironment;
    private final FileRevisionsService fileRevisionsService;

    public CheckCurrentFileAction() {
        super();
        this.notificationManager = new NotificationsManager();
        this.fileRevisionsService = new FileRevisionsService();
        this.projectEnvironment = new ProjectEnvironment();
    }

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
        if (!this.actionEnabled(e)) {
            return;
        }

        try {
            PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

            List<FileRevisionData> revisionsList = this.fileRevisionsService.registerNewRevision(psiFile);

            List<String> sameFileDevelopers = new ArrayList<>();

            for (FileRevisionData fileRevisionData : revisionsList) {
                if (fileRevisionData.getDeveloperName().equals(this.projectEnvironment.getDeveloperName())) {
                    continue;
                }
                sameFileDevelopers.add(fileRevisionData.getDeveloperName());
            }

            String checkResult = "This file not edited by other developers";

            if (!sameFileDevelopers.isEmpty()) {
                checkResult = String.join(" ,", sameFileDevelopers);
            }

            this.notificationManager.information("Existing revisions:", checkResult);
        } catch (PluginException exception) {
            this.notificationManager.warning("Team Development Plugin", exception.getMessage());
        }
    }
}