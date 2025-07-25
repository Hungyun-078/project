package method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class VehicleDispatch {

    public static void dispatchAllBikesEvenly() throws SQLException {
        List<String> availableBikes = getAvailableBikes();
        Map<String, List<String>> stationToDocks = getStationToDocks();

        int bikeIndex = 0;


        for (Map.Entry<String, List<String>> entry : stationToDocks.entrySet()) {
            List<String> docks = entry.getValue();
            int bikesAssigned = 0;
            for (int i = 0; i < docks.size() && bikesAssigned < 3; i++) {
                if (bikeIndex < availableBikes.size()) {
                    assignBikeToDock(availableBikes.get(bikeIndex), docks.get(i));
                    bikeIndex++;
                    bikesAssigned++;
                } else {
                    break;
                }
            }
        }


        while (bikeIndex < availableBikes.size()) {
            for (Map.Entry<String, List<String>> entry : stationToDocks.entrySet()) {
                List<String> docks = entry.getValue();
                for (String dock : docks) {
                    if (bikeIndex < availableBikes.size() && getBikeCountAtStation(entry.getKey()) < 6) {
                        assignBikeToDock(availableBikes.get(bikeIndex), dock);
                        bikeIndex++;
                    } else {
                        break;
                    }
                }
                if (bikeIndex >= availableBikes.size()) {
                    break;
                }
            }
        }
    }

    private static int getBikeCountAtStation(String stationUID) throws SQLException {
        int count = 0;
        try (Connection conn = Status_db.getConnection()) {
            String query = "SELECT COUNT(*) AS bikeCount FROM docks WHERE StationUID = ? AND Bike IS NOT NULL";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, stationUID);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt("bikeCount");
                    }
                }
            }
        }
        return count;
    }

    private static List<String> getAvailableBikes() throws SQLException {
        List<String> availableBikes = new ArrayList<>();
        try (Connection conn = Status_db.getConnection()) {
            String query = "SELECT BikeUID FROM bikes WHERE Type = 'Normal'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        availableBikes.add(rs.getString("BikeUID"));
                    }
                }
            }
        }
        return availableBikes;
    }

    private static Map<String, List<String>> getStationToDocks() throws SQLException {
        Map<String, List<String>> stationToDocks = new HashMap<>();
        try (Connection conn = Status_db.getConnection()) {
            String query = "SELECT StationUID, DockUID FROM docks";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String stationUID = rs.getString("StationUID");
                        String dockUID = rs.getString("DockUID");
                        stationToDocks.computeIfAbsent(stationUID, k -> new ArrayList<>()).add(dockUID);
                    }
                }
            }
        }
        return stationToDocks;
    }

    public static void assignBikeToDock(String bikeUID, String dockUID) throws SQLException {
        try (Connection conn = Status_db.getConnection()) {
            String query = "UPDATE docks SET Bike = ? WHERE DockUID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, bikeUID);
                stmt.setString(2, dockUID);
                stmt.executeUpdate();
            }
        }
    }

    public static void removeBikeFromDock(String dockUID) throws SQLException {
        try (Connection conn = Status_db.getConnection()) {
            String query = "UPDATE docks SET Bike = NULL WHERE DockUID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, dockUID);
                stmt.executeUpdate();
            }
        }
    }

    public static void removeAllBikes() throws SQLException {
        Map<String, List<String>> stationToDocks = getStationToDocks();
        for (List<String> docks : stationToDocks.values()) {
            for (String dock : docks) {
                removeBikeFromDock(dock);
            }
        }
    }
}
