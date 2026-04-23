import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            URL fxmlResource = getClass().getResource("/main.fxml");
            if (fxmlResource == null) {
                fxmlResource = Path.of("src", "main.fxml").toUri().toURL();
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Scene scene = new Scene(loader.load(), 980, 640);

            stage.setTitle("Expense Tracker");
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(620);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load JavaFX UI");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
