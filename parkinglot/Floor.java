package parkinglot;

import java.util.*;

public class Floor {

    private List<ParkingSpot> spots = new ArrayList<>();

    public Floor(int floorNo, int rows, int cols) {
        // Create spots arranged by rows and columns.
        // Spot ID format required by spec: F1-R1-S1
        int idx = 0;
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                // Cycle through types to populate a mix of spot types.
                SpotType type = SpotType.values()[idx % SpotType.values().length];
                String id = "F" + floorNo + "-R" + r + "-S" + c;
                spots.add(new ParkingSpot(id, type));
                idx++;
            }
        }
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}
