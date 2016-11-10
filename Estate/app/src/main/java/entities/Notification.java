package entities;

/**
 * Created by Paul K Szean on 9/11/2016.
 */

public class Notification {
    // Firebase Cloud Message (FCM)s
    private User notifyUser;
    private String notifyFCMToken;
    private String notifyTitle;
    private String notifyMessage;

    public Notification(User notifyUserId, String notifyTitle, String notifyMessage) {
        this.notifyUser = notifyUserId;
        this.notifyTitle = notifyTitle;
        this.notifyMessage = notifyMessage;
    }

    public User getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(User notifyUser) {
        this.notifyUser = notifyUser;
    }

    public String getNotifyFCMToken() {
        return notifyFCMToken;
    }

    public void setNotifyFCMToken(String notifyFCMToken) {
        this.notifyFCMToken = notifyFCMToken;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }
}
