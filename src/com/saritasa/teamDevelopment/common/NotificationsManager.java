package com.saritasa.teamDevelopment.common;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * Notification manager. Can display popup notifications with different critical levels to user.
 */
public class NotificationsManager {
    /**
     * Displays notification.
     *
     * @param title            Notification title
     * @param body             Notification body
     * @param notificationType Type of notification
     */
    private void notify(String title, String body, NotificationType notificationType) {
        Notifications.Bus.register(Notifications.SYSTEM_MESSAGES_GROUP_ID, NotificationDisplayType.BALLOON);
        Notification notification = new Notification(
                Notifications.SYSTEM_MESSAGES_GROUP_ID,
                title,
                body,
                notificationType
        );

        Notifications.Bus.notify(notification);
    }

    /**
     * Displays informational notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public void information(String title, String body) {
        this.notify(title, body, NotificationType.INFORMATION);
    }

    /**
     * Displays warning notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public void warning(String title, String body) {
        this.notify(title, body, NotificationType.WARNING);
    }

    /**
     * Displays error notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public void error(String title, String body) {
        this.notify(title, body, NotificationType.ERROR);
    }
}
