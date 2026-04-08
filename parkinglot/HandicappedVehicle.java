package parkinglot;

public class HandicappedVehicle extends Vehicle {

    private final boolean cardHolder;

    public HandicappedVehicle(String plate, boolean cardHolder) {
        super(plate);
        this.cardHolder = cardHolder;
    }

    public boolean canPark(SpotType type) {
        return true;
    }

    public boolean isCardHolder() {
        return cardHolder;
    }
}
