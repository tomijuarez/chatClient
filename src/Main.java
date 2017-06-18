import javafx.application.Application;
import javafx.stage.Stage;
import controller.*;
import model.ChatClient;
import view.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameWindow root = new GameWindow("layouts/configurationView.fxml", new FirstController(), primaryStage);
        root.setTitle("Piñata");
        root.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}