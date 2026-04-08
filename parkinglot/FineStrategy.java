package parkinglot;

public interface FineStrategy {
    /**
     * Calculate the overstay fine based on TOTAL parking duration in hours.
     * If hours <= 24, fine should be 0.
     */
    double calculateFine(long totalHoursParked);
}
