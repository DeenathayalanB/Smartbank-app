import java.sql.*;
import java.util.Scanner;

public class BankManagementSystem {

    static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    static final String USER = "root"; // change if needed
    static final String PASS = "15102005"; // change if needed

    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to MySQL Database Successfully!\n");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
    }

    public static void createAccount() {
        try {
            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Initial Deposit: ");
            double balance = sc.nextDouble();
            sc.nextLine();

            String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, balance);
            ps.executeUpdate();

            System.out.println("Account created successfully!\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAccounts() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");

            System.out.println("\nAll Accounts:");
            System.out.println("------------------------------------");
            System.out.printf("%-10s %-20s %-10s\n", "Acc_No", "Name", "Balance");
            System.out.println("------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10.2f\n",
                        rs.getInt("acc_no"),
                        rs.getString("name"),
                        rs.getDouble("balance"));
            }
            System.out.println("------------------------------------\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void depositMoney() {
        try {
            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            System.out.print("Enter Amount to Deposit: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            String query = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, amount);
            ps.setInt(2, accNo);

            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Amount Deposited Successfully!\n");
            else
                System.out.println("Account Not Found!\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void withdrawMoney() {
        try {
            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            System.out.print("Enter Amount to Withdraw: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            String checkSql = "SELECT balance FROM accounts WHERE acc_no = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, accNo);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String updateSql = "UPDATE accounts SET balance = balance - ? WHERE acc_no = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                    psUpdate.setDouble(1, amount);
                    psUpdate.setInt(2, accNo);
                    psUpdate.executeUpdate();
                    System.out.println("Withdrawal Successful!\n");
                } else {
                    System.out.println("Insufficient Balance!\n");
                }
            } else {
                System.out.println("Account Not Found!\n");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void checkBalance() {
        try {
            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            sc.nextLine();

            String sql = "SELECT name, balance FROM accounts WHERE acc_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nName: " + rs.getString("name"));
                System.out.println("Balance: " + rs.getDouble("balance") + "\n");
            } else {
                System.out.println("Account Not Found!\n");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteAccount() {
        try {
            System.out.print("Enter Account Number to Delete: ");
            int accNo = sc.nextInt();
            sc.nextLine();

            String sql = "DELETE FROM accounts WHERE acc_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accNo);

            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Account Deleted Successfully!\n");
            else
                System.out.println("Account Not Found!\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        connect();
        int choice;

        do {
            System.out.println("========== BANK MANAGEMENT SYSTEM ==========");
            System.out.println("1. Create Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Check Balance");
            System.out.println("6. Delete Account");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> viewAccounts();
                case 3 -> depositMoney();
                case 4 -> withdrawMoney();
                case 5 -> checkBalance();
                case 6 -> deleteAccount();
                case 0 -> System.out.println("Thank you for using the Bank System!");
                default -> System.out.println("Invalid Choice! Please try again.\n");
            }
        } while (choice != 0);

        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
