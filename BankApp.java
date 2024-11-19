import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

class BankingApp {
    static HashMap<String, Account> accounts = new HashMap<>();
    static final String DATA_FILE = "accounts_data.txt";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        BankingApp app = new BankingApp();
        app.showLoadingAnimation("Loading Accounts");
        app.loadAccounts();

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Welcome to Secure Banking App ===");

        while (true) {
            System.out.println("\n[1] Login\n[2] Create Account\n[3] Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            System.out.println();
            if (choice == 1) {
                app.login(sc);
            } else if (choice == 2) {
                app.createAccount(sc);
            } else {
                System.out.println("Exiting app.");
                break;
            }
        }
    }

    void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String username = data[0];
                String password = data[1];
                float balance = Float.parseFloat(data[2]);
                Account account = new Account(username, password, balance);
                accounts.put(username, account);
            }
            System.out.println("Accounts Loaded Successfully.\n");
        } catch (IOException e) {
            System.out.println("No saved data found. Starting fresh.");
        }
    }

    void saveAccounts() {
        showLoadingAnimation("Saving Accounts");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Account account : accounts.values()) {
                bw.write(account.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving account data.");
        }
    }

    void login(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            adminDashboard(sc);
        } else if (accounts.containsKey(username) && accounts.get(username).password.equals(password)) {
            userDashboard(sc, accounts.get(username));
        } else {
            System.out.println("Invalid credentials. Try again.");
        }
    }

    void createAccount(Scanner sc) {
        System.out.print("Choose a username: ");
        String username = sc.next();

        if (accounts.containsKey(username)) {
            System.out.println("Username taken. Choose another.");
            return;
        }

        System.out.print("Create a password: ");
        String password = sc.next();
        System.out.print("Initial deposit amount: ");
        float initialDeposit = sc.nextFloat();
        if (initialDeposit < 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }

        Account newAccount = new Account(username, password, initialDeposit);
        accounts.put(username, newAccount);
        saveAccounts();
        System.out.println("Account created successfully!");
    }

    void userDashboard(Scanner sc, Account account) {
        while (true) {
            System.out.println("\n=== User Dashboard ===");
            System.out.println("[1] View Balance\n[2] Deposit\n[3] Withdraw\n[4] Transfer Money\n[5] View Transactions\n[6] Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            System.out.println();

            switch (choice) {
                case 1 : System.out.println("Balance: ₹" + account.balance);
                case 2 : {
                    System.out.print("Deposit amount: ");
                    float amount = sc.nextFloat();
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                    } else {
                        showLoadingAnimation("Processing Deposit");
                        account.deposit(amount);
                        saveAccounts();
                    }
                }
                case 3 : {
                    System.out.print("Withdraw amount: ");
                    float amount = sc.nextFloat();
                    if (amount > account.balance) {
                        System.out.println("Insufficient funds.");
                    } else if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                    } else {
                        showLoadingAnimation("Processing Withdrawal");
                        account.withdraw(amount);
                        saveAccounts();
                    }
                }
                case 4 : {
                    System.out.print("Enter recipient's username: ");
                    String recipientUsername = sc.next();
                    System.out.print("Enter transfer amount: ");
                    float amount = sc.nextFloat();

                    if (!accounts.containsKey(recipientUsername)) {
                        System.out.println("Recipient not found.");
                    } else if (amount <= 0) {
                        System.out.println("Transfer amount must be positive.");
                    } else if (amount > account.balance) {
                        System.out.println("Insufficient funds.");
                    } else {
                        Account recipient = accounts.get(recipientUsername);
                        showLoadingAnimation("Processing Transfer");
                        account.transferTo(recipient, amount);
                        saveAccounts();
                    }
                }
                case 5 : account.viewTransactions();
                case 6 : { return; }
                default : System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    void adminDashboard(Scanner sc) {
        while (true) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("[1] View All Accounts\n[2] Delete Account\n[3] Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            System.out.println();

            switch (choice) {
                case 1 : accounts.values().forEach(acc -> System.out.println(acc));
                case 2 : {
                    System.out.print("Enter username to delete: ");
                    String username = sc.next();
                    if (accounts.remove(username) != null) {
                        System.out.println("Account deleted.");
                        saveAccounts();
                    } else {
                        System.out.println("Account not found.");
                    }
                }
                case 3 : { return; }
                default : System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    void showLoadingAnimation(String message) {
        System.out.print(message);
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Animation interrupted.");
            }
        }
        System.out.println();
    }
}

class Account {
    String username;
    String password;
    float balance;
    StringBuilder transactions = new StringBuilder();

    Account(String username, String password, float balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        addTransaction("Account created with balance ₹" + balance);
    }

    void deposit(float amount) {
        balance += amount;
        addTransaction("Deposited: ₹" + amount);
        System.out.println("Deposit successful. Balance: ₹" + balance);
    }

    void withdraw(float amount) {
        balance -= amount;
        addTransaction("Withdrew: ₹" + amount);
        System.out.println("Withdrawal successful. Balance: ₹" + balance);
    }

    void transferTo(Account recipient, float amount) {
        balance -= amount;
        recipient.balance += amount;
        addTransaction("Transferred: ₹" + amount + " to " + recipient.username);
        recipient.addTransaction("Received: ₹" + amount + " from " + username);
        System.out.println("Transfer successful. New balance: ₹" + balance);
    }

    void viewTransactions() {
        System.out.println("Transaction History:");
        System.out.println(transactions);
    }

    void addTransaction(String transaction) {
        transactions.append(transaction).append("\n");
    }

    @Override
    public String toString() {
        return username + "," + password + "," + balance;
    }
}