import java.io.*;
import java.util.*;
public class BankManagementSystem {
    private static Scanner sc = new Scanner(System.in);
    private static final String ACCOUNT_FOLDER_PATH = "C:\\Users\\debma\\Desktop\\db";
    public static void main() throws IOException, ClassNotFoundException {
        File accountFolder = new File(ACCOUNT_FOLDER_PATH);
        if (!accountFolder.exists()) {
            System.out.println("Specified account folder does not exist!");
            return;
        }
        while (true) {
            System.out.println("\nWelcome to the Bank Management System");
            System.out.println("1. Create New Account");
            if (accountFolder.list().length > 0) { // Check if accounts exist
                System.out.println("2. Login");
            }
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    createNewAccount(accountFolder);
                    break;
                case 2:
                    if (accountFolder.list().length > 0) {
                        login(accountFolder);
                    } else {
                        System.out.println("No accounts available. Please create a new account.");
                    }
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // Create a new account
    private static void createNewAccount(File accountFolder) throws IOException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        File accountFile = new File(accountFolder, username + ".dat");
        if (accountFile.exists()) {
            System.out.println("Account with this username already exists. Try another username.");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.print("Enter initial deposit: ");
        double initialBalance = sc.nextDouble();
        sc.nextLine(); // Consume newline
        // Create account object
        Account newAccount = new Account(username, password, initialBalance);
        // Save account to file
        saveAccount(accountFile, newAccount);
        System.out.println("Account created successfully!");
    }
    // Login to an account
    private static void login(File accountFolder) throws IOException, ClassNotFoundException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        File accountFile = new File(accountFolder, username + ".dat");
        if (!accountFile.exists()) {
            System.out.println("No such account found. Please check the username.");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        Account account = loadAccount(accountFile);
        if (!account.getPassword().equals(password)) {
            System.out.println("Incorrect password. Login failed.");
            return;
        }
        System.out.println("Login successful! Welcome, " + account.getUsername());
        userMenu(account, accountFile);
    }
    // User menu
    private static void userMenu(Account account, File accountFile) throws IOException {
        while (true) {
            System.out.println("\nWelcome, " + account.getUsername() + "!");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Check Balance");
            System.out.println("4. View Transaction History");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = sc.nextDouble();
                    sc.nextLine(); // Consume newline
                    account.deposit(depositAmount);
                    saveAccount(accountFile, account);
                    System.out.println("Deposit successful!");
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = sc.nextDouble();
                    sc.nextLine(); // Consume newline
                    if (account.withdraw(withdrawAmount)) {
                        saveAccount(accountFile, account);
                        System.out.println("Withdrawal successful!");
                    } else {
                        System.out.println("Insufficient balance. Withdrawal failed.");
                    }
                    break;
                case 3:
                    System.out.println("Your current balance is: " + account.getBalance());
                    break;
                case 4:
                    System.out.println("Transaction History:");
                    for (String transaction : account.getTransactions()) {
                        System.out.println(transaction);
                    }
                    break;
                case 5:
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    // Save account to a file
    private static void saveAccount(File accountFile, Account account) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(accountFile))) {
            oos.writeObject(account);
        }
    }
    // Load account from a file
    private static Account loadAccount(File accountFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(accountFile))) {
            return (Account) ois.readObject();
        }
    }
}
// Account class
class Account implements Serializable {
    private String username;
    private String password;
    private double balance;
    private List<String> transactions;
    public Account(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.transactions = new ArrayList<>();
        transactions.add("Account created with balance: " + balance);
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public double getBalance() {
        return balance;
    }
    public List<String> getTransactions() {
        return transactions;
    }
    public void deposit(double amount) {
        balance += amount;
        transactions.add("Deposited: " + amount);
    }
    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        transactions.add("Withdrew: " + amount);
        return true;
    }
}
