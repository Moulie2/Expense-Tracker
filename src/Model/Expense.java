package Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Expense {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private int id;
    private String title;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    private String note;

    public Expense(int id, String title, String category, BigDecimal amount, LocalDate date, String note) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String toJson() {
        return String.format(
                "{" +
                        "\"id\":%d," +
                        "\"title\":\"%s\"," +
                        "\"category\":\"%s\"," +
                        "\"amount\":\"%s\"," +
                        "\"date\":\"%s\"," +
                        "\"note\":\"%s\"" +
                        "}",
                id,
                escape(title),
                escape(category),
                amount.toPlainString(),
                date.format(DATE_FORMATTER),
                escape(note)
        );
    }

    @Override
    public String toString() {
        return String.format(
                "#%d | %-18s | %-12s | Rs. %-10s | %s | %s",
                id,
                title,
                category,
                amount.toPlainString(),
                date.format(DATE_FORMATTER),
                note.isBlank() ? "-" : note
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Expense)) {
            return false;
        }
        Expense expense = (Expense) object;
        return id == expense.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
