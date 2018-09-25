package com.saritasa.plugins.teamDevelopment.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.saritasa.plugins.common.exceptions.PluginException;
import com.saritasa.plugins.common.services.ApiClient;
import com.saritasa.plugins.common.services.ProjectEnvironmentService;
import com.saritasa.plugins.teamDevelopment.dto.FileRevisionData;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File revisions service. Can register new revision of project file in team development service.
 */
public class FileRevisionsService {

    private Project project;

    public FileRevisionsService(Project project) {
        this.project = project;
    }

    /**
     * Parses API response into list of file revisions.
     *
     * @param response String response from remote service.
     * @return List of file revisions
     * @throws PluginException When response has not a valid format
     */
    private List<FileRevisionData> parseFileRevisionsResponse(String response) throws PluginException {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray revisionsArray = jsonObject.getJSONArray("results");

            List<FileRevisionData> revisionsList = new ArrayList<>();
            FileRevisionData fileRevisionData;
            for (int i = 0; i < revisionsArray.length(); i++) {
                fileRevisionData = new FileRevisionData(
                        revisionsArray.getJSONObject(i).getString("projectName"),
                        revisionsArray.getJSONObject(i).getString("pathHash"),
                        revisionsArray.getJSONObject(i).getString("contentHash"),
                        revisionsArray.getJSONObject(i).getString("developerName")
                );
                revisionsList.add(fileRevisionData);
            }

            return revisionsList;
        } catch (Throwable e) {
            throw new PluginException("Can't parse revisions response: " + e.getMessage());
        }
    }

    /**
     * Retrieves file revision data from project file.
     *
     * @param psiFile Project to retrieve file revision details
     * @return File revision details
     * @throws PluginException When developer name not retrieved
     */
    @NotNull
    private FileRevisionData getFileRevisionData(@NotNull PsiFile psiFile) throws PluginException {
        ProjectEnvironmentService projectEnvironmentService = ServiceManager.getService(this.project, ProjectEnvironmentService.class);
        String fileContent = Objects.requireNonNull(psiFile).getText();
        String contentHash = DigestUtils.md5Hex(fileContent);

        String filePath = projectEnvironmentService.getFileRelativePath(psiFile);
        String pathHash = DigestUtils.md5Hex(filePath);

        String developerName = projectEnvironmentService.getDeveloperName();

        String projectName = this.project.getName();
        return new FileRevisionData(projectName, pathHash, contentHash, developerName);
    }

    /**
     * Register new revision in Team Development service.
     *
     * @param psiFile File which revision need to register
     * @return List of revisions of this file from other developers
     * @throws PluginException In case of server communication error
     */
    public List<FileRevisionData> registerNewRevision(PsiFile psiFile) throws PluginException {
        ApiClient apiClient = ServiceManager.getService(ApiClient.class);

        FileRevisionData fileRevision = this.getFileRevisionData(psiFile);

        ObjectNode editedFileData = fileRevision.toObjectNode();

        String response = apiClient.post("revisions", editedFileData);

        return this.parseFileRevisionsResponse(response);
    }
}
