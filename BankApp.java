import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class BankApp {
    static HashMap<String, Account> customerAccounts = new HashMap<>();
    static final String DATA_FILE = "data.txt";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "adminpass";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankApp app = new BankApp();
        app.loadAccounts();
        int choice;
        System.out.println("=== Welcome to Banking App ===");
        do {
            System.out.println("[1] Login\n[2] Create Account");
            choice = sc.nextInt();
            if (choice == 1) {
                app.showLogin(sc);
                break;
            } else if (choice == 2) {
                app.createNewAccount(sc);
                break;
            }
            System.out.println("Invalid Input.");
        } while (choice > 2 || choice < 1);
    }

    void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    // Load account data: username, password, and balance
                    Account account = new Account(data[0], data[1], Float.parseFloat(data[2]));

                    // Load transaction history if available
                    int i = 3;  // Start reading transactions from the 4th position
                    while (i < data.length) {
                        account.transactions.add(data[i++]);
                    }

                    // Add account to the map
                    customerAccounts.put(account.username, account);
                }
            }
            System.out.println("Data Loaded Successfully.");
        } catch (IOException e) {
            System.out.println("Starting with empty data");
        }
    }

    void showLogin(Scanner sc) {
        System.out.print("Enter Username : ");
        String username = sc.next();
        System.out.print("Enter Password : ");
        String password = sc.next();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD))
            showAdminDashboard(sc);
        else if (customerAccounts.containsKey(username) && customerAccounts.get(username).password.equals(password))
            showCustomerDashboard(username, sc);
        else {
            System.out.println("[1] Create Account [2] Exit");
            if (sc.nextInt() == 1)
                createNewAccount(sc);
            else
                System.out.println("Exiting");
        }
    }

    void createNewAccount(Scanner sc) {
        String username = "", password = "";
        System.out.print("Create Username (one word only): ");
        username = sc.next();

        while (customerAccounts.containsKey(username)) {
            System.out.println("Username not available. Try another.");
            System.out.print("Create Username: ");
            username = sc.next();
        }

        System.out.print("Create Password: ");
        password = sc.next();

        System.out.print("Starting Deposit: ");
        float initialDeposit = sc.nextFloat();

        Account newAccount = new Account(username, password, initialDeposit);
        customerAccounts.put(username, newAccount);
        saveAccounts();
        System.out.println("[1] Login [2] Exit");
        if (sc.nextInt() == 1)
            showLogin(sc);
        else
            System.out.println("Exiting");
    }

    void saveAccounts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Account account : customerAccounts.values()) {
                bw.write(account.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    void showCustomerDashboard(String username, Scanner sc) {
        Account account = customerAccounts.get(username);
        int choice;
        do {
            System.out.println("=== Customer Dashboard ===");
            System.out.println("[1] View Balance\n[2] Deposit\n[3] Withdraw\n[4] Transfer\n[5] View Transactions\n[6] Logout");
            choice = sc.nextInt();
            if (choice == 6)
                break;
            performCustomerActions(choice, account, sc);
            saveAccounts();
        } while (true);
    }

    void performCustomerActions(int choice, Account account, Scanner sc) {
        switch (choice) {
            case 1 -> System.out.println("Balance : ₹" + account.balance);
            case 2 -> {
                    System.out.print("Enter deposit amount: ");
                    account.deposit(sc.nextFloat());
                }
            case 3 -> {
                    System.out.print("Enter withdrawal amount: ");
                    account.withdraw(sc.nextFloat());
                }
            case 4 -> {
                    System.out.print("Enter recipient username: ");
                    String recipient = sc.next();
                    System.out.print("Enter transfer amount: ");
                    account.transfer(customerAccounts.get(recipient), sc.nextFloat());
                }
            case 5 -> account.viewTransactions();
            default -> System.out.println("Invalid Input. Try Again");
        }
    }

    void showAdminDashboard(Scanner sc) {
        int choice;
        do {
            System.out.println("=== Admin Dashboard ===");
            System.out.println("[1] View All Accounts\n[2] Close Account\n[3] View Account Count\n[4] Logout");
            choice = sc.nextInt();
            if (choice == 4)
                break;
            performAdminActions(choice, sc);
            saveAccounts();
        } while (true);
    }

    void performAdminActions(int choice, Scanner sc) {
        switch (choice) {
            case 1 -> customerAccounts.values().forEach(acc -> System.out.println("User: " + acc.username + " Balance: ₹" + acc.balance));
            case 2 -> {
                    System.out.print("Enter username to close account: ");
                    customerAccounts.remove(sc.next());
                    System.out.println("Account closed successfully.");
                }
            case 3 -> System.out.println("Total number of accounts: " + customerAccounts.size());
            default -> System.out.println("Invalid option. Try Again.");
        }
    }

}

class Account {
    String username;
    String password;
    float balance;
    ArrayList<String> transactions = new ArrayList<>();
    Loan currentLoan;

    Account(String username, String password, float balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    void deposit(float amount) {
        balance += amount;
        transactions.add("Deposited ₹" + amount);
        System.out.println("Deposit successful. New balance: ₹" + balance);
    }

    void withdraw(float amount) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add("Withdrew ₹" + amount);
            System.out.println("Withdrawal successful. New balance: ₹" + balance);
        } else
            System.out.println("Insufficient funds.");
    }

    void transfer(Account recipient, float amount) {
        if (amount <= balance) {
            balance -= amount;
            recipient.balance += amount;
            transactions.add("Transferred ₹" + amount + " to " + recipient.username);
            recipient.transactions.add("Received ₹" + amount + " from " + username);
            System.out.println("Transfer successful. New balance: ₹" + balance);
        } else
            System.out.println("Insufficient funds.");
    }

    void viewTransactions() {
        System.out.println("Transaction History:");
        for (String transaction : transactions)
            System.out.println(transaction);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(username + "," + password + "," + balance);
        for (String transaction : transactions) {
            sb.append(",").append(transaction);
        }
        return sb.toString();
    }
}