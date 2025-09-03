import java.sql.*;
import java.util.*;

public class TestConnection{
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/cab_booking";
    static final String DB_USER = "root";
    static final String DB_PASS = "YOUR_DB_PASSWORD_HERE"; // try workin with your sql pass

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("   Welcome to SAN-CABS!");
        System.out.println("================================");
        System.out.println("Press Enter to continue...");
        sc.nextLine();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            while (true) {
                System.out.println("\n=== Cab Booking Menu ===");
                System.out.println("1. Book a Cab");
                System.out.println("2. View Booking History");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1 -> bookCab(conn, sc);
                    case 2 -> viewBookingHistory(conn);
                    case 3 -> {
                        System.out.println("Thank you for using SAN-CABS!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void bookCab(Connection conn, Scanner sc) throws SQLException {

        System.out.print("Enter number of passengers: ");
        int passengerCount = Integer.parseInt(sc.nextLine());


        String preferredType = (passengerCount <= 4) ? "Sedan" : "SUV";

        String citySQL = "SELECT DISTINCT location FROM cabs ORDER BY location";
        List<String> cities = new ArrayList<>();
        try (Statement cityStmt = conn.createStatement();
             ResultSet cityRs = cityStmt.executeQuery(citySQL)) {
            System.out.println("\nAvailable service locations:");
            int index = 1;
            while (cityRs.next()) {
                String city = cityRs.getString("location");
                cities.add(city);
                System.out.println(index + ". " + city);
                index++;
            }
        }


        System.out.print("Choose your city (enter number): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > cities.size()) {
            System.out.println("❌ No service in that location.");
            return;
        }
        String location = cities.get(choice - 1);


        ResultSet rs = fetchCabs(conn, preferredType, passengerCount, location);

        String activeType = preferredType;
        if (!rs.isBeforeFirst()) {
            // Try preferred type, all locations
            System.out.println("⚠ No " + preferredType + " in " + location + ". Trying other locations...");
            rs = fetchCabs(conn, preferredType, passengerCount, null);
            if (!rs.isBeforeFirst()) {
                // Try other type, same location
                String otherType = preferredType.equals("Sedan") ? "SUV" : "Sedan";
                System.out.println("⚠ No " + preferredType + ". Trying " + otherType + " in " + location + "...");
                rs = fetchCabs(conn, otherType, passengerCount, location);
                activeType = otherType;
                if (!rs.isBeforeFirst()) {
                    // Try other type, all locations
                    System.out.println("⚠ No " + otherType + " in " + location + ". Trying other locations...");
                    rs = fetchCabs(conn, otherType, passengerCount, null);
                    activeType = otherType;
                    if (!rs.isBeforeFirst()) {
                        System.out.println("❌ Sorry, no cabs available for " + passengerCount + " passengers.");
                        return;
                    }
                }
            }
        }



        System.out.printf("%n%-5s %-12s %-18s %-10s %-15s %-10s%n",
                "ID", "Cab Number", "Driver Name", "Capacity", "Location", "Type");
        System.out.println("-----------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-5d %-12s %-18s %-10d %-15s %-10s%n",
                    rs.getInt("id"),
                    rs.getString("cab_number"),
                    rs.getString("driver_name"),
                    rs.getInt("capacity"),
                    rs.getString("location"),
                    rs.getString("vehicle_type"));
        }


        System.out.print("\nEnter Cab ID to book: ");
        int cabId = Integer.parseInt(sc.nextLine());
        System.out.print("Enter your Name: ");
        String name = sc.nextLine();
        System.out.print("Enter your Contact Number: ");
        String contact = sc.nextLine();


        String bookingSQL = "INSERT INTO bookings (cab_id, passenger_name, passenger_contact) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(bookingSQL)) {
            ps.setInt(1, cabId);
            ps.setString(2, name);
            ps.setString(3, contact);
            ps.executeUpdate();
            System.out.println("✅ Booking confirmed for " + name + " (" + activeType + ").");
        }
    }

    private static ResultSet fetchCabs(Connection conn, String vehicleType, int minCapacity, String location) throws SQLException {
        String sql = "SELECT id, cab_number, driver_name, capacity, location, vehicle_type " +
                "FROM cabs WHERE vehicle_type = ? AND capacity >= ?";
        if (location != null && !location.isEmpty()) {
            sql += " AND location = ?";
        }
        sql += " ORDER BY id";

        PreparedStatement ps = conn.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, vehicleType);
        ps.setInt(2, minCapacity);
        if (location != null && !location.isEmpty()) {
            ps.setString(3, location);
        }
        return ps.executeQuery();
    }

    private static void viewBookingHistory(Connection conn) throws SQLException {
        String sql = "SELECT b.id, c.cab_number, c.driver_name, c.vehicle_type, " +
                "b.passenger_name, b.passenger_contact, b.booking_time " +
                "FROM bookings b JOIN cabs c ON b.cab_id = c.id ORDER BY b.id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.printf("%n%-10s %-12s %-18s %-10s %-15s %-15s %-20s%n",
                    "BookingID", "Cab Number", "Driver Name", "Type", "Passenger", "Contact", "Booking Time");
            System.out.println("-----------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-12s %-18s %-10s %-15s %-15s %-20s%n",
                        rs.getInt("id"),
                        rs.getString("cab_number"),
                        rs.getString("driver_name"),
                        rs.getString("vehicle_type"),


                        rs.getString("passenger_name"),
                        rs.getString("passenger_contact"),
                        rs.getTimestamp("booking_time"));
            }
        }
    }
}
