package parkinglot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JPanel {

    private final JLabel revenueLabel = new JLabel();
    private final JLabel occupancyLabel = new JLabel();

    private final DefaultTableModel spotsModel = new DefaultTableModel(
            new Object[]{"Spot ID", "Type", "Rate (RM/hr)", "Status", "Current Plate"}, 0
    );

    private final DefaultTableModel parkedModel = new DefaultTableModel(
            new Object[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Fine Scheme"}, 0
    );

    private final DefaultTableModel finesModel = new DefaultTableModel(
            new Object[]{"Plate", "Outstanding Fine (RM)"}, 0
    );

    private final Timer autoRefreshTimer;

    public AdminPanel(ParkingLot lot) {
        setLayout(new BorderLayout(10, 10));

        // Top: Fine scheme selection (applies to future entries only)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> fine = new JComboBox<>(new String[]{"Fixed", "Progressive", "Hourly"});
        JButton apply = new JButton("Apply Fine Scheme (Future Entries)");

        apply.addActionListener(e -> {
            switch (fine.getSelectedIndex()) {
                case 0 -> lot.setFineStrategy(new FixedFine());
                case 1 -> lot.setFineStrategy(new ProgressiveFine());
                case 2 -> lot.setFineStrategy(new HourlyFine());
            }
            JOptionPane.showMessageDialog(this, "Fine scheme updated (will apply to future entries only).");
            refresh(lot);
        });

        top.add(new JLabel("Fine Scheme:"));
        top.add(fine);
        top.add(apply);

        // Summary section
        JPanel summary = new JPanel(new GridLayout(2, 1));
        summary.add(revenueLabel);
        summary.add(occupancyLabel);

        JPanel header = new JPanel(new BorderLayout());
        header.add(top, BorderLayout.NORTH);
        header.add(summary, BorderLayout.SOUTH);

        // Center: Tabs (required admin views)
        JTable spotsTable = new JTable(spotsModel);
        JTable parkedTable = new JTable(parkedModel);
        JTable finesTable = new JTable(finesModel);

        JTabbedPane centerTabs = new JTabbedPane();
        centerTabs.addTab("All Floors & Spots", new JScrollPane(spotsTable));
        centerTabs.addTab("Vehicles Currently Parked", new JScrollPane(parkedTable));
        centerTabs.addTab("Unpaid Fines", new JScrollPane(finesTable));

        // Bottom: manual refresh (still useful)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh(lot));
        bottom.add(refreshBtn);

        add(header, BorderLayout.NORTH);
        add(centerTabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Initial refresh
        refresh(lot);

        // Auto refresh every 500ms
        autoRefreshTimer = new Timer(500, e -> refresh(lot));
        autoRefreshTimer.start();

        // Stop timer if panel is disposed
        addHierarchyListener(e -> {
            if (!isDisplayable()) {
                autoRefreshTimer.stop();
            } else if (!autoRefreshTimer.isRunning()) {
                autoRefreshTimer.start();
            }
        });
    }

    private void refresh(ParkingLot lot) {
        revenueLabel.setText("Total revenue collected (paid): RM " + String.format("%.2f", lot.getRevenue()));
        occupancyLabel.setText("Occupancy rate: " + String.format("%.2f", lot.getOccupancyRate()) + "%");

        // View all floors and spots (required)
        spotsModel.setRowCount(0);
        for (Floor f : lot.getFloors()) {
            for (ParkingSpot s : f.getSpots()) {
                String status = s.isOccupied() ? "Occupied" : "Available";
                String plate = (s.getVehicle() == null) ? "-" : s.getVehicle().getPlate();
                spotsModel.addRow(new Object[]{
                        s.getId(),
                        String.valueOf(s.getType()),
                        String.format("%.2f", s.getType().rate),
                        status,
                        plate
                });
            }
        }

        // Vehicles currently parked table
        parkedModel.setRowCount(0);
        for (var entry : lot.getActive().entrySet()) {
            String plate = entry.getKey();
            ParkingRecord r = entry.getValue();
            String vType = r.getVehicle().getClass().getSimpleName();
            String spot = r.getSpot().getId() + " (" + r.getSpot().getType() + ")";
            String entryTime = String.valueOf(r.getVehicle().getEntryTime());
            String scheme = (r.getFineStrategyAtEntry() == null)
                    ? "(none)"
                    : r.getFineStrategyAtEntry().getClass().getSimpleName();
            parkedModel.addRow(new Object[]{plate, vType, spot, entryTime, scheme});
        }

        // Unpaid fines table
        finesModel.setRowCount(0);
        for (var e : lot.getUnpaidFines().entrySet()) {
            finesModel.addRow(new Object[]{e.getKey(), String.format("%.2f", e.getValue())});
        }
    }
}
