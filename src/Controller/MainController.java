package Controller;

import Model.Expense;
import util.FileHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class MainController {
    private final Scanner scanner;
    private final FileHandler fileHandler;
    private final List<Expense> expenses;

    public MainController(Scanner scanner, FileHandler fileHandler) {
        this.scanner = scanner;
        this.fileHandler = fileHandler;
        this.expenses = new ArrayList<>(fileHandler.loadExpenses());
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    listExpenses();
                    break;
                case 3:
                    showSummary();
                    break;
                case 4:
                    listExpensesByCategory();
                    break;
                case 5:
                    deleteExpense();
                    break;
                case 6:
                    running = false;
                    System.out.println("Expenses saved. See you next time.");
                    break;
                default:
                    System.out.println("Please enter a number between 1 and 6.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==== Expense Tracker ====");
        System.out.println("1. Add expense");
        System.out.println("2. View all expenses");
        System.out.println("3. Show summary");
        System.out.println("4. Filter by category");
        System.out.println("5. Delete expense");
        System.out.println("6. Exit");
    }

    private void addExpense() {
        System.out.println();
        System.out.println("Add a new expense");

        String title = readNonEmptyText("Title: ");
        String category = readNonEmptyText("Category: ");
        BigDecimal amount = readAmount("Amount: ");
        LocalDate date = readDate("Date (yyyy-mm-dd, leave blank for today): ");
        String note = readText("Note (optional): ");

        Expense expense = new Expense(nextId(), title, category, amount, date, note);
        expenses.add(expense);
        save();

        System.out.println("Added: " + expense);
    }

    private void listExpenses() {
        System.out.println();
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed().thenComparing(Expense::getId).reversed())
                .forEach(System.out::println);
    }

    private void showSummary() {
        System.out.println();
        if (expenses.isEmpty()) {
            System.out.println("No expenses available for summary.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> totalsByCategory = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
            totalsByCategory.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
        }

        BigDecimal average = total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);

        System.out.println("Total spending: Rs. " + total.toPlainString());
        System.out.println("Average expense: Rs. " + average.toPlainString());
        System.out.println("Spending by category:");
        totalsByCategory.forEach((category, amount) ->
                System.out.println(" - " + category + ": Rs. " + amount.toPlainString()));
    }

    private void listExpensesByCategory() {
        System.out.println();
        String category = readNonEmptyText("Category to filter: ");

        List<Expense> matches = expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .toList();

        if (matches.isEmpty()) {
            System.out.println("No expenses found in category '" + category + "'.");
            return;
        }

        matches.forEach(System.out::println);
    }

    private void deleteExpense() {
        System.out.println();
        if (expenses.isEmpty()) {
            System.out.println("There are no expenses to delete.");
            return;
        }

        int id = readInt("Expense id to delete: ");
        Optional<Expense> match = expenses.stream()
                .filter(expense -> expense.getId() == id)
                .findFirst();

        if (match.isEmpty()) {
            System.out.println("No expense found with id " + id + ".");
            return;
        }

        expenses.remove(match.get());
        save();
        System.out.println("Deleted expense #" + id + ".");
    }

    private int nextId() {
        return expenses.stream()
                .map(Expense::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void save() {
        fileHandler.saveExpenses(expenses);
    }

    private String readNonEmptyText(String prompt) {
        while (true) {
            String value = readText(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private BigDecimal readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal amount = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                return LocalDate.now();
            }

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException exception) {
                System.out.println("Please use the yyyy-mm-dd format.");
            }
        }
    }
}
