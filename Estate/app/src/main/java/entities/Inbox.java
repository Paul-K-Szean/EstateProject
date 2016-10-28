package entities;

/**
 * Created by Paul K Szean on 22/10/2016.
 */

public class Inbox {

    private String inboxID;
    private User sender;
    private String recipientID;
    private String inboxtype;
    private String inboxtitle;
    private String inboxmessage;
    private String createddate;

    // for creating
    public Inbox(User sender, String recipientID, String inboxtype, String inboxtitle, String inboxmessage) {
        this.sender = sender;
        this.recipientID = recipientID;
        this.inboxtype = inboxtype;
        this.inboxtitle = inboxtitle;
        this.inboxmessage = inboxmessage;
    }

    // for retrieving
    public Inbox(String recipientID, String inboxtype) {
        this.recipientID = recipientID;
        this.inboxtype = inboxtype;
    }

    // for displaying
    public Inbox(String inboxID, User sender, String recipientID, String inboxtype, String inboxtitle, String inboxmessage, String createddate) {
        this.inboxID = inboxID;
        this.sender = sender;
        this.recipientID = recipientID;
        this.inboxtype = inboxtype;
        this.inboxtitle = inboxtitle;
        this.inboxmessage = inboxmessage;
        this.createddate = createddate;
    }

    public String getInboxID() {
        return inboxID;
    }

    public void setInboxID(String inboxID) {
        this.inboxID = inboxID;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public String getInboxtype() {
        return inboxtype;
    }

    public void setInboxtype(String inboxtype) {
        this.inboxtype = inboxtype;
    }

    public String getInboxtitle() {
        return inboxtitle;
    }

    public void setInboxtitle(String inboxtitle) {
        this.inboxtitle = inboxtitle;
    }

    public String getInboxmessage() {
        return inboxmessage;
    }

    public void setInboxmessage(String inboxmessage) {
        this.inboxmessage = inboxmessage;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }
}
