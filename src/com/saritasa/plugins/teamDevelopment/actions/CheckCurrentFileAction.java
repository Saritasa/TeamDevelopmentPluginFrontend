package com.saritasa.plugins.teamDevelopment.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.saritasa.plugins.teamDevelopment.TeamDevelopmentPlugin;
import org.jetbrains.annotations.NotNull;

public class CheckCurrentFileAction extends AnAction {

    /**
     * Checks whether this action enabled or not.
     *
     * @param event Update action to check state for
     * @return Enabled or not
     */
    private boolean actionEnabled(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        return psiFile != null && editor != null;
    }

    /**
     * Plugin state updating handler. Can react for changed IDE environment.
     *
     * @param event Fired event with IDE state
     */
    @Override
    public void update(AnActionEvent event) {
        boolean actionEnabled = this.actionEnabled(event);
        event.getPresentation().setEnabled(actionEnabled);
    }

    /**
     * Action performing handler.
     *
     * @param event Fired event with IDE state
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        if (!this.actionEnabled(event) || event.getProject() == null) {
            return;
        }

        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);

        TeamDevelopmentPlugin teamDevelopmentPlugin = ServiceManager.getService(event.getProject(), TeamDevelopmentPlugin.class);
        teamDevelopmentPlugin.checkFileIntersection(psiFile);
    }
}