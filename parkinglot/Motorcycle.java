package parkinglot;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plate) {
        super(plate);
    }

    public boolean canPark(SpotType type) {
        return type == SpotType.COMPACT;
    }
}
