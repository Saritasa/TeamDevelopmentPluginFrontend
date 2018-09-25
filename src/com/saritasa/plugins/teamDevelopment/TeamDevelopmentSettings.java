package com.saritasa.plugins.teamDevelopment;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(
        name = "TeamDevelopmentPlugin",
        storages = {
                @Storage("TeamDevelopmentPlugin.xml"),
        }
)
public class TeamDevelopmentSettings implements PersistentStateComponent<String> {
    private String apiUrl = "";

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getState() {
        return this.getApiUrl();
    }

    public void loadState(@NotNull String state) {
        this.setApiUrl(state);
    }
}
