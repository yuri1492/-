import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        BaseScene root = new TitleScene(stage);

        Scene scene = new Scene(root, Config.WIDTH, Config.HEIGHT);

        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setTitle("パン屋物語");
        stage.setResizable(false); 
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}