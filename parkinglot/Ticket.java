package parkinglot;

import java.time.LocalDateTime;

public class Ticket {
    private final String ticketId;
    private final String plate;
    private final String spotId;
    private final LocalDateTime entryTime;

    public Ticket(String plate, String spotId, LocalDateTime entryTime) {
        this.plate = plate;
        this.spotId = spotId;
        this.entryTime = entryTime;
        // Recommended format: T-PLATE-TIMESTAMP
        this.ticketId = "T-" + plate + "-" + System.currentTimeMillis();
    }

    public String getTicketId() { return ticketId; }
    public String getPlate() { return plate; }
    public String getSpotId() { return spotId; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketId + "\nPlate: " + plate + "\nSpot: " + spotId + "\nEntry: " + entryTime;
    }
}
