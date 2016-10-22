package entities;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class Favourite {

    private String favouriteID;
    private String ownerID;
    private String propertyID;
    private String createddate;

    public Favourite(String ownerID, String propertyID) {
        this.ownerID = ownerID;
        this.propertyID = propertyID;
    }

    public Favourite(String favouriteID, String ownerID, String propertyID, String createddate) {
        this.favouriteID = favouriteID;
        this.ownerID = ownerID;
        this.propertyID = propertyID;
        this.createddate = createddate;
    }

    public String getFavouriteID() {
        return favouriteID;
    }

    public void setFavouriteID(String favouriteID) {
        this.favouriteID = favouriteID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }
}

