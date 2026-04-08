package parkinglot;

public class SUV extends Vehicle {

    public SUV(String plate) {
        super(plate);
    }

    public boolean canPark(SpotType type) {
        return type == SpotType.REGULAR;
    }
}
