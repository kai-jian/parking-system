package parkinglot;

import javax.swing.*;

public class MainApp extends JFrame {

    private final ParkingLot parkingLot;

    public MainApp() {
        // Singleton initialization: create the ONE ParkingLot instance shared by all tabs.
        parkingLot = ParkingLot.getInstance(5, 3, 10);
        parkingLot.setFineStrategy(new FixedFine());

        setTitle("University Parking Lot System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Entry", new EntryPanel(parkingLot));
        tabs.add("Exit", new ExitPanel(parkingLot));
        tabs.add("Admin", new AdminPanel(parkingLot));
        tabs.add("Reports", new ReportPanel(parkingLot));

        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
