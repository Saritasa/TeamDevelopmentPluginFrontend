package com.saritasa.teamDevelopment.common;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.saritasa.teamDevelopment.common.exceptions.PluginException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Project environment helper. Allows to retrieve developer name and relative path to files.
 */
public class ProjectEnvironment {
    /**
     * Cached retrieved user name property.
     */
    private static String userName = "";

    /**
     * Allows to retrieve user name of current project.
     *
     * @return Developer name
     */
    public String getDeveloperName() throws PluginException {
        if (userName.isEmpty()) {
            String command = "git config user.name";
            try {
                Process process = Runtime.getRuntime().exec(command);
                if (process.waitFor() != 0) {
                    return null;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = "";

                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    output = output.concat(line);
                }

                userName = output.trim();

                if (userName.isEmpty()) {
                    throw new PluginException("Can't get developer name. Please check git user setting");
                }
            } catch (InterruptedException | IOException e) {
                throw new PluginException("Can't get developer name with `" + command + "` command: " + e.getMessage());
            }
        }

        return userName;
    }

    /**
     * Returns relative path of the given file.
     *
     * @param psiFile File to find relative project path for
     * @return Relative path to file or null when path can not be retrieved
     */
    @Nullable
    public String getFileRelativePath(@NotNull PsiFile psiFile) {
        VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(psiFile.getProject())
                .getContentRootForFile(psiFile.getVirtualFile());

        if (contentRootForFile == null) {
            return null;
        }

        return VfsUtilCore.getRelativePath(psiFile.getVirtualFile(), contentRootForFile);
    }

}
