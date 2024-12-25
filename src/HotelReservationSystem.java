import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HotelReservationSystem {
    private static String url = "jdbc:mysql://localhost:3306/project";
    private static String id = "root";
    private static String pass = "rr55555";
    public static void main(String[] args){
        loading();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection Con = DriverManager.getConnection(url, id, pass);
            Scanner Sc = new Scanner(System.in);
            while (true) {
                System.out.println(" 1. Book a Room \n 2. View Reservation \n 3. Get Room Number \n 4. Get Guest Information \n 5. Update Reservation \n 6. Delete Reservation \n 0. Exit");
                System.out.print("\nChoose an Option : ");
                int option = -1;
                try{
                    option = Sc.nextInt();
                }
                catch(InputMismatchException e){}
                Sc.nextLine();
                switch (option) {
                    case 1:
                        reservation(Con, Sc);
                        break;
                    case 2:
                        view(Con);
                        break;
                    case 3:
                        getRoomNumber(Con, Sc);
                        break;
                    case 4:
                        guestInformation(Con ,Sc);
                        break;
                    case 5:
                        update(Con, Sc);
                        break;
                    case 6:
                        delete(Con , Sc);
                        break;
                    case 0:
                        exit();
                        Sc.close();
                        break;
                    default:
                        System.out.println("\n--------------Invaid ! Enter 0 - 7----------------\n");
                }
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("Error : " + e1.getMessage());
        } catch (SQLException e2) {
            System.out.println("Error : " + e2.getMessage());
        }catch (InterruptedException e3){
            System.out.println("Error : " + e3.getMessage());
        }
    }

    private static void loading(){
        System.out.println("\n\n----------WELCOME TO HOTEL MANAGEMENT SYSTEM------------\n");
        System.out.print("Loading System");
        for(int i = 0 ; i < 5 ; i++){
            try {
                Thread.sleep(100);
                System.out.print(".");
            }
            catch (InterruptedException e){
                System.out.println("Error : "+e.getMessage());
            }
        }
        System.out.println("\n");
    }

    private static int ValidRoomId(Scanner Sc){
        while (true) {
            int r_id;
            try {
                System.out.print("\nReservation Id : ");
                r_id = Sc.nextInt();
                if (r_id > 0) {
                    return r_id;
                }
                else {
                    System.out.println("\n-------------Invalid ! Enter Correct ID-----------");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n-------------Invalid ! Enter Correct ID-----------");
                Sc.nextLine();
            }
        }
    }

    private static String ValidContact(Scanner Sc){
        while (true){
            String contact;
            System.out.print("Enter Contact Number : ");
            contact = Sc.nextLine();
            if (contact.matches("^[6-9]\\d{9}$")) {
                return contact;
            }
            else {
                System.out.println("\n-----------Invalid ! Number Enter Correct Number-------------\n");
            }
        }
    }

    private static int RoomNumber(Scanner Sc){
        int room;
        while (true) {
            System.out.print("Enter Room Number : ");
            if (Sc.hasNextInt()) {
                room = Sc.nextInt();
                Sc.nextLine();
                return room;
            }
            else {
                System.out.println("\n----------Invalid ! Enter Correct RoomNumber---------\n");
                Sc.nextLine();
            }
        }
    }

    private static void reservation(Connection Con,Scanner Scc){
        while (true){
            try {
                System.out.print("\nEnter Guest Name : ");
                String name = Scc.nextLine().toUpperCase();
                int room = RoomNumber(Scc);
                String contact = ValidContact(Scc);
                String query = "INSERT INTO reservation (Guest_name,Room_Number,Contact_Number) VALUES (?, ?, ?)";
                try (PreparedStatement ps = Con.prepareStatement(query)) {
                    ps.setString(1, name);
                    ps.setInt(2, room);
                    ps.setString(3, contact);
                    int affectedRow = ps.executeUpdate();
                    if (affectedRow > 0) {
                        System.out.println("\n----------Reservation Successfully Done ! -----------\n");
                        return;
                    } else {
                        System.out.println("\n------------Oops! Something Wrong , Reservation Failed--------------\n");
                    }
                }
            }
            catch (SQLException e) {
                System.out.println("Error : " + e.getMessage());
            }
            catch (InputMismatchException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
    }

    private static void view(Connection Con){
        try{
            Statement St = Con.createStatement();
            String query = "SELECT * FROM reservation;";
            ResultSet rs = St.executeQuery(query);
            System.out.println();
            if(rs.next()){
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                System.out.println("   |    Reservation_ID   |  Guest_Name   |   Room_Number   |   Contact_Number   |   Reservation_Date/Time   |");
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                do{
                    int r_id = rs.getInt("Reservation_id");
                    String name = rs.getString("Guest_Name");
                    int room = rs.getInt("Room_Number");
                    String num = rs.getString("Contact_number");
                    String dat = rs.getString("Reservation_Date").toString();
                    System.out.printf("   |         %-11d |  %-12s |       %-9d |    %-15s |    %-22s |\n", r_id,name,room,num,dat);
                }while (rs.next());
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+\n");
            }
            else {
                System.out.println("\n-----------------Oops ! No reservation Found----------------------\n");
            }
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void getRoomNumber(Connection con, Scanner Sc){
        int r_id = ValidRoomId(Sc);
        Sc.nextLine();
        System.out.print("\nEnter Guest Name : ");
        String name = Sc.nextLine().toUpperCase();
        String query = "SELECT Room_Number FROM reservation WHERE Reservation_id = ? AND Guest_Name = ?";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, r_id);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int room_number = rs.getInt("Room_Number");
                System.out.println("\n------------Room Number of " + name + " is : " + room_number +"-------------\n");
            }
            else {
                System.out.println("\n----------------No Match Found---------------\n");
            }
        }
        catch(SQLException e){
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void guestInformation(Connection con, Scanner Sc) {
        int r_id = ValidRoomId(Sc);
        Sc.nextLine();
        System.out.print("\nEnter Guest Name : ");
        String name = Sc.nextLine().toUpperCase();
        String query = "SELECT * FROM reservation WHERE Reservation_id = ? AND Guest_Name = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, r_id);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                System.out.println("   |    Reservation_ID   |  Guest_Name   |   Room_Number   |   Contact_Number   |   Reservation_Date/Time   |");
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                do {
                    int reg_id = rs.getInt("Reservation_id");
                    String g_name = rs.getString("Guest_Name");
                    int room = rs.getInt("Room_Number");
                    String num = rs.getString("Contact_number");
                    String dat = rs.getString("Reservation_Date").toString();
                    System.out.printf("   |         %-11d |  %-12s |       %-9d |    %-15s |    %-22s |\n", reg_id, g_name, room, num, dat);
                } while (rs.next());
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+\n");
            } else {
                System.out.println("\n----------------No Match Found---------------\n");
            }
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void update(Connection con, Scanner Sc){
        int r_id = ValidRoomId(Sc);
        Sc.nextLine();
        String query = "SELECT * FROM reservation WHERE Reservation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, r_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                System.out.println("   |    Reservation_ID   |  Guest_Name   |   Room_Number   |   Contact_Number   |   Reservation_Date/Time   |");
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+");
                do {
                    int reg_id = rs.getInt("Reservation_id");
                    String g_name = rs.getString("Guest_Name");
                    int room = rs.getInt("Room_Number");
                    String num = rs.getString("Contact_number");
                    String dat = rs.getString("Reservation_Date").toString();
                    System.out.printf("   |         %-11d |  %-12s |       %-9d |    %-15s |    %-22s |\n", reg_id, g_name, room, num, dat);
                } while (rs.next());
                System.out.println("   +---------------------+---------------+-----------------+--------------------+---------------------------+\n");
                while (true){
                    try {
                        System.out.print("\nEnter Guest Name to Update : ");
                        String name = Sc.nextLine().toUpperCase();
                        int room = RoomNumber(Sc);
                        String contact = ValidContact(Sc);
                        String query1 = "UPDATE reservation SET Guest_Name = ?, Room_Number =  ?, Contact_Number =  ? WHERE Reservation_Id = ?";
                        try (PreparedStatement pss = con.prepareStatement(query1)) {
                            pss.setString(1, name);
                            pss.setInt(2, room);
                            pss.setString(3, contact);
                            pss.setInt(4,r_id);
                            int affectedRow = pss.executeUpdate();
                            if (affectedRow > 0) {
                                System.out.println("\n----------Reservation Updated Successfully ! -----------\n");
                                return;
                            } else {
                                System.out.println("\n------------Oops! Something Wrong, Updation failed !--------------\n");
                            }
                        }
                    }
                    catch (SQLException e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                }
            } else {
                System.out.println("\n----------------No Match Found---------------\n");
            }
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void delete(Connection con, Scanner Sc){
        int r_id = ValidRoomId(Sc);
        String query = "DELETE FROM reservation WHERE Reservation_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, r_id);
            int affectedRow = ps.executeUpdate();
            if(affectedRow > 0){
                System.out.println("\n------------Data Deleted Successfully !-------------\n");
                return;
            }
            else {
                System.out.println("\n----------------No Match Found---------------\n");
            }
        }
        catch(SQLException e){
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("\nExiting System");
        for (int i = 0 ; i < 5 ; i++){
            System.out.print(".");
            Thread.sleep(100);
        }
        System.out.println("\n\n----------------------------THANKS FOR USING---------------------------------");
        System.exit(0);
    }
}