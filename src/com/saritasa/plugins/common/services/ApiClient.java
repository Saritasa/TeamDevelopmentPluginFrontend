package com.saritasa.plugins.common.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.openapi.components.ServiceManager;
import com.saritasa.plugins.common.exceptions.PluginException;
import com.saritasa.plugins.teamDevelopment.TeamDevelopmentSettings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Team Development service API client.
 */
public class ApiClient {
    /**
     * Returns validated API url root.
     *
     * @param apiUrl API url to validate
     * @return Validated API url
     */
    private String getValidApiUrl(@NotNull String apiUrl) {
        // TODO Improve with UrlImpl
        if (!apiUrl.endsWith("/")) {
            apiUrl = apiUrl.concat("/");
        }

        if (!apiUrl.endsWith("api/")) {
            apiUrl = apiUrl.concat("api/");
        }

        return apiUrl;
    }

    /**
     * Returns root URL of API server.
     *
     * @return URL to server
     */
    @NotNull
    @Contract(pure = true)
    private String getApiRoot() {
        TeamDevelopmentSettings teamDevelopmentService = ServiceManager.getService(TeamDevelopmentSettings.class);
        String apiUrl = teamDevelopmentService.getApiUrl().toLowerCase().trim();
        return this.getValidApiUrl(apiUrl);
    }

    /**
     * Performs HTTP request to Team Development server.
     *
     * @param request Request to perform
     * @return Response entity
     */
    private String doRequest(@NotNull HttpRequestBase request) throws PluginException {
        try {
            HttpClient httpClient = HttpClients.createDefault();

            request.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            request.addHeader("X-Requested-With", "XMLHttpRequest");

            HttpResponse response = httpClient.execute(request);

            HttpEntity httpEntity = response.getEntity();

            if (httpEntity == null) {
                throw new PluginException("Response not received");
            }

            try {
                return EntityUtils.toString(httpEntity);
            } finally {
                httpEntity.getContent().close();
            }
        } catch (IOException exception) {
            throw new PluginException("Remote server communication error: " + exception.getMessage());
        }
    }

    /**
     * Allows to check Team Development service accessibility.
     *
     * @param apiUrl API url to check
     * @throws PluginException In case of communication error
     */
    public void ping(String apiUrl) throws PluginException {
        apiUrl = this.getValidApiUrl(apiUrl);
        // TODO improve with this.get()
        HttpGet httpGet = new HttpGet(apiUrl.concat("ping"));
        String response = this.doRequest(httpGet);
        if (!response.equals("[\"pong\"]")) {
            throw new PluginException("Invalid API ping response");
        }
    }

    /**
     * Performs POST API request.
     *
     * @param endpoint   Endpoint to perform request
     * @param parameters Parameters to send
     * @return Response content
     */
    public String post(String endpoint, ObjectNode parameters) throws PluginException {
        HttpPost httpPost = new HttpPost(this.getApiRoot().concat(endpoint));
        httpPost.setEntity(new StringEntity(parameters.toString(), ContentType.APPLICATION_JSON));

        return this.doRequest(httpPost);
    }
}
