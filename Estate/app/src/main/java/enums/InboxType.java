package enums;

/**
 * Created by Paul K Szean on 11/10/2016.
 */

public enum InboxType {
    Select("Select Inbox Type"),
    PropertyComment("Property Comment"),
    Message("Message");


    private String InboxType;

    InboxType(String DealType) {
        this.InboxType = DealType;
    }

    @Override
    public String toString() {
        return InboxType;
    }
}
