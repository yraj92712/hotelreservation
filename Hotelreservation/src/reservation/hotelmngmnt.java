package reservation;

import java.sql.*;
import java.util.Scanner;

public class hotelmngmnt {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Yash7417";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nHOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(connection, scanner);
                    case 2 -> viewReservations(connection);
                    case 3 -> getRoomNumber(connection, scanner);
                    case 4 -> updateReservation(connection, scanner);
                    case 5 -> deleteReservation(connection, scanner);
                    case 0 -> {
                        System.out.println("Thank You For Using Hotel Reservation System!!!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        System.out.print("Enter guest name: ");
        String guestName = scanner.next();
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.next();

        String sql = "INSERT INTO reservations (guestname, roomnumber, contactnumber, reservationdate) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, guestName);
            statement.setInt(2, roomNumber);
            statement.setString(3, contactNumber);
            int rows = statement.executeUpdate();

            System.out.println(rows > 0 ? "Reservation successful!" : "Reservation failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) {
        String sql = "SELECT reservationsid, guestname, roomnumber, contactnumber, reservationdate FROM reservations";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            System.out.printf("%-15s %-15s %-15s %-20s %-25s%n", "Reservation ID", "Guest Name", "Room Number", "Contact Number", "Reservation Date");
            while (rs.next()) {
                System.out.printf("%-15d %-15s %-15d %-20s %-25s%n",
                                  rs.getInt("reservationsid"),
                                  rs.getString("guestname"),
                                  rs.getInt("roomnumber"),
                                  rs.getString("contactnumber"),
                                  rs.getTimestamp("reservationdate").toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID: ");
        int reservationId = scanner.nextInt();
        System.out.print("Enter guest name: ");
        String guestName = scanner.next();

        String sql = "SELECT roomnumber FROM reservations WHERE reservationsid = ? AND guestname = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservationId);
            statement.setString(2, guestName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Room number: " + rs.getInt("roomnumber"));
                } else {
                    System.out.println("Reservation not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservations SET guestname = ?, roomnumber = ?, contactnumber = ? WHERE reservationsid = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newGuestName);
            statement.setInt(2, newRoomNumber);
            statement.setString(3, newContactNumber);
            statement.setInt(4, reservationId);
            int rows = statement.executeUpdate();

            System.out.println(rows > 0 ? "Reservation updated successfully!" : "Reservation update failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID to delete: ");
        int reservationId = scanner.nextInt();

        String sql = "DELETE FROM reservations WHERE reservationsid = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservationId);
            int rows = statement.executeUpdate();

            System.out.println(rows > 0 ? "Reservation deleted successfully!" : "Reservation deletion failed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
