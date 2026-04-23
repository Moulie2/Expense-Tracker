package util;

import Model.Expense;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler {
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}");
    private static final Pattern FIELD_PATTERN = Pattern.compile("\"([^\"]+)\":(?:\"((?:\\\\.|[^\"])*)\"|([^,}]+))");

    private final Path filePath;

    public FileHandler(Path filePath) {
        this.filePath = filePath;
    }

    public List<Expense> loadExpenses() {
        ensureFileExists();

        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (content.isBlank() || content.equals("[]")) {
                return new ArrayList<>();
            }

            List<Expense> expenses = new ArrayList<>();
            Matcher objectMatcher = OBJECT_PATTERN.matcher(content);
            while (objectMatcher.find()) {
                String objectBody = objectMatcher.group(1);
                expenses.add(parseExpense(objectBody));
            }
            return expenses;
        } catch (IOException exception) {
            throw new RuntimeException("Unable to read expenses from " + filePath, exception);
        }
    }

    public void saveExpenses(List<Expense> expenses) {
        ensureFileExists();

        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (int index = 0; index < expenses.size(); index++) {
            builder.append("  ").append(expenses.get(index).toJson());
            if (index < expenses.size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("]\n");

        try {
            Files.writeString(filePath, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save expenses to " + filePath, exception);
        }
    }

    private Expense parseExpense(String objectBody) {
        Matcher fieldMatcher = FIELD_PATTERN.matcher(objectBody);

        Integer id = null;
        String title = "";
        String category = "";
        String amount = "0";
        String date = LocalDate.now().toString();
        String note = "";

        while (fieldMatcher.find()) {
            String key = fieldMatcher.group(1);
            String quotedValue = fieldMatcher.group(2);
            String rawValue = fieldMatcher.group(3);
            String value = quotedValue != null ? unescape(quotedValue) : rawValue.trim();

            switch (key) {
                case "id":
                    id = Integer.parseInt(value);
                    break;
                case "title":
                    title = value;
                    break;
                case "category":
                    category = value;
                    break;
                case "amount":
                    amount = value;
                    break;
                case "date":
                    date = value;
                    break;
                case "note":
                    note = value;
                    break;
                default:
                    break;
            }
        }

        if (id == null) {
            throw new IllegalArgumentException("Expense entry is missing an id.");
        }

        return new Expense(
                id,
                title,
                category,
                new BigDecimal(amount),
                LocalDate.parse(date),
                note
        );
    }

    private void ensureFileExists() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "[]\n", StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to initialize storage file " + filePath, exception);
        }
    }

    private String unescape(String value) {
        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
