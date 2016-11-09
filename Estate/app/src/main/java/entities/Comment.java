package entities;

/**
 * Created by Paul K Szean on 22/10/2016.
 */

public class Comment {

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
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

    public String getCommenttitle() {
        return commenttitle;
    }

    public void setCommenttitle(String commenttitle) {
        this.commenttitle = commenttitle;
    }

    public String getCommentmessage() {
        return commentmessage;
    }

    public void setCommentmessage(String commentmessage) {
        this.commentmessage = commentmessage;
    }

    public String getCommentstatus() {
        return commentstatus;
    }

    public void setCommentstatus(String commentstatus) {
        this.commentstatus = commentstatus;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    // create comment
    public Comment(User sender, String recipientID, String commenttitle, String commentmessage, String status) {
        this.sender = sender;
        this.recipientID = recipientID;
        this.commenttitle = commenttitle;
        this.commentmessage = commentmessage;
        this.commentstatus = status;
    }

    // for display
    public Comment(String commentID, User sender, String recipientID, String commenttitle, String commentmessage, String status, String createddate) {
        this.commentID = commentID;
        this.sender = sender;
        this.recipientID = recipientID;
        this.commenttitle = commenttitle;
        this.commentmessage = commentmessage;
        this.commentstatus = status;
        this.createddate = createddate;
    }

    private String commentID;
    private User sender;
    private String recipientID;
    private String commenttitle;
    private String commentmessage;
    private String commentstatus;
    private String createddate;


}
