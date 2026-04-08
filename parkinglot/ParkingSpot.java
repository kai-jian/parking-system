package parkinglot;

public class ParkingSpot {

    private String id;
    private SpotType type;
    private boolean occupied;
    private Vehicle vehicle;

    public ParkingSpot(String id, SpotType type) {
        this.id = id;
        this.type = type;
        this.occupied = false;
    }

    public boolean canFit(Vehicle v) {
        return !occupied && v.canPark(type);
    }

    public void park(Vehicle v) {
        vehicle = v;
        occupied = true;
    }

    public void leave() {
        vehicle = null;
        occupied = false;
    }

    public String getId() { return id; }
    public SpotType getType() { return type; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getVehicle() { return vehicle; }
}
