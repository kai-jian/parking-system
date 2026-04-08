package parkinglot;

public class Car extends Vehicle {

    public Car(String plate) {
        super(plate);
    }

    public boolean canPark(SpotType type) {
        return type == SpotType.COMPACT || type == SpotType.REGULAR;
    }
}
