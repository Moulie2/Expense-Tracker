import Controller.MainController;
import util.FileHandler;

import java.nio.file.Path;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Path dataFile = Path.of("data", "expenses.json");

        try (Scanner scanner = new Scanner(System.in)) {
            FileHandler fileHandler = new FileHandler(dataFile);
            MainController controller = new MainController(scanner, fileHandler);
            controller.start();
        } catch (RuntimeException exception) {
            System.err.println("Something went wrong: " + exception.getMessage());
        }
    }
}
