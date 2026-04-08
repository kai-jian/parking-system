package parkinglot;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ReportPanel extends JPanel {

    private final JTextArea output = new JTextArea(20, 70);

    public ReportPanel(ParkingLot lot) {
        setLayout(new BorderLayout(10, 10));
        output.setEditable(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton vehiclesBtn = new JButton("Current Vehicles");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton revenueBtn = new JButton("Revenue Report");
        JButton finesBtn = new JButton("Fine Report");

        vehiclesBtn.addActionListener(e -> output.setText(buildCurrentVehiclesReport(lot)));
        occupancyBtn.addActionListener(e -> output.setText(buildOccupancyReport(lot)));
        revenueBtn.addActionListener(e -> output.setText(buildRevenueReport(lot)));
        finesBtn.addActionListener(e -> output.setText(buildFineReport(lot)));

        buttons.add(vehiclesBtn);
        buttons.add(occupancyBtn);
        buttons.add(revenueBtn);
        buttons.add(finesBtn);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        // default view
        output.setText(buildCurrentVehiclesReport(lot));
    }

    private String buildCurrentVehiclesReport(ParkingLot lot) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Vehicles Currently Parked ===\n");
        if (lot.getActive().isEmpty()) {
            sb.append("(none)\n");
            return sb.toString();
        }
        for (var e : lot.getActive().entrySet()) {
            ParkingRecord r = e.getValue();
            sb.append("Plate: ").append(e.getKey())
                    .append(" | Type: ").append(r.getVehicle().getClass().getSimpleName())
                    .append(" | Spot: ").append(r.getSpot().getId())
                    .append(" (" + r.getSpot().getType() + ")")
                    .append(" | Entry: ").append(r.getVehicle().getEntryTime())
                    .append("\n");
        }
        return sb.toString();
    }

    private String buildRevenueReport(ParkingLot lot) {
        return "=== Revenue Report ===\n" +
                "Total revenue collected (paid): RM " + String.format("%.2f", lot.getRevenue()) + "\n";
    }

    private String buildFineReport(ParkingLot lot) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Fine Report (Outstanding Unpaid Fines) ===\n");
        if (lot.getUnpaidFines().isEmpty()) {
            sb.append("(none)\n");
            return sb.toString();
        }
        for (var e : lot.getUnpaidFines().entrySet()) {
            sb.append("Plate: ").append(e.getKey())
                    .append(" | Outstanding: RM ")
                    .append(String.format("%.2f", e.getValue()))
                    .append("\n");
        }
        return sb.toString();
    }

    private String buildOccupancyReport(ParkingLot lot) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Occupancy Report ===\n");
        sb.append("Overall occupancy: ").append(String.format("%.2f", lot.getOccupancyRate())).append("%\n\n");

        // By floor
        int floorIdx = 1;
        for (Floor f : lot.getFloors()) {
            int total = 0, occ = 0;
            Map<SpotType, int[]> byType = new EnumMap<>(SpotType.class);
            for (SpotType t : SpotType.values()) byType.put(t, new int[]{0,0});

            for (ParkingSpot s : f.getSpots()) {
                total++;
                if (s.isOccupied()) occ++;
                int[] bucket = byType.get(s.getType());
                bucket[0]++; // total
                if (s.isOccupied()) bucket[1]++; // occupied
            }
            sb.append("Floor ").append(floorIdx).append(": ")
                    .append(occ).append("/").append(total)
                    .append(" occupied (")
                    .append(total == 0 ? "0.00" : String.format("%.2f", occ * 100.0 / total))
                    .append("%)\n");

            for (var entry : byType.entrySet()) {
                SpotType t = entry.getKey();
                int[] v = entry.getValue();
                sb.append("  - ").append(t).append(": ")
                        .append(v[1]).append("/").append(v[0]).append(" occupied\n");
            }
            sb.append("\n");
            floorIdx++;
        }
        return sb.toString();
    }
}
