import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.geometry.Pos;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.Map;

public class EndingScene extends BaseScene {
    EndingScene(Stage stage,GameData gamedata,Shop shop,int day,Map<BreadType, Bread> breads){
        VBox creditBox = new VBox(20);
        String[] endingMassages = {
            //day  + "日の営業が終了しました",
            //"",
            "=============================",
            "         エンディング",
            "=============================",
            "店名：" + shop.getName(),
            "",
            "経営日数：" + day + "日",
            "店舗レベル：" + shop.getLevel(),
            "人気度：" + shop.getPopularity(),
            "最終所持金：" + String.format("%,d", shop.getMoney()) + "G",
            "",
            "==== 経営成績 ====",
            "総売上：" + String.format("%,d",shop.getSalesHistory().getTotalSales()) + "G",
            "総仕入れ額：" + String.format("%,d",shop.getSalesHistory().getTotalCost()) + "G",
            "総広告費：" + String.format("%,d",shop.getSalesHistory().getTotalPromotionCost()) + "G",
            "純利益：" + String.format("%,d",shop.getSalesHistory().getTotalProfit()) + "G",
            "最高売上：" + String.format("%,d",shop.getSalesHistory().getHighSalses()) + "G" + "(" + shop.getSalesHistory().getHighSalsesDay() + "日目)",
            "最高人気度：" + shop.getSalesHistory().getHighPopularity(),
            "全商品完売：" + shop.getSalesHistory().getSoldOut() + "回",
            "",
            "販売したパンの総数：" + shop.getSalesHistory().getTotalSoldBread() + "個",
            "来客人数：" + shop.getSalesHistory().getTotalCustomers() + "人",
            "一番売ったパン：" + shop.getSalesHistory().showHighSoldBreadName(breads) + "(" + shop.getSalesHistory().showHighSoldBreadNum(breads) + "個)",
            "一番作ったパン：" + shop.getSalesHistory().showHighMakeBreadName(breads) + "(" + shop.getSalesHistory().showHighMakeBreadNum(breads) + "個)",
            "",
            "",
            "=============================",
        };
        Label[] massages = new Label[endingMassages.length];
        final int paneWidth = 700;
        final int paneHeight = 400;
        for(int i=0;i<endingMassages.length;i++){
            massages[i] = new Label(endingMassages[i]);
            massages[i].setPrefWidth(paneWidth);
            massages[i].setAlignment(Pos.CENTER);
            massages[i].getStyleClass().add("largeMenuLabel");
            creditBox.getChildren().add(massages[i]);
        }
        
        StackPane pane = new StackPane();
        pane.setMinSize(paneWidth,paneHeight);
        pane.setPrefSize(paneWidth,paneHeight);
        pane.setMaxSize(paneWidth,paneHeight);
        pane.getStyleClass().add("endingBackGround");
        getChildren().add(pane);
        pane.getChildren().add(creditBox);
        pane.setAlignment(creditBox,Pos.CENTER);
        Rectangle clip = new Rectangle(paneWidth,paneHeight);
        pane.setClip(clip);


        creditBox.setTranslateY(720);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(25),
            new KeyValue(creditBox.translateYProperty(), -1000))
        );

        timeline.play();
        Text rankLabel = new Text();
        Label nameLabel = new Label();
        Label[] massageLabel = {new Label(),new Label()};
        rankLabel.getStyleClass().add("rankLabel");
        nameLabel.getStyleClass().add("endingTitle");

        for (Label label : massageLabel) {
            label.getStyleClass().add("endingMessage");
            label.setPrefWidth(paneWidth);
            label.setAlignment(Pos.CENTER);
        }


        if(shop.getLevel() == 5&&
           shop.getPopularity() >= 90&&
           shop.getSalesHistory().getTotalProfit() >= 400000){
            rankLabel.setText("～ランクS～");
            rankLabel.getStyleClass().add("rankS");
            nameLabel.setText("伝説のパン職人！");
            massageLabel[0].setText("あなたのパン屋には毎日長蛇の列！");
            massageLabel[1].setText("経営も技術も町一番、誰もが憧れる伝説のパン屋になりました。");
        }else if(shop.getLevel() == 5&&
           shop.getPopularity() >= 75&&
           shop.getSalesHistory().getTotalProfit() >= 300000){
            rankLabel.setText("～ランクA～");
            rankLabel.getStyleClass().add("rankA");
            nameLabel.setText("町一番の人気ベーカリー！");
            massageLabel[0].setText("多くのお客さんに愛される人気店へと成長しました。");
            massageLabel[1].setText("町の人々の暮らしに欠かせない存在です。");
        }else if(shop.getLevel() >= 4&&
           shop.getPopularity() >= 60&&
           shop.getSalesHistory().getTotalProfit() >= 200000){
            rankLabel.setText("～ランクB～");
            rankLabel.getStyleClass().add("rankB");
            nameLabel.setText("評判のパン屋さん！");
            massageLabel[0].setText("お店は順調に成長し、常連客も増えました。");
            massageLabel[1].setText("あと一歩で町を代表するパン屋です。");
        }else if(shop.getLevel() >= 3&&
           shop.getPopularity() >= 40&&
           shop.getSalesHistory().getTotalProfit() >= 120000){
            rankLabel.setText("～ランクC～");
            rankLabel.getStyleClass().add("rankC");
            nameLabel.setText("地域密着のパン屋");
            massageLabel[0].setText("少しずつお客さんが増え、地域に根付いたお店になりました。");
            massageLabel[1].setText("これからの成長に期待です。");
        }else if(shop.getLevel() >= 2&&
           shop.getSalesHistory().getTotalProfit() >= 50000){
            rankLabel.setText("～ランクD～");
            rankLabel.getStyleClass().add("rankD");
            nameLabel.setText("修行中のパン職人");
            massageLabel[0].setText("パン作りも経営もまだ発展途上です。");
            massageLabel[1].setText("経験を積めば、もっと素敵なお店になるでしょう。");
        }else{
            rankLabel.setText("～ランクE～");
            rankLabel.getStyleClass().add("rankE");
            nameLabel.setText("見習いパン屋");
            massageLabel[0].setText("パン屋経営は決して簡単ではありません。");
            massageLabel[1].setText("今回の経験を活かして、次こそ人気店を目指しましょう！");
        }

        Button titleButton = new Button("～タイトルへ戻る～");
        titleButton.getStyleClass().add("titleButton");
        titleButton.setTranslateY(30);
        
        ScaleTransition st = new ScaleTransition(Duration.seconds(2), titleButton);

        st.setFromX(1.0);
        st.setFromY(1.0);

        st.setToX(1.05);
        st.setToY(1.05);

        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);

        st.play();

        VBox contentBox = new VBox(20);
        contentBox.setPrefWidth(paneWidth);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(rankLabel,nameLabel,massageLabel[0],massageLabel[1],titleButton);
        titleButton.setTranslateY(-5);

        pane.setFocusTraversable(true);
        pane.requestFocus();
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                timeline.stop();
                pane.getChildren().clear();
                pane.getChildren().add(contentBox);
            }
        });

        timeline.setOnFinished(e -> {
            pane.getChildren().clear();
            pane.getChildren().add(contentBox);
        });
        titleButton.setOnAction(e -> {
            getChildren().remove(pane);
            setContent(new TitleScene(stage));
        });
    }
}
