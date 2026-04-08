package parkinglot;

public class HourlyFine implements FineStrategy {

    @Override
    public double calculateFine(long totalHoursParked) {
        if (totalHoursParked <= 24) return 0.0;
        return (totalHoursParked - 24) * 20.0;
    }
}
