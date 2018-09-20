package com.saritasa.teamDevelopment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.psi.PsiFile;
import com.saritasa.teamDevelopment.common.ApiClient;
import com.saritasa.teamDevelopment.common.ProjectEnvironment;
import com.saritasa.teamDevelopment.common.exceptions.PluginException;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File revisions service. Can register new revision of project file in team development service.
 */
class FileRevisionsService {

    private final ApiClient apiClient;
    private final ProjectEnvironment projectEnvironment;

    FileRevisionsService() {
        this.apiClient = new ApiClient();
        this.projectEnvironment = new ProjectEnvironment();
    }

    private List<FileRevisionData> parseFileRevisions(String response) throws PluginException {
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
            throw new PluginException("Can't parse revisoins response: " + e.getMessage());
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
    private FileRevisionData getFileRevisionData(PsiFile psiFile) throws PluginException {
        String fileContent = Objects.requireNonNull(psiFile).getText();
        String contentHash = DigestUtils.md5Hex(fileContent);

        String filePath = this.projectEnvironment.getFileRelativePath(psiFile);
        String pathHash = DigestUtils.md5Hex(filePath);

        String developerName = this.projectEnvironment.getDeveloperName();

        String projectName = Objects.requireNonNull(psiFile.getProject()).getName();
        return new FileRevisionData(projectName, pathHash, contentHash, developerName);
    }

    /**
     * Register new revision in Team Development service.
     *
     * @param psiFile File which revision need to register
     * @return List of revisions of this file from other developers
     * @throws PluginException In case of server communication error
     */
    List<FileRevisionData> registerNewRevision(PsiFile psiFile) throws PluginException {

        FileRevisionData fileRevision = this.getFileRevisionData(psiFile);

        ObjectNode editedFileData = fileRevision.toObjectNode();

        String response = this.apiClient.post("revisions", editedFileData);

        return this.parseFileRevisions(response);
    }
}
