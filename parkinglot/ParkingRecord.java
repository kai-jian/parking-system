package parkinglot;

import java.time.LocalDateTime;

public class ParkingRecord {
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final Ticket ticket;
    private final FineStrategy fineStrategyAtEntry;
    private final boolean hasReservation;

    public ParkingRecord(Vehicle vehicle, ParkingSpot spot, Ticket ticket, FineStrategy fineStrategyAtEntry, boolean hasReservation) {
        this.vehicle = vehicle;
        this.spot = spot;
        this.ticket = ticket;
        this.fineStrategyAtEntry = fineStrategyAtEntry;
        this.hasReservation = hasReservation;
    }

    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public Ticket getTicket() { return ticket; }
    public FineStrategy getFineStrategyAtEntry() { return fineStrategyAtEntry; }
    public boolean hasReservation() { return hasReservation; }

    public LocalDateTime getEntryTime() { return vehicle.getEntryTime(); }
}
