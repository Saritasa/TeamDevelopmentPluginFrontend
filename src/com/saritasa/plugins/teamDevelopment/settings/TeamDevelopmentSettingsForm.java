package com.saritasa.plugins.teamDevelopment.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.JBColor;
import com.saritasa.plugins.common.exceptions.PluginException;
import com.saritasa.plugins.common.services.ApiClient;
import com.saritasa.plugins.common.services.NotificationsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Team Development plugin settings form. Allows to manipulate service form.
 */
public class TeamDevelopmentSettingsForm {
    private JTextField apiUrlTextField;
    private JButton testConnectionButton;
    private JLabel connectionTestLabel;
    private JPanel pluginForm;

    /**
     * Team Development plugin settings form. Allows to manipulate service form.
     */
    TeamDevelopmentSettingsForm() {
        hideConnectionIndicator();
        setTestConnectionClickHandler();
        setApiUrlFieldChangeHandler();
    }

    /**
     * Test connection button click handler. Allows to test entered API url.
     */
    private void setTestConnectionClickHandler() {
        testConnectionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ApiClient apiClient = ServiceManager.getService(ApiClient.class);

                setConnectionIndicatorState(true, "Connecting...", JBColor.DARK_GRAY);

                Color color;
                String connectionIndicatorLabel;

                try {
                    apiClient.ping(getEnteredApiUrl());

                    color = JBColor.GREEN.darker().darker();
                    connectionIndicatorLabel = "Connection successful";
                } catch (PluginException exception) {
                    color = JBColor.RED.darker();
                    connectionIndicatorLabel = "Connection unsuccessful. See IDE Events log";

                    NotificationsService notificationsService = ServiceManager.getService(NotificationsService.class);
                    notificationsService.information("Test connection issue", exception.getMessage());
                }

                setConnectionIndicatorState(true, connectionIndicatorLabel, color);
            }
        });
    }

    /**
     * API url input text change handler.
     */
    private void setApiUrlFieldChangeHandler() {
        apiUrlTextField.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                hideConnectionIndicator();
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {

            }
        });
    }

    /**
     * Hides test connection indicator from settings form.
     */
    void hideConnectionIndicator() {
        setConnectionIndicatorState(false, connectionTestLabel.getText(), connectionTestLabel.getForeground());
    }

    /**
     * Sets API test connection indicator state.
     *
     * @param visibility Whether indicator should be visible or not
     * @param label      Indicator message
     * @param color      Indicator color
     */
    private void setConnectionIndicatorState(boolean visibility, String label, Color color) {
        connectionTestLabel.setVisible(visibility);
        connectionTestLabel.setText(label);
        connectionTestLabel.setForeground(color);
    }

    /**
     * Returns element with settings form content.
     *
     * @return Settings form panel
     */
    JPanel getSettingsForm() {
        return pluginForm;
    }

    /**
     * Returns API server url from settings form.
     *
     * @return API server url
     */
    String getEnteredApiUrl() {
        return this.apiUrlTextField.getText().trim().toLowerCase();
    }

    /**
     * Sets API server url to form.
     *
     * @param apiUrl API server url
     */
    void setEnteredApiUrl(String apiUrl) {
        this.apiUrlTextField.setText(apiUrl.trim().toLowerCase());
    }
}
