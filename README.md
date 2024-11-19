# Console-Based Banking Management System

## Description
This Java-based program is a simple, console-driven banking application designed to demonstrate core banking functionalities. The system allows both users and administrators to interact with the banking system through various account management features, including deposits, withdrawals, and transfers, all handled within a streamlined command-line interface.

## Features
### User Functionality
1. Login and Account Creation:
    Users can log in to their accounts or create a new account by setting a username, password, and initial deposit.

2. Account Operations:
    Once logged in, users can view their balance, deposit funds, withdraw funds, and transfer money to other users within the system.

3. Transaction History:
    Users can view a detailed list of all their past transactions, including deposits, withdrawals, and transfers.

4. Transfer Funds to Other Accounts:
    Users can transfer funds to any other user within the system by specifying the recipient's username and the amount to be transferred. Transaction details are updated for both sender and recipient.

### Admin Functionality
1. Admin Dashboard:
    Admins can view all user accounts and their balances, delete specific accounts, and view the total count of accounts. This is useful for overall account management and reporting.

2. Authentication:
    Admins have a unique login with a fixed username and password, allowing for secure access to the admin dashboard.

## Implementation Details
### 1. Data Persistence:
· All account data is stored in a simple text file (accounts_data.txt). Each time a change is made (e.g., a transaction, account creation, or deletion), the program saves the current state of all accounts to this file, ensuring data is preserved between program runs.

### 2. Account Management:
· Accounts are stored in a HashMap for efficient lookups. Each account contains fields for the username, password, balance, and a transaction history, which is updated for every transaction.

### 3. Modular Functions:
· Account Class: Represents individual user accounts, with methods for depositing, withdrawing, transferring funds, and viewing transactions.
· BankingApp Class: Manages the overall flow, including login, account creation, and dashboard navigation for both users and admins.

### 4. Animations:
· Simple loading animations are included for specific actions, such as saving data, to enhance the user experience.

### 5. User Input Validation:
· The program includes input validation for balance checks, ensuring that users cannot overdraw their accounts or transfer more than they have available.

## How to Run

### 1. Compile the Java code:
```
javac BankingApp.java
```

### 2. Run the application:
```
java BankingApp
```

### 3. Using the Admin Account:
· Use the username admin and password admin123 to access the admin dashboard.

## Sample Usage

### 1. User Login/Account Creation:
· Upon launching, the program presents options for logging in or creating a new account.

### 2. Performing Transactions:
· After logging in, users can deposit, withdraw, or transfer funds to another account by selecting the appropriate menu option.

### 3. Viewing Transaction History:
· Users can view a list of all their transactions, which is useful for tracking their financial activities.

## Example Screenshot
Here's an example of the user dashboard output for reference.
```
=== User Dashboard ===
[1] View Balance
[2] Deposit
[3] Withdraw
[4] Transfer Money
[5] View Transactions
[6] Logout
Choose an option: 
```

## Limitations and Future Improvements

· Error Handling: Currently, basic error handling is in place, but additional edge cases (e.g., file access errors) could be further refined.
· Security: Passwords are stored in plaintext; this could be improved by implementing hashing for secure storage.
· Enhanced UI: Additional UI formatting and color could be added to improve readability in the console.
