package parkinglot;

import javax.swing.*;

public class ExitPanel extends JPanel {

    public ExitPanel(ParkingLot lot) {

        JTextField plate = new JTextField(10);
        JComboBox<PaymentMethod> method = new JComboBox<>(PaymentMethod.values());
        JTextField paid = new JTextField(10);
        JButton showReceipt = new JButton("Show Receipt");
        JButton exit = new JButton("Exit & Pay");

        showReceipt.addActionListener(e -> {
            String p = plate.getText() == null ? "" : plate.getText().trim();
            if (p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate number.");
                return;
            }
            try {
                Receipt preview = lot.previewReceipt(p, (PaymentMethod) method.getSelectedItem());
                JOptionPane.showMessageDialog(this, preview.toString());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        exit.addActionListener(e -> {
            String p = plate.getText() == null ? "" : plate.getText().trim();
            if (p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate number.");
                return;
            }
            double amountPaid;
            try {
                String t = paid.getText() == null ? "" : paid.getText().trim();
                amountPaid = t.isEmpty() ? 0.0 : Double.parseDouble(t);
                if (amountPaid < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount paid. Example: 20 or 20.50");
                return;
            }

            try {
                Receipt receipt = lot.exitAndPay(p, (PaymentMethod) method.getSelectedItem(), amountPaid);
                JOptionPane.showMessageDialog(this, receipt.toString());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        add(new JLabel("Plate Number"));
        add(plate);
        add(new JLabel("Payment Method"));
        add(method);
        add(showReceipt);
        add(new JLabel("Amount Paid (RM)"));
        add(paid);
        add(exit);
    }
}
