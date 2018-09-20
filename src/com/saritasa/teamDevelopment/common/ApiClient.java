package com.saritasa.teamDevelopment.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saritasa.teamDevelopment.common.exceptions.PluginException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
     * Returns root URL of API server.
     *
     * @return URL to server
     */
    @NotNull
    @Contract(pure = true)
    private String getApiRoot() {
        return "http://team-development-web.service.docker/api/";
    }

    /**
     * Performs POST API request.
     *
     * @param endpoint   Endpoint to perform request
     * @param parameters Parameters to send
     * @return Response content
     */
    public String post(String endpoint, ObjectNode parameters) throws PluginException {
        try {
            HttpClient httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(this.getApiRoot().concat(endpoint));
            httpPost.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.setEntity(new StringEntity(parameters.toString(), ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try {
                    return EntityUtils.toString(entity);
                } finally {
                    entity.getContent().close();
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new PluginException("Remote server communication error: " + e.getMessage());
        }
    }
}
