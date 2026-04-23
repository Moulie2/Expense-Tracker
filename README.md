# Expense Tracker

A simple console-based expense tracker built in Java.

## Features

- Add a new expense with title, category, amount, date, and note
- View all recorded expenses
- Show total and average spending
- Filter expenses by category
- Delete an expense by id
- Persist expense data in `data/expenses.json`

## Compile

```powershell
javac -d out src\App.java src\Controller\MainController.java src\Model\Expense.java src\util\FileHandler.java
```

## Run

```powershell
java -cp out App
```
