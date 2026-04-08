package parkinglot;

import javax.swing.*;
import java.util.*;

public class EntryPanel extends JPanel {

    public EntryPanel(ParkingLot lot) {

        JTextField plate = new JTextField(10);
        JComboBox<String> type = new JComboBox<>(
                new String[]{"Motorcycle", "Car", "SUV", "Handicapped"}
        );
        JCheckBox handicappedCard = new JCheckBox("Handicapped card holder");
        JCheckBox hasReservation = new JCheckBox("Has VIP reservation (for Reserved spot)");
        JButton park = new JButton("Park");

        park.addActionListener(e -> {
            String plateText = plate.getText() == null ? "" : plate.getText().trim();
            if (plateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate number.");
                return;
            }

            Vehicle v;
            switch (type.getSelectedIndex()) {
                case 0 -> v = new Motorcycle(plateText);
                case 1 -> v = new Car(plateText);
                case 2 -> v = new SUV(plateText);
                default -> v = new HandicappedVehicle(plateText, handicappedCard.isSelected());
            }

            List<ParkingSpot> spots = lot.findAvailable(v);
            if (spots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available spot");
                return;
            }

            // Show suitable available spots and let user select (required by spec)
            String[] options = spots.stream()
                    .map(s -> s.getId() + " (" + s.getType() + ", RM " + s.getType().rate + "/hour)")
                    .toArray(String[]::new);

            String chosen = (String) JOptionPane.showInputDialog(
                    this,
                    "Select an available spot:",
                    "Spot Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (chosen == null) return; // cancelled

            ParkingSpot selected = null;
            for (ParkingSpot s : spots) {
                String label = s.getId() + " (" + s.getType() + ", RM " + s.getType().rate + "/hour)";
                if (label.equals(chosen)) {
                    selected = s;
                    break;
                }
            }
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Invalid spot selection.");
                return;
            }

            // Validate reservation rule for Reserved spots
            if (selected.getType() == SpotType.RESERVED && !hasReservation.isSelected()) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "This is a RESERVED spot. You indicated no reservation.\n" +
                                "A fine may be charged at exit. Continue?",
                        "No Reservation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            Ticket ticket = lot.park(v, selected, hasReservation.isSelected());
            JOptionPane.showMessageDialog(this, ticket.toString());
        });

        add(new JLabel("Plate Number"));
        add(plate);
        add(type);
        add(handicappedCard);
        add(hasReservation);
        add(park);
    }
}
