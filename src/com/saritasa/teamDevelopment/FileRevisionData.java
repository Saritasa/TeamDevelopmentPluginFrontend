package com.saritasa.teamDevelopment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Revision information of project file.
 */
class FileRevisionData {
    private String projectName;
    private String pathHash;
    private String contentHash;
    private String developerName;

    FileRevisionData(String projectName, String pathHash, String contentHash, String developerName) {
        this.projectName = projectName;
        this.pathHash = pathHash;
        this.contentHash = contentHash;
        this.developerName = developerName;
    }

    String getProjectName() {
        return projectName;
    }

    String getPathHash() {
        return pathHash;
    }

    String getContentHash() {
        return contentHash;
    }

    String getDeveloperName() {
        return developerName;
    }

    ObjectNode toObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("projectName", projectName);
        node.put("developerName", developerName);
        node.put("pathHash", pathHash);
        node.put("contentHash", contentHash);

        return node;
    }
}
