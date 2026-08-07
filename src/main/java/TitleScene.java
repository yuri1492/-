import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;


public class TitleScene extends BaseScene {
    private GameData gamedata;
    TitleScene(Stage stage){
        GameData gamedata = new GameData();
        showTitle(stage,gamedata);
    }

    private void showTitle(Stage stage,GameData gamedata){
        AnchorPane pane = new AnchorPane();

        Label title = new Label("パン屋物語");
        Label subtitle = new Label("～小さな町のベーカリー～");
        Pane titleBackGround = new Pane();
        title.getStyleClass().add("title");
        subtitle.getStyleClass().add("subtitle");
        titleBackGround.getStyleClass().add("titleBackGround");
        title.setPrefWidth(500);
        subtitle.setPrefWidth(500);
        titleBackGround.setMaxWidth(500);
        titleBackGround.setPrefHeight(135);
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setAlignment(subtitle, Pos.TOP_CENTER);
        layout.setAlignment(titleBackGround, Pos.TOP_CENTER);
        title.setTranslateY(103);
        subtitle.setTranslateY(155);
        titleBackGround.setTranslateY(90);

        Button startButton = new Button("ゲーム開始");
        startButton.getStyleClass().add("startButton");
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.2), startButton);

        st.setFromX(1.0);
        st.setFromY(1.0);

        st.setToX(1.05);
        st.setToY(1.05);

        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);

        st.play();
        
        startButton.setPrefSize(240, 70);
        startButton.setLayoutY(450);
        startButton.setLayoutX(410);

        startButton.setOnAction(e -> {
            getChildren().removeAll(title,subtitle);
            layout.getChildren().clear();
            inputName(stage,gamedata);
        });
        pane.getChildren().add(startButton);
        getChildren().addAll(title,subtitle);
        layout.setTop(titleBackGround);
        layout.getChildren().addAll(pane);
    }

    private void inputName(Stage stage,GameData gamedata){
        StackPane content = new StackPane();
        content.getStyleClass().add("nameBackGround");
        content.setPrefSize(480,350);
        content.setMaxWidth(400);
        content.setMaxHeight(480);
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("お店の名前を入力してください");
        label.getStyleClass().add("nameFieldLabel");
        TextField nameField = new TextField();
        Button okButton = new Button("決定");
        okButton.getStyleClass().add("okButton");
        okButton.setPrefSize(120, 50);
        nameField.getStyleClass().add("nameFieldButton");
        nameField.setPromptText("まちのパン屋さん");
        nameField.setMaxWidth(400);

        box.setMargin(okButton,new Insets(40,0,20,0));

        HBox modeBox = new HBox(30);
        Label modeChoiceLabel = new Label("ゲームモード");
        box.setMargin(modeChoiceLabel,new Insets(40,0,0,0));
        modeChoiceLabel.getStyleClass().add("nameFieldLabel");
        Button left = new Button("◀");
        Button right = new Button("▶");
        left.setPrefSize(45,45);
        right.setPrefSize(45,45);
        left.getStyleClass().add("modeButton");
        right.getStyleClass().add("modeButton");
        final int[] index = {0};
        Mode[] modes = Mode.values();
        Label modeLabel = new Label(modes[index[0]].getName());
        modeLabel.getStyleClass().add("modeLabel");
        modeLabel.setPrefWidth(130);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.getChildren().addAll(left,modeLabel,right);
        right.setOnAction(e -> {
            if(index[0] == modes.length - 1){
                index[0] = 0;
            }else{
                index[0]++;
            }
            modeLabel.setText(modes[index[0]].getName());
        });

        left.setOnAction(e -> {
            if(index[0] == 0){
                index[0] = modes.length - 1;
            }else{
                index[0]--;
            }
            modeLabel.setText(modes[index[0]].getName());
        });
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        alert.setHeaderText(null);
        alert.setGraphic(null);
        okButton.setOnAction(e -> {
            if(isValidName(nameField.getText())){
                if(nameField.getText().trim().isEmpty()){
                    gamedata.setName("まちのパン屋さん");
                }else{
                    gamedata.setName(nameField.getText());
                }
            }else{
                alert.setContentText("店名は全角8文字、または半角12文字以内で入力してください。");
                alert.showAndWait();
                return;
            }
            gamedata.setMode(modes[index[0]]);
            stage.setTitle("パン屋物語（" + gamedata.getMode().getName() + "）");
            layout.getChildren().clear();
            setContent(new GameScene(stage,gamedata));
        });

        box.getChildren().addAll(label, nameField, modeChoiceLabel, modeBox, okButton);
        content.getChildren().add(box);

        layout.setCenter(content);

    }

    private boolean isValidName(String name) {
        if (name.trim().isEmpty()) {
            return true;
        }

        float length = 0;

        for (char c : name.toCharArray()) {
            if (c <= 0x7F) {
                // ASCII文字（半角英数字・記号）
                length += 1;
            } else {
                // それ以外（日本語など）
                length += 1.5;
            }
        }

        return length <= 12;
    }
}
