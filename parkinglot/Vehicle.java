package parkinglot;

import java.time.LocalDateTime;

public abstract class Vehicle {

    protected String plate;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;

    public Vehicle(String plate) {
        this.plate = plate;
        this.entryTime = LocalDateTime.now();
    }

    public abstract boolean canPark(SpotType type);

    public String getPlate() {
        return plate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setExitTime() {
        exitTime = LocalDateTime.now();
    }
}
