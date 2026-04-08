package parkinglot;

public class ProgressiveFine implements FineStrategy {

    /**
     * Progressive scheme (cumulative) based on TOTAL hours parked:
     * - <= 24h: RM 0
     * - >24h up to 48h: RM 50
     * - >48h up to 72h: RM 50 + RM 100 = RM 150
     * - >72h: RM 50 + RM 100 + RM 150 + RM 200 = RM 500
     */
    @Override
    public double calculateFine(long totalHoursParked) {
        if (totalHoursParked <= 24) return 0.0;
        if (totalHoursParked <= 48) return 50.0;
        if (totalHoursParked <= 72) return 150.0;
        return 500.0;
    }
}
