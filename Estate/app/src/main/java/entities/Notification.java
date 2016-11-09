package entities;

/**
 * Created by Paul K Szean on 9/11/2016.
 */

public class Notification {
    public User getNotifyUserId() {
        return notifyUserId;
    }

    public void setNotifyUserId(User notifyUserId) {
        this.notifyUserId = notifyUserId;
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

    public Notification(User notifyUserId, String notifyTitle, String notifyMessage) {
        this.notifyUserId = notifyUserId;
        this.notifyTitle = notifyTitle;
        this.notifyMessage = notifyMessage;
    }

    // Firebase Cloud Message (FCM)s
    private User notifyUserId;
    private String notifyFCMToken;
    private String notifyTitle;
    private String notifyMessage;
}
