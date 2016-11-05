package entities;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class Favourite {

    private String favouriteID;
    private User ownerID;
    private Property propertyID;
    private String createddate;

    public Favourite(String favouriteID, User owner, Property property, String createddate) {

        this.ownerID = owner;
        this.favouriteID = favouriteID;
        this.propertyID = property;
        this.createddate = createddate;
    }

    public Favourite(User owner, Property property) {
        this.ownerID = owner;
        this.propertyID = property;
    }

    public String getFavouriteID() {
        return favouriteID;
    }

    public void setFavouriteID(String favouriteID) {
        this.favouriteID = favouriteID;
    }

    public User getOwner() {
        return ownerID;
    }

    public void setOwnerID(User ownerID) {
        this.ownerID = ownerID;
    }

    public Property getProperty() {
        return propertyID;
    }

    public void setPropertyID(Property propertyID) {
        this.propertyID = propertyID;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }
}

