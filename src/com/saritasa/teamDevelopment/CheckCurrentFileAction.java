package com.saritasa.teamDevelopment;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CheckCurrentFileAction extends AnAction {

    /**
     * Returns relative path of the given file.
     *
     * @param project Project to get relative path to
     * @param file    File to get relative path for
     * @return Relative path to file or null when path can not be retrieved
     */
    @Nullable
    private String getFileRelativePath(Project project, VirtualFile file) {
        VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getContentRootForFile(file);

        if (contentRootForFile == null) {
            return null;
        }

        return VfsUtilCore.getRelativePath(file, contentRootForFile);
    }

    /**
     * Displays notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    private void notify(String title, String body) {
        Notifications.Bus.register(Notifications.SYSTEM_MESSAGES_GROUP_ID, NotificationDisplayType.BALLOON);
        Notification notification = new Notification(
                Notifications.SYSTEM_MESSAGES_GROUP_ID,
                title,
                body,
                NotificationType.INFORMATION
        );

        Notifications.Bus.notify(notification);
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
    public void actionPerformed(AnActionEvent e) {
        if (!this.actionEnabled(e)) {
            return;
        }

        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        String fileContent = Objects.requireNonNull(psiFile).getText();
        String contentHash = DigestUtils.md5Hex(fileContent);

        String filePath = this.getFileRelativePath(e.getProject(), psiFile.getVirtualFile());
        String pathHash = DigestUtils.md5Hex(filePath);

        this.notify(pathHash, contentHash);
    }

    @Override
    public void update(AnActionEvent e) {
        boolean actionEnabled = this.actionEnabled(e);
        e.getPresentation().setEnabled(actionEnabled);
    }
}