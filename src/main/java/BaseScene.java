import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.geometry.Pos;

public class BaseScene extends StackPane {
    protected BorderPane layout = new BorderPane();
    
    public BaseScene() {
        Image image = new Image(
            getClass().getResource("/images/background.jpg").toExternalForm()
        );

        ImageView bg = new ImageView(image);
        bg.setFitWidth(Config.WIDTH);
        bg.setFitHeight(Config.HEIGHT);
        layout.setPrefSize(Config.WIDTH, Config.HEIGHT);
        layout.setMaxHeight(Config.HEIGHT);
        layout.setMaxWidth(Config.WIDTH);

        StackPane.setAlignment(layout, Pos.TOP_LEFT);

        getChildren().addAll(bg,layout);
    }

    protected void setContent(Node node) {
        layout.setCenter(node);
    }
}