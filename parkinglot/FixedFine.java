package parkinglot;

public class FixedFine implements FineStrategy {

    @Override
    public double calculateFine(long totalHoursParked) {
        return (totalHoursParked > 24) ? 50.0 : 0.0;
    }
}
