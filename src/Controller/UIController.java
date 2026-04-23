package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import util.FileHandler;
import Model.Expense;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class UIController {

    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;

    @FXML private TableView<Expense> tableView;
    @FXML private TableColumn<Expense, String> titleCol;
    @FXML private TableColumn<Expense, String> categoryCol;
    @FXML private TableColumn<Expense, String> amountCol;

    private ObservableList<Expense> observableExpenses;
    private FileHandler fileHandler = new FileHandler(Path.of("data", "expenses.json"));

    @FXML
    public void initialize() {
        List<Expense> expenses = fileHandler.loadExpenses();

        observableExpenses = FXCollections.observableArrayList(expenses);

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableView.setItems(observableExpenses);
    }

    @FXML
    public void handleAdd() {
        try {
            Expense expense = new Expense(
                    observableExpenses.size() + 1,
                    titleField.getText(),
                    categoryField.getText(),
                    new BigDecimal(amountField.getText()),
                    LocalDate.now(),
                    ""
            );

            observableExpenses.add(expense);
            fileHandler.saveExpenses(observableExpenses);

            titleField.clear();
            categoryField.clear();
            amountField.clear();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}