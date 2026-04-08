package parkinglot;

public enum SpotType {
    COMPACT(2),
    REGULAR(5),
    HANDICAPPED(2),
    RESERVED(10);

    public final double rate;

    SpotType(double rate) {
        this.rate = rate;
    }
}
