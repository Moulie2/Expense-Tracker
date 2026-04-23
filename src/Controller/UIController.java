package Controller;

import Model.Expense;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import util.FileHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class UIController {
    @FXML
    private TextField titleField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea noteField;

    @FXML
    private Label totalLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Expense> tableView;

    @FXML
    private TableColumn<Expense, Number> idCol;

    @FXML
    private TableColumn<Expense, String> titleCol;

    @FXML
    private TableColumn<Expense, String> categoryCol;

    @FXML
    private TableColumn<Expense, String> amountCol;

    @FXML
    private TableColumn<Expense, String> dateCol;

    @FXML
    private TableColumn<Expense, String> noteCol;

    private final FileHandler fileHandler = new FileHandler(Path.of("data", "expenses.json"));
    private ObservableList<Expense> observableExpenses;

    @FXML
    public void initialize() {
        List<Expense> expenses = fileHandler.loadExpenses();
        observableExpenses = FXCollections.observableArrayList(expenses);
        sortExpenses();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper("Rs. " + cellData.getValue().getAmount().toPlainString()));
        dateCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDate().toString()));
        noteCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNote().isBlank() ? "-" : cellData.getValue().getNote()));

        tableView.setItems(observableExpenses);
        datePicker.setValue(LocalDate.now());
        refreshSummary();
        statusLabel.setText("Loaded " + observableExpenses.size() + " expense(s).");
    }

    @FXML
    public void handleAdd() {
        try {
            String title = titleField.getText().trim();
            String category = categoryField.getText().trim();
            String amountText = amountField.getText().trim();
            LocalDate date = datePicker.getValue() == null ? LocalDate.now() : datePicker.getValue();
            String note = noteField.getText().trim();

            if (title.isBlank() || category.isBlank() || amountText.isBlank()) {
                showError("Title, category, and amount are required.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

            Expense expense = new Expense(
                    nextId(),
                    title,
                    category,
                    amount,
                    date,
                    note
            );

            observableExpenses.add(expense);
            sortExpenses();
            persist();
            clearForm();
            statusLabel.setText("Added expense #" + expense.getId() + ".");
        } catch (NumberFormatException exception) {
            showError("Please enter a valid number for amount.");
        } catch (RuntimeException exception) {
            showError("Could not save the expense: " + exception.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
        if (selectedExpense == null) {
            showError("Select an expense in the table first.");
            return;
        }

        observableExpenses.remove(selectedExpense);
        persist();
        refreshSummary();
        statusLabel.setText("Deleted expense #" + selectedExpense.getId() + ".");
    }

    @FXML
    public void handleClear() {
        clearForm();
        statusLabel.setText("Form cleared.");
    }

    private void persist() {
        fileHandler.saveExpenses(observableExpenses);
        refreshSummary();
    }

    private void refreshSummary() {
        BigDecimal total = observableExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText("Total spent: Rs. " + total.toPlainString());
    }

    private int nextId() {
        return observableExpenses.stream()
                .map(Expense::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void clearForm() {
        titleField.clear();
        categoryField.clear();
        amountField.clear();
        noteField.clear();
        datePicker.setValue(LocalDate.now());
        tableView.getSelectionModel().clearSelection();
    }

    private void sortExpenses() {
        FXCollections.sort(
                observableExpenses,
                Comparator.comparing(Expense::getDate).reversed().thenComparing(Expense::getId).reversed()
        );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Expense Tracker");
        alert.setContentText(message);
        alert.showAndWait();
        statusLabel.setText(message);
    }
}
