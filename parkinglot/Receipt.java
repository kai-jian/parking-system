package parkinglot;

import java.time.LocalDateTime;

public class Receipt {
    private final String plate;
    private final String spotId;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final long hours;
    private final double hourlyRate;
    private final double parkingFee;
    private final double currentFine;
    private final double previousUnpaidFine;
    private final double totalDue;
    private final PaymentMethod paymentMethod;
    private final double amountPaid;
    private final double remainingBalance;

    public Receipt(
            String plate,
            String spotId,
            LocalDateTime entryTime,
            LocalDateTime exitTime,
            long hours,
            double hourlyRate,
            double parkingFee,
            double currentFine,
            double previousUnpaidFine,
            PaymentMethod paymentMethod,
            double amountPaid
    ) {
        this.plate = plate;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.currentFine = currentFine;
        this.previousUnpaidFine = previousUnpaidFine;
        this.totalDue = parkingFee + currentFine + previousUnpaidFine;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.remainingBalance = Math.max(0, totalDue - amountPaid);
    }

    public double getTotalDue() { return totalDue; }
    public double getRemainingBalance() { return remainingBalance; }

    @Override
    public String toString() {
        return "===== Exit Receipt =====\n" +
                "Plate: " + plate + "\n" +
                "Spot: " + spotId + "\n" +
                "Entry time: " + entryTime + "\n" +
                "Exit time: " + exitTime + "\n" +
                "Duration (hours): " + hours + "\n\n" +
                "Parking fee: " + hours + " x RM " + String.format("%.2f", hourlyRate) +
                " = RM " + String.format("%.2f", parkingFee) + "\n" +
                "Current fine: RM " + String.format("%.2f", currentFine) + "\n" +
                "Previous unpaid fines: RM " + String.format("%.2f", previousUnpaidFine) + "\n" +
                "------------------------\n" +
                "Total due: RM " + String.format("%.2f", totalDue) + "\n" +
                "Paid by: " + paymentMethod + "\n" +
                "Amount paid: RM " + String.format("%.2f", amountPaid) + "\n" +
                "Remaining balance: RM " + String.format("%.2f", remainingBalance) + "\n" +
                "========================";
    }
}
