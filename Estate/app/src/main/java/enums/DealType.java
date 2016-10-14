package enums;

/**
 * Created by Paul K Szean on 11/10/2016.
 */

public enum DealType {

    ForSale("Sale"),
    ForLease("Lease");


    private String DealType;

    DealType(String DealType) {
        this.DealType = DealType;
    }

    @Override
    public String toString() {
        return DealType;
    }
}
