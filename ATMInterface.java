import java.util.*;

class Account {
    private String userId;
    private String userPin;
    private double balance;
    private List<String> transactionHistory = new ArrayList<>();

    public Account(String userId, String userPin, double initialBalance) {
        this.userId = userId;
        this.userPin = userPin;
        this.balance = initialBalance;
    }

    public String getUserId() { return userId; }
    public boolean validatePin(String pin) { return userPin.equals(pin); }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: " + amount + " | Balance: " + balance);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) return false;
        balance -= amount;
        transactionHistory.add("Withdrew: " + amount + " | Balance: " + balance);
        return true;
    }

    public boolean transfer(Account receiver, double amount) {
        if (amount > balance) return false;
        balance -= amount;
        receiver.balance += amount;
        transactionHistory.add("Transferred: " + amount + " to " + receiver.userId + " | Balance: " + balance);
        receiver.transactionHistory.add("Received: " + amount + " from " + userId + " | Balance: " + receiver.balance);
        return true;
    }

    public void printTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            System.out.println("\n--- Transaction History for " + userId + " ---");
            for (String t : transactionHistory) {
                System.out.println(t);
            }
        }
    }
}

class Bank {
    private Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account authenticate(String userId, String pin) {
        Account acc = accounts.get(userId);
        if (acc != null && acc.validatePin(pin)) {
            return acc;
        }
        return null;
    }

    public Account getAccount(String userId) {
        return accounts.get(userId);
    }
}

class ATM {
    private Scanner sc = new Scanner(System.in);
    private Bank bank;
    private Account currentAccount;

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void start() {
        System.out.println("===== Welcome to ATM =====");
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();

        currentAccount = bank.authenticate(userId, pin);
        if (currentAccount == null) {
            System.out.println("Invalid credentials. Exiting...");
            return;
        }

        boolean quit = false;
        while (!quit) {
            System.out.println("\n===== ATM Menu =====");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> currentAccount.printTransactionHistory();
                case 2 -> withdraw();
                case 3 -> deposit();
                case 4 -> transfer();
                case 5 -> { quit = true; System.out.println("Thank you for using ATM. Goodbye!"); }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void withdraw() {
        System.out.print("Enter amount to withdraw: ");
        double amt = sc.nextDouble();
        if (currentAccount.withdraw(amt)) {
            System.out.println("Withdrawal successful! New Balance: " + currentAccount.getBalance());
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    private void deposit() {
        System.out.print("Enter amount to deposit: ");
        double amt = sc.nextDouble();
        currentAccount.deposit(amt);
        System.out.println("Deposit successful! New Balance: " + currentAccount.getBalance());
    }

    private void transfer() {
        sc.nextLine(); // clear buffer
        System.out.print("Enter receiver User ID: ");
        String receiverId = sc.nextLine();
        Account receiver = bank.getAccount(receiverId);
        if (receiver == null) {
            System.out.println("Receiver not found!");
            return;
        }
        System.out.print("Enter amount to transfer: ");
        double amt = sc.nextDouble();
        if (currentAccount.transfer(receiver, amt)) {
            System.out.println("Transfer successful! New Balance: " + currentAccount.getBalance());
        } else {
            System.out.println("Insufficient balance!");
        }
    }
}

public class ATMInterface {
    public static void main(String[] args) {
        Bank bank = new Bank();

        // Adding demo accounts
        bank.addAccount(new Account("user1", "1234", 5000));
        bank.addAccount(new Account("user2", "5678", 3000));

        ATM atm = new ATM(bank);
        atm.start();
    }
}