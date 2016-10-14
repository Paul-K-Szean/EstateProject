package entities;

/**
 * Created by Paul K Szean on 27/9/2016.
 */

public class User {
    private String userID;
    private String name;
    private String email;
    private String password;
    private String contact;

    public User(String userID, String name, String email, String contact) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public User(String userID, String name, String email, String password, String contact) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


}
