package com.saritasa.plugins.teamDevelopment.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Revision information of project file.
 */
public class FileRevisionData {
    private String projectName;
    private String pathHash;
    private String contentHash;
    private String developerName;

    public FileRevisionData(String projectName, String pathHash, String contentHash, String developerName) {
        this.projectName = projectName;
        this.pathHash = pathHash;
        this.contentHash = contentHash;
        this.developerName = developerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPathHash() {
        return pathHash;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public ObjectNode toObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("projectName", projectName);
        node.put("developerName", developerName);
        node.put("pathHash", pathHash);
        node.put("contentHash", contentHash);

        return node;
    }
}
