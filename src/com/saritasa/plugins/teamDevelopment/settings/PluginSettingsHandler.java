package com.saritasa.plugins.teamDevelopment.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.saritasa.plugins.teamDevelopment.TeamDevelopmentSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Service that can communicate with plugin settings form and with other services.
 */
public class PluginSettingsHandler implements SearchableConfigurable {

    private TeamDevelopmentSettingsForm settingsForm;
    private TeamDevelopmentSettings teamDevelopmentService;

    @Nullable
    @Override
    public JComponent createComponent() {
        this.settingsForm = new TeamDevelopmentSettingsForm();
        this.teamDevelopmentService = ServiceManager.getService(TeamDevelopmentSettings.class);

        this.reset();

        return settingsForm.getSettingsForm();
    }

    /**
     * Releases all allocated resources during displaying settings form.
     */
    @Override
    public void disposeUIResources() {
        this.settingsForm = null;
        this.teamDevelopmentService = null;
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.teamDevelopmentPlugin";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Team Development";
    }

    /**
     * Check whether settings not the same and can be applied.
     *
     * @return True when entered settings are not the same as plugin configuration
     */
    @Override
    public boolean isModified() {
        return !this.teamDevelopmentService.getApiUrl().equals(this.settingsForm.getEnteredApiUrl());
    }

    /**
     * Applies settings from form to plugin services.
     *
     * @throws ConfigurationException In case of settings applying failure
     */
    @Override
    public void apply() throws ConfigurationException {
        this.teamDevelopmentService.setApiUrl(this.settingsForm.getEnteredApiUrl());
    }

    /**
     * Resets newly entered settings on form to it's default state.
     */
    @Override
    public void reset() {
        this.settingsForm.hideConnectionIndicator();
        this.settingsForm.setEnteredApiUrl(this.teamDevelopmentService.getApiUrl());
    }
}
