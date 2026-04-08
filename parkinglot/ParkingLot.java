package parkinglot;

import java.time.*;
import java.util.*;

/**
 * Singleton (allowed design pattern in the rubric list):
 * The whole GUI must share ONE ParkingLot state (active vehicles, revenue, unpaid fines, etc.).
 */
public class ParkingLot {

    private static ParkingLot instance;

    private List<Floor> floors = new ArrayList<>();
    private FineStrategy fineStrategy;
    // Active parking is tracked per plate so we can compute receipts and fines.
    private final Map<String, ParkingRecord> active = new HashMap<>();
    // Unpaid fines are linked to license plate (required by spec)
    private final Map<String, Double> unpaidFines = new HashMap<>();
    private double revenue = 0;

    private ParkingLot(int floorsCount, int rows, int cols) {
        for (int i = 1; i <= floorsCount; i++) {
            floors.add(new Floor(i, rows, cols));
        }
    }

    /**
     * Get (or create) the single ParkingLot instance.
     * Call this once in MainApp during startup.
     */
    public static synchronized ParkingLot getInstance(int floorsCount, int rows, int cols) {
        if (instance == null) {
            instance = new ParkingLot(floorsCount, rows, cols);
        }
        return instance;
    }

    /**
     * Get the already-created ParkingLot instance.
     */
    public static ParkingLot getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ParkingLot not initialized. Call getInstance(floors, rows, cols) first.");
        }
        return instance;
    }

    public void setFineStrategy(FineStrategy fs) {
        fineStrategy = fs;
    }

    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<ParkingSpot> findAvailable(Vehicle v) {
        List<ParkingSpot> list = new ArrayList<>();
        for (Floor f : floors)
            for (ParkingSpot s : f.getSpots())
                if (s.canFit(v)) list.add(s);
        return list;
    }

    /**
     * Park a vehicle in a selected spot.
     * - Records entry time (stored in Vehicle)
     * - Generates a ticket (T-PLATE-TIMESTAMP)
     * - Captures the current fine scheme so that changes apply to FUTURE entries only
     */
    public Ticket park(Vehicle v, ParkingSpot s, boolean hasReservation) {
        if (v == null || s == null) {
            throw new IllegalArgumentException("Vehicle and spot must not be null");
        }
        if (!s.canFit(v)) {
            throw new IllegalArgumentException("Selected spot is not suitable or already occupied");
        }
        s.park(v);
        Ticket ticket = new Ticket(v.getPlate(), s.getId(), v.getEntryTime());
        ParkingRecord record = new ParkingRecord(v, s, ticket, fineStrategy, hasReservation);
        active.put(v.getPlate(), record);
        return ticket;
    }

    // Backward-compatible helper (kept so old calls won't break)
    public void park(Vehicle v, ParkingSpot s) {
        park(v, s, true);
    }

    /**
     * Exit workflow:
     * - Locate vehicle by plate
     * - Calculate duration (ceiling to nearest hour)
     * - Calculate parking fee (hours x hourly rate)
     * - Add current fine (overstay + reserved misuse)
     * - Add previous unpaid fines
     * - Accept payment and generate receipt
     * - Remaining balance becomes unpaid fine for future visits
     */
    public Receipt exitAndPay(String plate, PaymentMethod method, double amountPaid) {
        String key = plate == null ? "" : plate.trim();
        ParkingRecord record = active.get(key);
        if (record == null) {
            throw new IllegalArgumentException("No active vehicle found for plate: " + key);
        }

        ParkingSpot s = record.getSpot();
        Vehicle v = record.getVehicle();
        v.setExitTime();
        LocalDateTime exitTime = LocalDateTime.now();

        long minutes = Duration.between(v.getEntryTime(), exitTime).toMinutes();
        long hours = (minutes + 59) / 60; // ceiling rounding
        if (hours <= 0) hours = 1;

        double hourlyRate = computeHourlyRate(v, s);
        double parkingFee = hours * hourlyRate;

        // Fine 1: Overstay >24h (using the fine scheme captured at entry)
        double currentFine = 0;
        if (hours > 24) {
            FineStrategy schemeAtEntry = record.getFineStrategyAtEntry();
            if (schemeAtEntry != null) {
                currentFine += schemeAtEntry.calculateFine(hours);
            }
        }

        // Fine 2: Reserved spot without reservation (flat RM 50)
        if (s.getType() == SpotType.RESERVED && !record.hasReservation()) {
            currentFine += 50;
        }

        double previousUnpaid = unpaidFines.getOrDefault(key, 0.0);

        Receipt receipt = new Receipt(
                key,
                s.getId(),
                v.getEntryTime(),
                exitTime,
                hours,
                hourlyRate,
                parkingFee,
                currentFine,
                previousUnpaid,
                method,
                amountPaid
        );

        // Revenue: only count what was actually paid now
        double paidNow = Math.max(0, amountPaid);
        revenue += paidNow;

        // Remaining balance becomes unpaid fine for next visit
        if (receipt.getRemainingBalance() > 0) {
            unpaidFines.put(key, receipt.getRemainingBalance());
        } else {
            unpaidFines.remove(key);
        }

        // Release spot
        s.leave();
        active.remove(key);

        return receipt;
    }

    /**
     * Preview the bill/receipt WITHOUT changing system state.
     * This allows the user to see the total due before clicking Exit & Pay.
     */
    public Receipt previewReceipt(String plate, PaymentMethod method) {
        String key = plate == null ? "" : plate.trim();
        ParkingRecord record = active.get(key);
        if (record == null) {
            throw new IllegalArgumentException("No active vehicle found for plate: " + key);
        }

        ParkingSpot s = record.getSpot();
        Vehicle v = record.getVehicle();

        LocalDateTime exitTime = LocalDateTime.now();

        long minutes = Duration.between(v.getEntryTime(), exitTime).toMinutes();
        long hours = (minutes + 59) / 60; // ceiling rounding
        if (hours <= 0) hours = 1;

        double hourlyRate = computeHourlyRate(v, s);
        double parkingFee = hours * hourlyRate;

        // Fine 1: Overstay >24h (using the fine scheme captured at entry)
        double currentFine = 0;
        if (hours > 24) {
            FineStrategy schemeAtEntry = record.getFineStrategyAtEntry();
            if (schemeAtEntry != null) {
                currentFine += schemeAtEntry.calculateFine(hours);
            }
        }

        // Fine 2: Reserved spot without reservation (flat RM 50)
        if (s.getType() == SpotType.RESERVED && !record.hasReservation()) {
            currentFine += 50;
        }

        double previousUnpaid = unpaidFines.getOrDefault(key, 0.0);

        // amountPaid = 0 because this is just a preview
        return new Receipt(
                key,
                s.getId(),
                v.getEntryTime(),
                exitTime,
                hours,
                hourlyRate,
                parkingFee,
                currentFine,
                previousUnpaid,
                method,
                0.0
        );
    }

    // Backward-compatible old behavior
    public double exit(String plate) {
        Receipt r = exitAndPay(plate, PaymentMethod.CASH, Double.POSITIVE_INFINITY);
        return r.getTotalDue();
    }

    private double computeHourlyRate(Vehicle v, ParkingSpot spot) {
        // Special handicapped rules:
        // - Handicapped card holder vehicle: discounted RM2/hour in ANY spot
        // - FREE only if handicapped card holder vehicle parks in handicapped spot
        if (v instanceof HandicappedVehicle hv && hv.isCardHolder()) {
            return (spot.getType() == SpotType.HANDICAPPED) ? 0.0 : 2.0;
        }
        return spot.getType().rate;
    }

    public double getRevenue() {
        return revenue;
    }

    public Map<String, ParkingRecord> getActive() { return active; }

    public Map<String, Double> getUnpaidFines() { return unpaidFines; }

    public double getOccupancyRate() {
        int total = 0;
        int occupied = 0;
        for (Floor f : floors) {
            for (ParkingSpot s : f.getSpots()) {
                total++;
                if (s.isOccupied()) occupied++;
            }
        }
        return total == 0 ? 0 : (occupied * 100.0 / total);
    }
}
