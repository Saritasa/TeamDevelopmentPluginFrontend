package com.saritasa.plugins.common.services;

import com.intellij.notification.*;

/**
 * Notification manager. Can display popup notifications with different importance levels to user.
 */
public class NotificationsService {

    public NotificationsService() {
        Notifications.Bus.register(Notifications.SYSTEM_MESSAGES_GROUP_ID, NotificationDisplayType.BALLOON);
    }

    /**
     * Displays notification.
     *
     * @param title            Notification title
     * @param body             Notification body
     * @param notificationType Type of notification
     */
    private Notification notify(String title, String body, NotificationType notificationType) {
        return notify(title, body, notificationType, null);
    }

    /**
     * Displays notification.
     *
     * @param title            Notification title
     * @param body             Notification body
     * @param notificationType Type of notification
     */
    private Notification notify(String title, String body, NotificationType notificationType, NotificationListener notificationListener) {
        Notification notification = new Notification(
                Notifications.SYSTEM_MESSAGES_GROUP_ID,
                title,
                body,
                notificationType,
                notificationListener
        );

        Notifications.Bus.notify(notification);

        return notification;
    }

    /**
     * Displays informational notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification information(String title, String body) {
        return information(title, body, null);
    }

    /**
     * Displays informational notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification information(String title, String body, NotificationListener notificationListener) {
        return this.notify(title, body, NotificationType.INFORMATION, notificationListener);
    }

    /**
     * Displays warning notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification warning(String title, String body) {
        return warning(title, body, null);
    }

    /**
     * Displays warning notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification warning(String title, String body, NotificationListener notificationListener) {
        return this.notify(title, body, NotificationType.WARNING, notificationListener);
    }

    /**
     * Displays error notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification error(String title, String body) {
        return error(title, body, null);
    }

    /**
     * Displays error notification.
     *
     * @param title Notification title
     * @param body  Notification body
     */
    public Notification error(String title, String body, NotificationListener notificationListener) {
        return this.notify(title, body, NotificationType.ERROR, notificationListener);
    }
}
