package enums;

/**
 * Created by Paul K Szean on 11/10/2016.
 */

public enum PropertyStatus {
    Select("Select Property Status"),
    Open("open"),
    Close("closed");


    private String PropertyStatus;

    PropertyStatus(String PropertyStatus) {
        this.PropertyStatus = PropertyStatus;
    }

    @Override
    public String toString() {
        return PropertyStatus;
    }
}
