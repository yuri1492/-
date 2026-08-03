import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.border.Border;
import javax.swing.plaf.LabelUI;
import javax.swing.text.LabelView;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.util.Duration;

import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.List;

public class GameScene extends BaseScene {
    private Map<IngredientType, Ingredient> ingredients;
    private Map<BreadType, Bread> breadMap;
    private Map<BreadType, Integer> breadNum;
    private Map<BreadType,Integer> salesBread;
    private List<BreadType> breadList;
    private int BreadMaxLength;
    private int IngredientMaxLength;

    private Shop shop;
    private final int closeBread = 0;
    private int day = 1;
    private boolean checkPop;
    private boolean checkBread;
    private final int maxCommand = 6;
    private final int upPopBread = 5;
    private final int upPopNum = 2;
    private int totalBread;
    private HBox hbox;
    private VBox vbox;
    private Label dayLabel;
    private Label guessCustomer;
    private Label haveMoney;
    private Label shopName;
    private Label shopLevel;
    private Label menuTitle;
    private final List<String> logs = new ArrayList<>();
    private StackPane leftContent;
    private StackPane centerContent;
    private StackPane topContent;
    private StackPane menuTitleBackGround;
    private StackPane popupPane = new StackPane();
    private ImageView girlView;
    private TextArea logArea;
    private  HBox resultBox;
    private VBox resultNameBox;
    private VBox resultNumBox;
    private VBox resultUnitBox;
    private Label[] resultNameLabel;
    private Label[] resultNumLabel;
    private Label[] resultUnitLabel;
    private HBox expiredStockBox;
    private VBox expiredStockNameBox;
    private VBox expiredStockNumBox;
    private VBox expiredStockUnitBox;
    private Label[] expiredStockNameLabel;
    private Label[] expiredStockNumLabel;
    private Label[] expiredStockUnitLabel;
    private Label expiredStockLabel = new Label("期限切れの在庫");
    private boolean checkRemove;
    private Button makeBreadButton = new Button("パンを作る");
    private Button buyIngredientButton = new Button("材料を仕入れる");
    private Button showInventoryButton = new Button("在庫を見る");
    private Button showShopButton = new Button("店を確認する");
    private Button doPromotionButton = new Button("宣伝をする");
    private Button startSalesButton = new Button("営業を開始する");
    private Button doPartButton = new Button("アルバイトをする");
    private final Button[] morningMassages = {
        makeBreadButton,
        buyIngredientButton,
        showInventoryButton,
        showShopButton,
        doPromotionButton,
        startSalesButton,
        doPartButton
    };

    public GameScene(Stage stage,GameData gamedata) {
        DataLoader loader = new DataLoader();

        ingredients =
            loader.loadIngredients()
                  .stream()
                  .collect(Collectors.toMap(
                      Ingredient::getId,
                      i -> i
                  ));

        breadMap =
            loader.loadBreads()
                  .stream()
                  .collect(Collectors.toMap(
                      Bread::getId,
                      b -> b
                  ));
        for (Ingredient ingredient : ingredients.values()) {
            IngredientMaxLength = Math.max(IngredientMaxLength, ingredient.getName().length());
        }
        for (Bread bread : breadMap.values()) {
            BreadMaxLength = Math.max(BreadMaxLength , bread.getName().length());
        }
        shop = new Shop(gamedata.getName(),ingredients,breadMap);
        breadNum = new LinkedHashMap<>();
        
        startGame(stage, gamedata);
    }

    public void startGame(Stage stage,GameData gamedata){
        Image girl = new Image(
            getClass().getResource("/images/girl2.png").toExternalForm()
        );
        girlView = new ImageView(girl);
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(5);
        logArea.getStyleClass().add("logArea");
        logArea.setMaxWidth(800);
        logArea.setMaxHeight(200);
        logArea.setPrefSize(800,200);
        layout.setAlignment(logArea,Pos.TOP_LEFT);

        girlView.setFitWidth(230);
        girlView.setPreserveRatio(true);
        girlView.setTranslateY(140);
        girlView.setTranslateX(380);
        topContent = new StackPane();
        topContent.setPrefSize(Config.WIDTH,70);
        topContent.getStyleClass().add("gameBackGround");
        leftContent = new StackPane();
        centerContent = new StackPane();
        centerContent.getStyleClass().add("gameBackGround");
        centerContent.setMaxWidth(600);
        centerContent.setMaxHeight(450);
        layout.setAlignment(centerContent,Pos.TOP_LEFT);
        leftContent.setMaxHeight(450);
        leftContent.setPrefWidth(200);
        leftContent.getStyleClass().add("gameBackGround");
        menuTitleBackGround = new StackPane();
        menuTitleBackGround.getStyleClass().add("menuTitleBackGround");
        menuTitleBackGround.setMaxWidth(600);
        menuTitleBackGround.setMaxHeight(70);
        menuTitle = new Label();
        menuTitle.getStyleClass().add("menuTitleLabel");
        menuTitleBackGround.getChildren().add(menuTitle);
        centerContent.setAlignment(menuTitleBackGround,Pos.TOP_LEFT);
        hbox = new HBox(20);
        vbox = new VBox(20);
        dayLabel = new Label(day + "日目");
        guessCustomer = new Label("来客予想：" + shop.getPopularity() + "～" + (shop.getPopularity() + shop.getLevel() * 6) + "人");
        haveMoney = new Label(String.format("所持金：%,7dG",shop.getMoney()));
        shopName = new Label(shop.getName());
        shopLevel = new Label("店舗レベル：" + shop.getLevel());
        hbox.getChildren().addAll(shopName,dayLabel,haveMoney,shopLevel,guessCustomer);
        topContent.getChildren().add(hbox);
        leftContent.getChildren().add(vbox);
        layout.setTop(topContent);
        layout.setCenter(centerContent);
        layout.setBottom(logArea);
        popupPane.setPrefSize(450,600);
        popupPane.setMaxWidth(450);
        popupPane.setMaxHeight(600);
        popupPane.setTranslateY(-100);
        popupPane.getStyleClass().add("popupPane");
        Label[] topLabels = {
            dayLabel,
            guessCustomer,
            haveMoney,
            shopName,
            shopLevel
        };
        for(Label label : topLabels){
            label.getStyleClass().add("topLabel");
        }
        for(Button button : morningMassages){
            vbox.getChildren().add(button);
            button.getStyleClass().add("menu");
        }
        doPartButton.setManaged(true);
        layout.setLeft(leftContent);
        getChildren().add(girlView);
        startDay(stage, gamedata);
    }

    public void startDay(Stage stage,GameData gamedata){
        // shop.useMoney(2000);
        // shop.getSalesHistory().addTodaySales(5000000);
        // checkLevel();
        // addPopularity(100);
        // int num = 11;
        // shop.getInventory().addBread(BreadType.Shokupan,num);
        // shop.getInventory().addBread(BreadType.RollBread,num);
        // shop.getInventory().addBread(BreadType.MelonPan,num);
        // shop.getInventory().addBread(BreadType.ChocoCornet,num);
        // shop.getInventory().addBread(BreadType.Croissant,num);
        // shop.getInventory().addBread(BreadType.CurryBread,num);
        // shop.getInventory().addBread(BreadType.HotDog,num);
        // shop.getInventory().addBread(BreadType.HamCheeseBread,num);
        // shop.getInventory().addBread(BreadType.CheeseBread,num);
        // shop.getInventory().addBread(BreadType.Anpan,num);
        shop.getSalesHistory().resetTodaydata();
        resetPromotion();
        switchMenuLabel(false);
        displayClear();
        guessCustomer.setText("来客予想：" + shop.getPopularity() + "～" + (shop.getPopularity() + shop.getLevel() * 6) + "人");
        checkPop = false;
        checkBread = false;
        dayLabel.setText(day + "日目");
        for(BreadType type : shop.getHasBreadRecipe()){
            breadNum.put(type, shop.getInventory().getBread(type));
        }
        boolean condition = !canMakeAnyBread() && shop.getMoney() <= 100 && shop.getInventory().getTotalBread() <= closeBread;
        doPartButton.setVisible(condition);
        makeBreadButton.setOnAction(e -> {
            makeBread();
        });
        buyIngredientButton.setOnAction(e -> {
            buyIngredient();
        });
        showInventoryButton.setOnAction(e -> {
            showInventory();
        });
        showShopButton.setOnAction(e -> {
            showShop();
        });
        doPromotionButton.setOnAction(e -> {
            promotion();
        });
        startSalesButton.setOnAction(e -> {
            totalBread = shop.getInventory().getTotalBread();
            if(totalBread == 0){
                addLog("パンがないので、営業ができません！");
                refreshLog();
            }else{
                switchMenuLabel(true);
                Label start = new Label(day + "日目の営業を開始します");
                start.getStyleClass().add("largeMenuLabel");
                centerContent.getChildren().add(start);
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    startSales(stage,gamedata);
                });
                pause.play();
            }
        });
        doPartButton.setOnAction(e -> {
            switchMenuLabel(true);
            doPartTime(stage,gamedata);
        });
    }

    public void startSales(Stage stage,GameData gamedata){
        displayClear();

        addLog(day + "日目の営業を開始しました");
        Random random = new Random();
        shop.getSalesHistory().setTodayCustomers(shop.getPopularity() + random.nextInt(shop.getLevel() * 6));
        salesBread = new LinkedHashMap<>();
        breadList = new ArrayList<>();
        int breadSize = shop.getHasBreadRecipe().size();
        totalBread = 0;
        for(int i=0;i<breadSize;i++){
            if(shop.getInventory().getBread(shop.getHasBreadRecipe().get(i)) > 0){
                breadList.add(shop.getHasBreadRecipe().get(i));
                salesBread.put(shop.getHasBreadRecipe().get(i), 0);
                totalBread += shop.getInventory().getBread(shop.getHasBreadRecipe().get(i));
            }
        }
        int breadKinds = breadList.size();

        HBox contentBox = new HBox(0);
        VBox breadMenuLeftBox = new VBox(5);
        VBox breadStockNumLeftBox = new VBox(5);
        VBox breadStockUnitLeftBox = new VBox(5);
        VBox breadMenuRightBox = new VBox(5);
        VBox breadStockNumRightBox = new VBox(5);
        VBox breadStockUnitRightBox = new VBox(5);
        Label breadMenuLabel[] = new Label[breadKinds];
        Label breadStockNumLabel[] = new Label[breadKinds];
        Label breadStockUnitLabel[] = new Label[breadKinds];
        int index = 0;
        for (Map.Entry<BreadType, Integer> entry : salesBread.entrySet()) {
            breadMenuLabel[index] = new Label(shop.getBreads().get(entry.getKey()).getName());
            breadStockNumLabel[index] = new Label("" + shop.getInventory().getBread(entry.getKey()));
            breadStockUnitLabel[index] = new Label("個");
            breadMenuLabel[index].getStyleClass().add("salesLabel");
            breadStockNumLabel[index].getStyleClass().add("salesLabel");
            breadStockUnitLabel[index].getStyleClass().add("salesLabel");
            breadMenuLabel[index].setPrefWidth(200);
            breadStockNumLabel[index].setPrefWidth(60);
            breadStockNumLabel[index].setAlignment(Pos.CENTER);
            breadStockUnitLabel[index].setPrefWidth(95);
            if(index % 2 == 0){
                breadMenuLeftBox.getChildren().add(breadMenuLabel[index]);
                breadStockNumLeftBox.getChildren().add(breadStockNumLabel[index]);
                breadStockUnitLeftBox.getChildren().add(breadStockUnitLabel[index]);
            }else{
                breadMenuRightBox.getChildren().add(breadMenuLabel[index]);
                breadStockNumRightBox.getChildren().add(breadStockNumLabel[index]);
                breadStockUnitRightBox.getChildren().add(breadStockUnitLabel[index]);
            }
            index++;
        }
        contentBox.getChildren().addAll(breadMenuLeftBox,breadStockNumLeftBox,breadStockUnitLeftBox,breadMenuRightBox,breadStockNumRightBox,breadStockUnitRightBox);
        contentBox.setTranslateX(15);
        contentBox.setTranslateY(100);
        if(breadSize - 2 > breadKinds){
            int pop = breadSize - breadKinds;
            shop.addPopularity(pop * -1);
            addLog("商品の種類が少ないので、人気度が" + pop + "さがりました");
            checkPop = true;
        }else if (breadKinds >= upPopBread) {
            shop.addPopularity(upPopNum);
            addLog("商品が" + upPopBread + "種類を超えているので、人気度が" + upPopNum + "上がりました");
        }
        refreshLog();
        int[] soldCustomer = {0};


        //*******************************************************
        int updateCount = 15;
        int customerPerUpdate = (int)Math.ceil((double)shop.getSalesHistory().getTodayCustomers() / updateCount);
        Label customerLabel = new Label("来客人数：" + soldCustomer[0] + "/" + shop.getSalesHistory().getTodayCustomers() + "人");
        customerLabel.getStyleClass().add("salesTopLabel");
        customerLabel.setPrefWidth(250);
        customerLabel.setTranslateX(5);
        Label todaySalesLabel = new Label(String.format( "合計売上：%,dG",shop.getSalesHistory().getTodaySales()));
        todaySalesLabel.getStyleClass().add("salesTopLabel");
        todaySalesLabel.setTranslateX(5);
        Label soldBread = new Label("販売数：" + shop.getSalesHistory().getTodaySoldBread() + "個");
        soldBread.getStyleClass().add("salesTopLabel");
        soldBread.setPrefWidth(200);
        Label stockLabel = new Label("＜在庫＞");
        stockLabel.getStyleClass().add("salesTopLabel");
        centerContent.getChildren().addAll(customerLabel,stockLabel,contentBox,todaySalesLabel,soldBread);
        todaySalesLabel.setTranslateY(-5);
        soldBread.setTranslateY(-5);
        centerContent.setAlignment(todaySalesLabel,Pos.BOTTOM_LEFT);
        centerContent.setAlignment(soldBread,Pos.BOTTOM_RIGHT);
        centerContent.setAlignment(customerLabel,Pos.TOP_LEFT);
        centerContent.setAlignment(stockLabel,Pos.TOP_CENTER);
        stockLabel.setTranslateY(40);
        logArea.setScrollTop(Double.MAX_VALUE);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(350), e -> {
                int typeInt;
                BreadType type;
                for(int i=0;i<customerPerUpdate;i++){
                    if(shop.getInventory().getTotalBread() == 0) checkBread = true;
                    if(soldCustomer[0] < shop.getSalesHistory().getTodayCustomers()){
                        soldCustomer[0]++;
                        customerLabel.setText("来客人数：" + soldCustomer[0] + "/" + shop.getSalesHistory().getTodayCustomers() + "人");
                        typeInt = random.nextInt(breadKinds);
                        type = breadList.get(typeInt);
                        if(shop.getInventory().getBread(type) > 0){
                            shop.getInventory().useBread(type, 1);
                            shop.getSalesHistory().addTodaySales(shop.getBreads().get(type).getPrice());
                            shop.getSalesHistory().addSoldBread(type, 1);
                            shop.getSalesHistory().addTodaySoldBread();
                            shop.addMoney(shop.getBreads().get(type).getPrice());
                            salesBread.put(type, salesBread.get(type) + 1);
                            breadStockNumLabel[typeInt].setText(""  + shop.getInventory().getBread(type));
                            todaySalesLabel.setText(String.format( "合計売上：%,dG",shop.getSalesHistory().getTodaySales()));
                            soldBread.setText("販売数：" + shop.getSalesHistory().getTodaySoldBread() + "個");
                        }else if(random.nextInt(2) == 0){
                            if(shop.getInventory().getTotalBread() != 0){
                                while (true) {
                                    typeInt = random.nextInt(breadKinds);
                                    type = breadList.get(typeInt);
                                    if(shop.getInventory().getBread(type) > 0){
                                        shop.getInventory().useBread(type, 1);
                                        shop.getSalesHistory().addTodaySales(shop.getBreads().get(type).getPrice());
                                        shop.getSalesHistory().addSoldBread(type, 1);
                                        shop.getSalesHistory().addTodaySoldBread();
                                        shop.addMoney(shop.getBreads().get(type).getPrice());
                                        salesBread.put(type, salesBread.get(type) + 1);
                                        breadStockNumLabel[typeInt].setText(""  + shop.getInventory().getBread(type));
                                        todaySalesLabel.setText(String.format( "合計売上：%,dG",shop.getSalesHistory().getTodaySales()));
                                        soldBread.setText("販売数：" + shop.getSalesHistory().getTodaySoldBread() + "個");
                                        break;
                                    }
                                }
                            }
                        }
                    }else{
                        break;
                    }
                }
                //logArea.setScrollTop(Double.MAX_VALUE);
            }));
        timeline.setCycleCount(updateCount + 1);
        timeline.play();
        Label end = new Label("営業が終了しました");
        end.getStyleClass().add("largeMenuLabel");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            endDay(stage, gamedata);
        });
        timeline.setOnFinished(e -> {
            displayClear();
            centerContent.getChildren().add(end);
            pause.play();
        });
    }

    public void endDay(Stage stage,GameData gamedata){
        displayClear();
        haveMoney.setText(String.format("所持金：%,7dG",shop.getMoney()));

        resultNameBox = new VBox(0);
        resultNumBox = new VBox(0);
        resultUnitBox = new VBox(0);
        resultNameLabel = new Label[salesBread.size()];
        resultNumLabel = new Label[salesBread.size()];
        resultUnitLabel = new Label[salesBread.size()];
        int index = 0;
        for (Map.Entry<BreadType, Integer> entry : salesBread.entrySet()) {
            resultNameLabel[index] = new Label(shop.getBreads().get(entry.getKey()).getName());
            resultNameLabel[index].getStyleClass().add("popupLabel");
            resultNameLabel[index].setPrefWidth(135);
            resultNumLabel[index] = new Label("" + entry.getValue());
            resultNumLabel[index].getStyleClass().add("popupLabel");
            resultNumLabel[index].setPrefWidth(30);
            resultNumLabel[index].setAlignment(Pos.CENTER);
            resultUnitLabel[index] = new Label("個");
            resultUnitLabel[index].getStyleClass().add("popupLabel");
            resultNameBox.getChildren().add(resultNameLabel[index]);
            resultNumBox.getChildren().add(resultNumLabel[index]);
            resultUnitBox.getChildren().add(resultUnitLabel[index]);
            index++;
        }
        
        expiredStockNameBox = new VBox(0);
        expiredStockNumBox = new VBox(0);
        expiredStockUnitBox = new VBox(0);
        expiredStockNameLabel = new Label[shop.getHasBreadRecipe().size()];
        expiredStockNumLabel = new Label[shop.getHasBreadRecipe().size()];
        expiredStockUnitLabel = new Label[shop.getHasBreadRecipe().size()];

        double rate = (double) shop.getSalesHistory().getTodaySoldBread() / totalBread;
        if(checkPop && checkBread){
            if(shop.getSalesHistory().getTodayCustomers() / 2 > totalBread){
                shop.addPopularity(-3);
                addLog("商品が少なすぎたので、人気度が3さがりました");
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()) shop.getSalesHistory().addSoldOut();

            }else{
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()){
                    shop.addPopularity(1);
                    addLog("商品が完売したので、人気度が1あがりました");
                    shop.getSalesHistory().addSoldOut();
                } else if (rate >= 0.5) {
                    addLog("商品が少し売れましたが、人気度は変わりませんでした");
                    // 変化なし
                } else {
                    shop.addPopularity(-2);
                    addLog("商品があまり売れなかったので、人気度が2下がってしまいました");
                }
            }
        }else if(checkPop || checkBread){
            if(shop.getSalesHistory().getTodayCustomers() / 2 > totalBread){
                shop.addPopularity(-3);
                addLog("商品が少なすぎたので、人気度が3さがりました");
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()) shop.getSalesHistory().addSoldOut();

            }else{
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()){
                    shop.addPopularity(2);
                    addLog("商品が完売したので、人気度が2あがりました");
                    shop.getSalesHistory().addSoldOut();
                } else if(rate >= 0.7) {
                    shop.addPopularity(1);
                    addLog("商品がたくさん売れたので、人気度が1あがりました");
                } else if (rate >= 0.4) {
                    addLog("商品が少し売れましたが、人気度は変わりませんでした");
                    // 変化なし
                } else {
                    shop.addPopularity(-2);
                    addLog("商品があまり売れなかったので、人気度が2下がってしまいました");
                }
            }
        }else{
            if(shop.getSalesHistory().getTodayCustomers() / 2 > totalBread){
                shop.addPopularity(-4);
                addLog("商品が少なすぎたので、人気度が4さがりました");
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()) shop.getSalesHistory().addSoldOut();

            }else{
                if(totalBread == shop.getSalesHistory().getTodaySoldBread()){
                    shop.addPopularity(4);
                    addLog("商品が完売したので、人気度が4あがりました");
                    shop.getSalesHistory().addSoldOut();
                } else if(rate >= 0.9) {
                    shop.addPopularity(3);
                    addLog("商品がたくさん売れたので、人気度が3あがりました");
                } else if (rate >= 0.7) {
                    shop.addPopularity(2);
                    addLog("商品がそこそこ売れたので、人気度が2あがりました");
                } else if (rate >= 0.5) {
                    shop.addPopularity(1);
                    addLog("商品がまあまあ売れたので、人気度が1あがりました");
                } else if (rate >= 0.3) {
                    addLog("商品が少し売れましたが、人気度は変わりませんでした");
                    // 変化なし
                } else {
                    shop.addPopularity(-2);
                    addLog("商品があまり売れなかったので、人気度が2下がってしまいました");
                }
            }
        }
        int profit = shop.getSalesHistory().getTodaySales() - shop.getSalesHistory().getTodayCost() - shop.getSalesHistory().getTodayPromotionCost();
        shop.getSalesHistory().addTotalProfit(profit);
        shop.getSalesHistory().updateHighSalses(shop.getSalesHistory().getTodaySales(), day);

        Random random = new Random();
        if(random.nextInt(3) <= 1) shop.addPopularity(random.nextInt(shop.getLevel()) * -1);
        checkLevel();
        checkRemove = false;
        int indexNum = 0;
        for (Map.Entry<BreadType, Integer> entry : breadNum.entrySet()) {
            String name = shop.getBreads().get(entry.getKey()).getName();
            int deadline;
            if(salesBread.get(entry.getKey()) == null){
                deadline = entry.getValue() - 0;
            }else{
                deadline = entry.getValue() - salesBread.get(entry.getKey());
            }
            if(deadline > 0){
                if(!checkRemove){
                    checkRemove = true;
                    centerContent.getChildren().add(expiredStockLabel);
                    addLog("期限切れの在庫を処分しました");
                }
                shop.getInventory().useBread(entry.getKey(), deadline);
                expiredStockNameLabel[indexNum] = new Label(name);
                expiredStockNameLabel[indexNum].setPrefWidth(135);
                expiredStockNumLabel[indexNum] = new Label("" + deadline);
                expiredStockNumLabel[indexNum].setPrefWidth(30);
                expiredStockUnitLabel[indexNum] = new Label("個");
                expiredStockNameLabel[indexNum].getStyleClass().add("popupLabel");
                expiredStockNumLabel[indexNum].getStyleClass().add("popupLabel");
                expiredStockUnitLabel[indexNum].getStyleClass().add("popupLabel");
                expiredStockNameBox.getChildren().add(expiredStockNameLabel[indexNum]);
                expiredStockNumBox.getChildren().add(expiredStockNumLabel[indexNum]);
                expiredStockUnitBox.getChildren().add(expiredStockUnitLabel[indexNum]);
            }
        }

        refreshLog();
        showResultBread(stage, gamedata);
    }

    public void makeBread(){
        displayClear();
        setMenuLabel("パンを作る");
        boolean check = false;
        HBox contentBox = new HBox(60);
        VBox menuBox = new VBox(5);
        VBox stockBox = new VBox(5);
        VBox priceBox = new VBox(5);
        VBox makeButtonBox = new VBox(5);
        Label menuLabel[] = new Label[shop.getHasBreadRecipe().size()];
        Label stockLabel[] = new Label[shop.getHasBreadRecipe().size()];
        Label priceLabel[]  = new Label[shop.getHasBreadRecipe().size()];
        Button makeButton[] = new Button[shop.getHasBreadRecipe().size()];
        for (int i = 0; i < shop.getHasBreadRecipe().size(); i++) {
            final int index = i;
            for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getRecipe().getIngredients()){
                int item = shop.getInventory().getIngredient(recipe.getIngredientId());
                if(recipe.getQuantity() > item){
                    check = false;
                    break;
                }else{
                    check = true;
                }
            }
            menuLabel[i] = new Label();
            stockLabel[i] = new Label();
            priceLabel[i]  = new Label();
            makeButton[i]  = new Button();
            menuLabel[i].getStyleClass().add("menuLabel");
            menuLabel[i].setDisable(!check);
            stockLabel[i].getStyleClass().add("menuLabel");
            priceLabel[i].getStyleClass().add("menuLabel");
            makeButton[i].getStyleClass().add("decideButton");
            menuLabel[i].setText(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getName());
            stockLabel[i].setText(String.format("在庫%02d個",shop.getInventory().getBread(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getId())));
            priceLabel[i].setText(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getPrice() + "G");
            makeButton[i].setText("＜作る＞");
            int canMakeBread = 999;
            for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getRecipe().getIngredients()){
                canMakeBread = Math.min(canMakeBread,shop.getInventory().getIngredient(recipe.getIngredientId())/recipe.getQuantity());
            }
            makeButton[i].setOnAction(e -> {
                popupPane.getChildren().clear();
                getChildren().remove(popupPane);
                makeBreadPopup(index,menuLabel,stockLabel);
            });
            menuBox.getChildren().add(menuLabel[i]);
            stockBox.getChildren().add(stockLabel[i]);
            priceBox.getChildren().add(priceLabel[i]);
            makeButtonBox.getChildren().add(makeButton[i]);
            check = false;
        }
        contentBox.getChildren().addAll(menuBox,stockBox,priceBox,makeButtonBox);
        ScrollPane scrollpane = new ScrollPane(contentBox);
        scrollpane.setTranslateY(34);
        scrollpane.setPrefHeight(376);
        scrollpane.setMaxHeight(376);
        scrollpane.getStyleClass().add("transparent");
        centerContent.getChildren().add(scrollpane);
    }

    public void makeBreadPopup(int menu,Label[] menuLabel,Label[] stockLabel){
        getChildren().add(popupPane);
        VBox menuBox = new VBox(20);
        menuBox.setPadding(new Insets(20));
        popupPane.getChildren().add(menuBox);
        String name = shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getName();
        Label breadName = new Label(name);
        breadName.getStyleClass().add("popupTitleLabel");
        Label breadPrice = new Label("価格：" + shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getPrice() + "G");
        breadPrice.getStyleClass().add("popupLabel");
        Label breadStock = new Label("在庫：" + shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)) + "個");
        breadStock.getStyleClass().add("popupLabel");
        Label textNum = new Label("何個作成しますか？");
        textNum.getStyleClass().add("popupLabel");
        menuBox.getChildren().addAll(breadName,breadPrice,breadStock,textNum);
        final int[] index = {0};
        Label indexLabel = new Label(String.format("%2d",index[0]));
        indexLabel.getStyleClass().add("popupLabel");
        indexLabel.setPrefWidth(30);
        Button minusButton = new Button("－");
        Button plusButton = new Button("＋");
        Button minusTenButton = new Button("－10");
        Button plusTenButton = new Button("＋10");
        Button maxButton = new Button("MAX");
        minusButton.getStyleClass().add("plusMinusButton");
        minusButton.setPrefSize(30,30);
        plusButton.getStyleClass().add("plusMinusButton");
        plusButton.setPrefSize(30,30);
        minusTenButton.getStyleClass().add("plusMinusButton");
        minusTenButton.setPrefSize(60,30);
        plusTenButton.getStyleClass().add("plusMinusButton");
        plusTenButton.setPrefSize(60,30);
        maxButton.getStyleClass().add("plusMinusButton");
        maxButton.setPrefSize(60,30);
        HBox choiceBox = new HBox(15);
        choiceBox.getChildren().addAll(minusTenButton,minusButton,indexLabel,plusButton,plusTenButton,maxButton);
        Label ingredientTitle = new Label("必要食材");
        ingredientTitle.getStyleClass().add("popupLabel");
        menuBox.getChildren().addAll(choiceBox,ingredientTitle);
        VBox ingredientBox = new VBox(20);
        VBox colonBox = new VBox(20);
        VBox necessaryBox = new VBox(20);
        VBox slashBox = new VBox(20);
        VBox inventoryBox = new VBox(20);
        int recipeSize = shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients().size();
        Label[] ingredientLabel = new Label[recipeSize];
        Label[] colonLabel = new Label[recipeSize];
        Label[] necessaryLabel = new Label[recipeSize];
        int[] necessaryNum = new int[recipeSize];
        Label[] slashLabel = new Label[recipeSize];
        Label[] inventoryLabel = new Label[recipeSize];
        int[] inventoryNum = new int[recipeSize];
        for(int i=0;i<recipeSize;i++){
            ingredientLabel[i] = new Label(shop.getIngredients().get(shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients().get(i).getIngredientId()).getName());
            ingredientLabel[i].getStyleClass().add("popupLabel");
            ingredientBox.getChildren().add(ingredientLabel[i]);
            colonLabel[i] = new Label("：");
            colonLabel[i].getStyleClass().add("popupLabel");
            colonBox.getChildren().add(colonLabel[i]);
            necessaryNum[i] = shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients().get(i).getQuantity();
            necessaryLabel[i] = new Label(String.format("%3d", 0));
            necessaryLabel[i].getStyleClass().add("popupLabel");
            necessaryLabel[i].setPrefWidth(40);
            necessaryBox.getChildren().add(necessaryLabel[i]);
            slashLabel[i] = new Label("/");
            slashLabel[i].getStyleClass().add("popupLabel");
            slashBox.getChildren().add(slashLabel[i]);
            inventoryNum[i] = shop.getInventory().getIngredient(shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients().get(i).getIngredientId());
            inventoryLabel[i] = new Label("" + inventoryNum[i] + "　個");
            inventoryLabel[i].getStyleClass().add("popupLabel");
            inventoryBox.getChildren().add(inventoryLabel[i]);
        }
        int canMakeBread = 999;
        for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
            canMakeBread = Math.min(canMakeBread,shop.getInventory().getIngredient(recipe.getIngredientId())/recipe.getQuantity());
        }
        final int finalCanMakeBread = canMakeBread;
        HBox ingredientMenuBox = new HBox(20);
        Button decideButton = new Button("決定");
        decideButton.getStyleClass().add("popupDecideButton");
        decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        ingredientMenuBox.getChildren().addAll(ingredientBox,colonBox,necessaryBox,slashBox,inventoryBox);
        menuBox.getChildren().add(ingredientMenuBox);
        minusButton.setOnAction(e -> {
            index[0] = Math.max(index[0]-1,0);
            indexLabel.setText(String.format("%2d",index[0]));
            for(int i=0;i<recipeSize;i++){
                necessaryLabel[i].setText(String.format("%3d", necessaryNum[i] * index[0]));
            }
            decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        });
        plusButton.setOnAction(e -> {
            index[0] = Math.min(index[0]+1,99-shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)));
            indexLabel.setText(String.format("%2d",index[0]));
            for(int i=0;i<recipeSize;i++){
                necessaryLabel[i].setText(String.format("%3d", necessaryNum[i] * index[0]));
            }
            decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        });
        minusTenButton.setOnAction(e -> {
            index[0] = Math.max(index[0]-10,0);
            indexLabel.setText(String.format("%2d",index[0]));
            for(int i=0;i<recipeSize;i++){
                necessaryLabel[i].setText(String.format("%3d", necessaryNum[i] * index[0]));
            }
            decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        });
        plusTenButton.setOnAction(e -> {
            index[0] = Math.min(index[0]+10,99-shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)));
            indexLabel.setText(String.format("%2d",index[0]));
            for(int i=0;i<recipeSize;i++){
                necessaryLabel[i].setText(String.format("%3d", necessaryNum[i] * index[0]));
            }
            decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        });
        final int num  = Math.min(canMakeBread,99-shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)));
        maxButton.setOnAction(e -> {
            index[0] = num;
            indexLabel.setText(String.format("%2d",index[0]));
            for(int i=0;i<recipeSize;i++){
                necessaryLabel[i].setText(String.format("%3d", necessaryNum[i] * index[0]));
            }
            decideButton.setDisable(index[0] == 0||index[0] > finalCanMakeBread);
        });
        HBox buttonBox = new HBox(140);
        Button cancelButton = new Button("戻る");
        cancelButton.getStyleClass().add("popupCancelButton");
        buttonBox.getChildren().addAll(decideButton,cancelButton);
        decideButton.setOnAction(e -> {
            if(index[0] != 0){
                boolean check = true;
                for(int i=0;i<recipeSize;i++){
                    if(necessaryNum[i] * index[0] > inventoryNum[i]) {
                        check = false;
                        break;
                    }
                }
                if(!check){
                    addLog("食材が足りなくて、" + name + "を作成できませんでした");
                }else{
                    shop.getInventory().addBread(shop.getHasBreadRecipe().get(menu),index[0]);
                    for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
                        shop.getInventory().useIngredient(recipe.getIngredientId(), recipe.getQuantity() * index[0]);
                    }
                    addLog(name + "を" + index[0] + "個作成しました");
                }
                refreshLog();
                updateMenuLabel(menuLabel,stockLabel);
                popupPane.getChildren().clear();
                getChildren().remove(popupPane);
            }
        });
        cancelButton.setOnAction(e -> {
            popupPane.getChildren().clear();
            getChildren().remove(popupPane);
        });
        menuBox.getChildren().add(buttonBox);
    }

    private void updateMenuLabel(Label[] menuLabel,Label[] stockLabel){
        boolean check = false;
        for (int i = 0; i < shop.getHasBreadRecipe().size(); i++) {
            for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getRecipe().getIngredients()){
                int item = shop.getInventory().getIngredient(recipe.getIngredientId());
                if(recipe.getQuantity() > item){
                    check = false;
                    break;
                }else{
                    check = true;
                }
            }
            menuLabel[i].setDisable(!check);
            check = false;
            stockLabel[i].setText(String.format("在庫%02d個",shop.getInventory().getBread(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getId())));
        }
    }

    public void buyIngredient(){
        displayClear();
        setMenuLabel("材料を仕入れる");
        HBox contentBox = new HBox(5);
        VBox menuBox = new VBox(5);
        VBox stockColonBox = new VBox(5);
        VBox stockNumBox = new VBox(5);
        VBox stockUnitBox = new VBox(5);
        VBox priceBox = new VBox(5);
        VBox makeMinusButtonBox = new VBox(13);
        VBox makeMinusTenButtonBox = new VBox(13);
        VBox makeMinusHundredButtonBox = new VBox(13);
        VBox buyNumBox = new VBox(5);
        VBox makePlusButtonBox = new VBox(13);
        VBox makePlusTenButtonBox = new VBox(13);
        VBox makePlusHundredButtonBox = new VBox(13);
        Label menuLabel[] = new Label[shop.getHasIngredientType().size()];
        Label stockColon[] = new Label[shop.getHasIngredientType().size()];
        Label stockNumLabel[] = new Label[shop.getHasIngredientType().size()];
        Label stockUnitLabel[] = new Label[shop.getHasIngredientType().size()];
        Label priceLabel[]  = new Label[shop.getHasIngredientType().size()];
        Button makeMinusButton[] = new Button[shop.getHasIngredientType().size()];
        Button makeMinusTenButton[] = new Button[shop.getHasIngredientType().size()];
        Button makeMinusHundredButton[] = new Button[shop.getHasIngredientType().size()];
        int buyNum[] = new int[shop.getHasIngredientType().size()];
        Label buyNumLabel[] = new Label[shop.getHasIngredientType().size()];
        Button makePlusButton[] = new Button[shop.getHasIngredientType().size()];
        Button makePlusTenButton[] = new Button[shop.getHasIngredientType().size()];
        Button makePlusHundredButton[] = new Button[shop.getHasIngredientType().size()];
        Label totalPriceLabel = new Label(String.format("合計：%,dG", 0));
        totalPriceLabel.getStyleClass().add("totalPriceLabel");
        Button decideButton = new Button("決定");
        final int totalPrice[] = {0};
        decideButton.getStyleClass().add("popupDecideButton");
        decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
        for (int i = 0; i < shop.getHasIngredientType().size(); i++) {
            final int index = i;
            menuLabel[i] = new Label();
            menuLabel[i].setPrefWidth(130);
            menuLabel[i].setMaxWidth(130);
            stockColon[i] = new Label("在庫：");
            stockNumLabel[i] = new Label();
            stockUnitLabel[i] = new Label("個");
            priceLabel[i]  = new Label();
            makeMinusButton[i]  = new Button("‹");
            makeMinusTenButton[i] = new Button("‹‹");
            makeMinusHundredButton[i] = new Button("‹‹‹");
            buyNumLabel[i] = new Label("" + buyNum[i]);
            buyNumLabel[i].setPrefWidth(45);
            buyNumLabel[i].setAlignment(Pos.CENTER);
            makePlusButton[i] = new Button("›");
            makePlusTenButton[i] = new Button("››");
            makePlusHundredButton[i] = new Button("›››");
            menuLabel[i].getStyleClass().add("menuLabel");
            stockColon[i].getStyleClass().add("menuLabel");
            stockNumLabel[i].getStyleClass().add("menuLabel");
            stockNumLabel[i].setPrefWidth(45);
            stockNumLabel[i].setAlignment(Pos.CENTER);
            stockUnitLabel[i].getStyleClass().add("menuLabel");
            priceLabel[i].getStyleClass().add("menuLabel");
            makeMinusButton[i].getStyleClass().add("IngredientButton");
            makeMinusTenButton[i].getStyleClass().add("IngredientButton");
            makeMinusHundredButton[i].getStyleClass().add("IngredientButton");
            buyNumLabel[i].getStyleClass().add("menuLabel");
            makePlusButton[i].getStyleClass().add("IngredientButton");
            makePlusTenButton[i].getStyleClass().add("IngredientButton");
            makePlusHundredButton[i].getStyleClass().add("IngredientButton");
            makeMinusButtonBox.setMargin(makeMinusButton[i],new Insets(6,0,0,0));
            makeMinusTenButtonBox.setMargin(makeMinusTenButton[i],new Insets(6,0,0,0));
            makeMinusHundredButtonBox.setMargin(makeMinusHundredButton[i],new Insets(6,0,0,0));
            makePlusButtonBox.setMargin(makePlusButton[i],new Insets(6,0,0,0));
            makePlusTenButtonBox.setMargin(makePlusTenButton[i],new Insets(6,0,0,0));
            makePlusHundredButtonBox.setMargin(makePlusHundredButton[i],new Insets(6,0,0,0));
            menuLabel[i].setText(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getName());
            stockNumLabel[i].setText("" + shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getId()));
            priceLabel[i].setText(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getPrice() + "G");
            makeMinusButton[i].setOnAction(e -> {
                buyNum[index] = Math.max(buyNum[index] - 1,0);
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            makeMinusTenButton[i].setOnAction(e -> {
                buyNum[index] = Math.max(buyNum[index] - 10,0);
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            makeMinusHundredButton[i].setOnAction(e -> {
                buyNum[index] = Math.max(buyNum[index] - 100,0);
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            makePlusButton[i].setOnAction(e -> {
                buyNum[index] = Math.min(buyNum[index] + 1,999 - shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(index)).getId()));
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            makePlusTenButton[i].setOnAction(e -> {
                buyNum[index] = Math.min(buyNum[index] + 10,999 - shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(index)).getId()));
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            makePlusHundredButton[i].setOnAction(e -> {
                buyNum[index] = Math.min(buyNum[index] + 100,999 - shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(index)).getId()));
                buyNumLabel[index].setText("" + buyNum[index]);
                totalPrice[0] = calculationTotalPrice(buyNum);
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
            });
            menuBox.getChildren().add(menuLabel[i]);
            stockColonBox.getChildren().add(stockColon[i]);
            stockNumBox.getChildren().add(stockNumLabel[i]);
            stockUnitBox.getChildren().add(stockUnitLabel[i]);
            priceBox.getChildren().add(priceLabel[i]);
            makeMinusButtonBox.getChildren().add(makeMinusButton[i]);
            makeMinusTenButtonBox.getChildren().add(makeMinusTenButton[i]);
            makeMinusHundredButtonBox.getChildren().add(makeMinusHundredButton[i]);
            buyNumBox.getChildren().add(buyNumLabel[i]);
            makePlusButtonBox.getChildren().add(makePlusButton[i]);
            makePlusTenButtonBox.getChildren().add(makePlusTenButton[i]);
            makePlusHundredButtonBox.getChildren().add(makePlusHundredButton[i]);
        }
        contentBox.getChildren().addAll(menuBox,stockColonBox,stockNumBox,stockUnitBox,priceBox,makeMinusHundredButtonBox,makeMinusTenButtonBox,makeMinusButtonBox,buyNumBox,makePlusButtonBox,makePlusTenButtonBox,makePlusHundredButtonBox);
        ScrollPane scrollpane = new ScrollPane(contentBox);
        scrollpane.setPrefHeight(316);
        scrollpane.setMaxHeight(316);
        StackPane showTotalPrice = new StackPane();
        showTotalPrice.setPrefHeight(60);
        showTotalPrice.setMaxHeight(60);
        showTotalPrice.getStyleClass().add("showTotalPrice");
        HBox checkTotalPrice = new HBox(180);
        checkTotalPrice.getChildren().addAll(totalPriceLabel,decideButton);
        totalPriceLabel.setPrefWidth(200);
        showTotalPrice.getChildren().add(checkTotalPrice);
        checkTotalPrice.setTranslateY(8);
        VBox totalContent = new VBox(0);
        totalContent.setTranslateY(70);
        totalContent.getChildren().addAll(scrollpane,showTotalPrice);
        scrollpane.getStyleClass().add("transparent");
        centerContent.getChildren().add(totalContent);
        
        decideButton.setOnAction(e -> {
            if(totalPrice[0] > shop.getMoney()){
                addLog("お金が足りません");
            }else{
                for(int i=0;i<shop.getHasIngredientType().size(); i++){
                    if(buyNum[i]!=0){
                        addLog(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getName() + "を" + buyNum[i] + "個購入しました");
                        shop.getInventory().addIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getId(),buyNum[i]);
                        stockNumLabel[i].setText("" + shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getId()));
                        buyNum[i] = 0;
                        buyNumLabel[i].setText("" + buyNum[i]);
                    }
                }
                useMoney(totalPrice[0]);
                shop.getSalesHistory().addTodayCost(totalPrice[0]);
                totalPrice[0] = 0;
                totalPriceLabel.setText(String.format("合計：%,dG", totalPrice[0]));
                totalPriceLabel.setDisable(totalPrice[0] > shop.getMoney());
                decideButton.setDisable(totalPrice[0] > shop.getMoney() || totalPrice[0] == 0);
                updatePartTimeButton();
            }
            refreshLog();
        });
    }

    public int calculationTotalPrice(int[] buyNum){
        int totalPrice = 0;
        for(int i=0;i<shop.getHasIngredientType().size();i++){
            totalPrice += shop.getIngredients().get(shop.getHasIngredientType().get(i)).getPrice() * buyNum[i];
        }
        return totalPrice;
    }

    public void showInventory(){
        displayClear();
        setMenuLabel("在庫を見る");
        HBox contentBox = new HBox(0);
        VBox breadMenuBox = new VBox(0);
        VBox breadStockNumBox = new VBox(0);
        VBox breadStockUnitBox = new VBox(0);
        VBox ingredientMenuBox = new VBox(0);
        VBox ingredientStockNumBox = new VBox(0);
        VBox ingredientStockUnitBox = new VBox(0);
        Label breadMenuLabel[] = new Label[shop.getHasBreadRecipe().size() + 1];
        Label breadStockNumLabel[] = new Label[shop.getHasBreadRecipe().size() + 1];
        Label breadStockUnitLabel[] = new Label[shop.getHasBreadRecipe().size() + 1];
        Label ingredientMenuLabel[]  = new Label[shop.getHasIngredientType().size()];
        Label ingredientStockNumLabel[] = new Label[shop.getHasIngredientType().size()];
        Label ingredientStockUnitLabel[] = new Label[shop.getHasIngredientType().size()];

        for(int i = 0; i < shop.getHasBreadRecipe().size(); i++){
            breadMenuLabel[i] = new Label(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getName());
            breadStockNumLabel[i] = new Label("" + shop.getInventory().getBread(shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getId()));
            breadStockUnitLabel[i] = new Label("個");
            breadMenuLabel[i].getStyleClass().add("showInventoryLabel");
            breadStockNumLabel[i].getStyleClass().add("showInventoryLabel");
            breadStockUnitLabel[i].getStyleClass().add("showInventoryLabel");
            breadMenuLabel[i].setPrefWidth(150);
            breadStockNumLabel[i].setPrefWidth(45);
            breadStockNumLabel[i].setAlignment(Pos.CENTER);
            breadStockUnitLabel[i].setPrefWidth(95);
            breadMenuBox.getChildren().add(breadMenuLabel[i]);
            breadStockNumBox.getChildren().add(breadStockNumLabel[i]);
            breadStockUnitBox.getChildren().add(breadStockUnitLabel[i]);
        }
        breadMenuLabel[shop.getHasBreadRecipe().size()] = new Label("合計");
        breadStockNumLabel[shop.getHasBreadRecipe().size()] = new Label("" + shop.getInventory().getTotalBread());
        breadStockUnitLabel[shop.getHasBreadRecipe().size()] = new Label("個");
        breadMenuLabel[shop.getHasBreadRecipe().size()].getStyleClass().add("showInventoryLabel");
        breadStockNumLabel[shop.getHasBreadRecipe().size()].getStyleClass().add("showInventoryLabel");
        breadStockUnitLabel[shop.getHasBreadRecipe().size()].getStyleClass().add("showInventoryLabel");
        breadMenuLabel[shop.getHasBreadRecipe().size()].setPrefWidth(150);
        breadStockNumLabel[shop.getHasBreadRecipe().size()].setPrefWidth(45);
        breadStockNumLabel[shop.getHasBreadRecipe().size()].setAlignment(Pos.CENTER);
        breadStockUnitLabel[shop.getHasBreadRecipe().size()].setPrefWidth(95);
        breadMenuBox.getChildren().add(breadMenuLabel[shop.getHasBreadRecipe().size()]);
        breadStockNumBox.getChildren().add(breadStockNumLabel[shop.getHasBreadRecipe().size()]);
        breadStockUnitBox.getChildren().add(breadStockUnitLabel[shop.getHasBreadRecipe().size()]);
        
        for(int i = 0; i < shop.getHasIngredientType().size(); i++){
            ingredientMenuLabel[i] = new Label(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getName());
            ingredientStockNumLabel[i] = new Label("" + shop.getInventory().getIngredient(shop.getIngredients().get(shop.getHasIngredientType().get(i)).getId()));
            ingredientStockUnitLabel[i] = new Label("個");
            ingredientMenuLabel[i].getStyleClass().add("showInventoryLabel");
            ingredientStockNumLabel[i].getStyleClass().add("showInventoryLabel");
            ingredientStockUnitLabel[i].getStyleClass().add("showInventoryLabel");
            ingredientMenuLabel[i].setPrefWidth(150);
            ingredientStockNumLabel[i].setPrefWidth(45);
            ingredientStockNumLabel[i].setAlignment(Pos.CENTER);
            ingredientMenuBox.getChildren().add(ingredientMenuLabel[i]);
            ingredientStockNumBox.getChildren().add(ingredientStockNumLabel[i]);
            ingredientStockUnitBox.getChildren().add(ingredientStockUnitLabel[i]);
        }
        contentBox.getChildren().addAll(breadMenuBox,breadStockNumBox,breadStockUnitBox,ingredientMenuBox,ingredientStockNumBox,ingredientStockUnitBox);
        contentBox.setTranslateX(10);
        ScrollPane scrollpane = new ScrollPane(contentBox);
        scrollpane.setPrefHeight(331);
        scrollpane.setMaxHeight(331);
        HBox decidTypeLabel = new HBox(200);
        decidTypeLabel.setPrefHeight(35);
        decidTypeLabel.setMaxHeight(35);
        Label breadLabel = new Label("＜パン＞");
        breadLabel.getStyleClass().add("menuLabel");
        Label ingredientLabel = new Label("＜材料＞");
        ingredientLabel.getStyleClass().add("menuLabel");
        decidTypeLabel.getChildren().addAll(breadLabel,ingredientLabel);
        VBox totalContent = new VBox(0);
        totalContent.setTranslateY(70);
        totalContent.getChildren().addAll(decidTypeLabel,scrollpane);
        scrollpane.getStyleClass().add("transparent");
        centerContent.getChildren().add(totalContent);
    }

    public void showShop(){
        displayClear();
        setMenuLabel("店を確認する");
        ProgressBar popularityBar = new ProgressBar();
        popularityBar.setProgress((double)shop.getPopularity()/100);
        popularityBar.setPrefSize(200,20);
        popularityBar.getStyleClass().add("popularityBar");
        Label popularityLabel = new Label("人気度：" + shop.getPopularity());
        popularityLabel.getStyleClass().add("largeMenuLabel");
        VBox popularityBox = new VBox(10);
        popularityBox.getChildren().addAll(popularityLabel,popularityBar);
        Label shopLebelLabel = new Label("店舗レベル：Lv." + shop.getLevel());
        shopLebelLabel.getStyleClass().add("largeMenuLabel");
        Label shopLebelStar = new Label("★ ".repeat(shop.getLevel()) + "☆".repeat(5 - shop.getLevel()));
        shopLebelStar.getStyleClass().add("largeMenuLabel");
        shopLebelStar.setTranslateX(25);
        VBox shopLebelBox = new VBox(0);
        shopLebelBox.getChildren().addAll(shopLebelLabel,shopLebelStar);
        HBox topContent = new HBox(130);
        topContent.getChildren().addAll(popularityBox,shopLebelBox);
        Label managementLabel = new Label("経営状況");
        managementLabel.getStyleClass().add("largeMenuLabel");
        Label totalSalesLabel = new Label("累計売上：");
        totalSalesLabel.getStyleClass().add("menuLabel");
        Label totalSalesNumLabel = new Label(String.format("%,dG",shop.getSalesHistory().getTotalSales()));
        totalSalesNumLabel.setPrefWidth(120);
        totalSalesNumLabel.setAlignment(Pos.CENTER_RIGHT);
        totalSalesNumLabel.getStyleClass().add("menuLabel");
        Label totalProfitLabel = new Label("累計利益：");
        totalProfitLabel.getStyleClass().add("menuLabel");
        Label totalProfitNumLabel = new  Label(String.format("%,dG", shop.getSalesHistory().getTotalProfit()));
        totalProfitNumLabel.setPrefWidth(120);
        totalProfitNumLabel.setAlignment(Pos.CENTER_RIGHT);
        totalProfitNumLabel.getStyleClass().add("menuLabel");
        VBox salesLabelBox = new VBox(10);
        salesLabelBox.getChildren().addAll(totalSalesLabel,totalProfitLabel);
        VBox salesLabelNumBox = new VBox(10);
        salesLabelNumBox.getChildren().addAll(totalSalesNumLabel,totalProfitNumLabel);
        HBox salesBox = new HBox(20);
        salesBox.getChildren().addAll(salesLabelBox,salesLabelNumBox);
        VBox managementBox = new VBox(10);
        managementBox.getChildren().addAll(managementLabel,salesBox);
        Label nextLevelLabel = new Label("次のレベルまで");
        nextLevelLabel.getStyleClass().add("largeMenuLabel");
        VBox nextLevelBox = new VBox(15);
        nextLevelBox.getChildren().add(nextLevelLabel);
        if(shop.getLevel() == 5){
            Label maxLevelLabel = new Label("最大レベルです");
            maxLevelLabel.getStyleClass().add("menuLabel");
            nextLevelBox.getChildren().add(maxLevelLabel);
        }else{
            ProgressBar nextLevelBar = new ProgressBar();
            nextLevelBar.setProgress((double)shop.getSalesHistory().getTotalSales()/shop.getNextLevelSales());
            nextLevelBar.setPrefSize(200,20);
            nextLevelBar.getStyleClass().add("popularityBar");
            nextLevelBar.setTranslateY(8);
            Label nowTotalSalesLabel = new Label(String.format("%,d",shop.getSalesHistory().getTotalSales()));
            nowTotalSalesLabel.setPrefWidth(90);
            nowTotalSalesLabel.setAlignment(Pos.CENTER);
            nowTotalSalesLabel.getStyleClass().add("menuLabel");
            Label salesSlash = new Label("/");
            salesSlash.getStyleClass().add("menuLabel");
            Label nextLevelSalesLabel = new Label(String.format("%,dG",shop.getNextLevelSales()));
            nextLevelSalesLabel.getStyleClass().add("menuLabel");
            nextLevelSalesLabel.setPrefWidth(100);
            nextLevelSalesLabel.setAlignment(Pos.CENTER);
            HBox nextLevelSalesBox = new HBox(0);
            nextLevelSalesBox.getChildren().addAll(nowTotalSalesLabel,salesSlash,nextLevelSalesLabel);
            nextLevelSalesBox.setTranslateY(5);
            nextLevelBox.getChildren().addAll(nextLevelBar,nextLevelSalesBox);
        }

        HBox bottomBox = new HBox(90);
        bottomBox.getChildren().addAll(managementBox,nextLevelBox);
        VBox totalContent = new VBox(80);
        totalContent.getChildren().addAll(topContent,bottomBox);
        totalContent.setTranslateY(80);
        totalContent.setTranslateX(10);

        centerContent.getChildren().add(totalContent);
    }

    public void promotion(){
        displayClear();
        setMenuLabel("宣伝をする");
        HBox contentBox = new HBox(10);
        VBox nameBox = new VBox(5);
        VBox costBox = new VBox(5);
        VBox popularityBox = new VBox(5);
        VBox popularityNumBox = new VBox(5);
        VBox decideButtonBox = new VBox(5);
        Label nameLabel[] = new Label[shop.getPromotions().size()];
        Label costLabel[] = new Label[shop.getPromotions().size()];
        Label popularityLabel[] = new Label[shop.getPromotions().size()];
        Label popularityNumLabel[] = new Label[shop.getPromotions().size()];
        Button decideButton[] = new Button[shop.getPromotions().size()];
        for(int i=0;i<shop.getPromotions().size();i++){
            final int index = i;
            nameLabel[i] = new Label(shop.getPromotions().get(i).getChoiceMessage());
            costLabel[i] = new Label(String.format("%,dG", shop.getPromotions().get(i).getCost()));
            popularityLabel[i] = new Label("人気度＋");
            popularityNumLabel[i] = new Label("" + shop.getPromotions().get(i).getPop());
            decideButton[i] = new Button("＜宣伝＞");
            nameLabel[i].getStyleClass().add("menuLabel");
            costLabel[i].getStyleClass().add("menuLabel");
            popularityLabel[i].getStyleClass().add("menuLabel");
            popularityNumLabel[i].getStyleClass().add("menuLabel");
            decideButton[i].getStyleClass().add("decideButton");
            nameLabel[i].setPrefWidth(200);
            costLabel[i].setPrefWidth(80);
            costLabel[i].setAlignment(Pos.CENTER);
            popularityNumLabel[i].setPrefWidth(40);
            popularityNumLabel[i].setAlignment(Pos.CENTER);
            nameBox.getChildren().add(nameLabel[i]);
            costBox.getChildren().add(costLabel[i]);
            popularityBox.getChildren().add(popularityLabel[i]);
            popularityNumBox.getChildren().add(popularityNumLabel[i]);
            decideButtonBox.getChildren().add(decideButton[i]);
            decideButton[i].setOnAction(e -> {
                if(useMoney(shop.getPromotions().get(index).getCost())){
                    addLog(shop.getPromotions().get(index).getViewMessage());
                    addLog("人気度が" + shop.getPromotions().get(index).getPop() + "あがりました");
                    refreshLog();
                    addPopularity(shop.getPromotions().get(index).getPop());
                    shop.getSalesHistory().addTodayPromotionCost(shop.getPromotions().get(index).getCost());
                    shop.getPromotions().get(index).setCheck(true);
                    updatePromotionLabel(nameLabel, costLabel, popularityLabel, popularityNumLabel,decideButton);
                }
            });
        }
        updatePromotionLabel(nameLabel, costLabel, popularityLabel, popularityNumLabel,decideButton);
        contentBox.getChildren().addAll(nameBox,costBox,popularityBox,popularityNumBox,decideButtonBox);
        popularityBox.setTranslateX(15);
        contentBox.setTranslateY(70);
        centerContent.getChildren().add(contentBox);
    }

    private void updatePromotionLabel(Label[] nameLabel,Label[] costLabel,Label[] popularityLabel,Label[] popularityNumLabel,Button[] decideButton){
        for(int i=0;i<shop.getPromotions().size();i++){
            nameLabel[i].setDisable(shop.getPromotions().get(i).getCheck()||shop.getMoney() < shop.getPromotions().get(i).getCost());
            costLabel[i].setDisable(shop.getPromotions().get(i).getCheck()||shop.getMoney() < shop.getPromotions().get(i).getCost());
            popularityLabel[i].setDisable(shop.getPromotions().get(i).getCheck()||shop.getMoney() < shop.getPromotions().get(i).getCost());
            popularityNumLabel[i].setDisable(shop.getPromotions().get(i).getCheck()||shop.getMoney() < shop.getPromotions().get(i).getCost());
            decideButton[i].setDisable(shop.getPromotions().get(i).getCheck()||shop.getMoney() < shop.getPromotions().get(i).getCost());
        }
    }

    public void showResultBread(Stage stage,GameData gamedata){
        displayClear();
        HBox contentBox = new HBox(100);
        resultBox = new HBox(2);
        expiredStockBox = new HBox(2);
        Label salesResultLabel = new Label("販売結果");
        salesResultLabel.getStyleClass().add("popupTitleLabel");
        centerContent.getChildren().add(salesResultLabel);
        centerContent.setAlignment(salesResultLabel,Pos.TOP_LEFT);
        salesResultLabel.setTranslateY(10);
        salesResultLabel.setTranslateX(80);
        resultBox.getChildren().addAll(resultNameBox,resultNumBox,resultUnitBox);
        expiredStockLabel.getStyleClass().add("popupTitleLabel");
        centerContent.setAlignment(expiredStockLabel,Pos.TOP_CENTER);
        if(checkRemove){
            centerContent.getChildren().add(expiredStockLabel);
        }
        expiredStockLabel.setTranslateY(10);
        expiredStockLabel.setTranslateX(135);
        expiredStockBox.getChildren().addAll(expiredStockNameBox,expiredStockNumBox,expiredStockUnitBox);
        Button nextButton = new Button("次へ ▶");
        nextButton.setOnAction(e -> {
            showResultSales(stage,gamedata);
        });
        nextButton.getStyleClass().add("decideButton");
        centerContent.setAlignment(nextButton,Pos.BOTTOM_RIGHT);
        nextButton.setTranslateX(-30);
        nextButton.setTranslateY(4);
        contentBox.getChildren().addAll(resultBox,expiredStockBox);
        centerContent.getChildren().add(contentBox);
        centerContent.getChildren().add(nextButton);
        contentBox.setTranslateY(55);
        contentBox.setTranslateX(40);
    }

    public void showResultSales(Stage stage,GameData gamedata){
        displayClear();
        Label salesLabel = new Label("今日の売上");
        salesLabel.getStyleClass().add("salesTopLabel");
        centerContent.getChildren().add(salesLabel);
        centerContent.setAlignment(salesLabel,Pos.TOP_CENTER);
        int profit = shop.getSalesHistory().getTodaySales() - shop.getSalesHistory().getTodayCost() - shop.getSalesHistory().getTodayPromotionCost();
        HBox contentBox = new HBox(0);
        VBox titleBox = new VBox(5);
        VBox numBox = new VBox(5);
        VBox unitBox = new VBox(5);
        Label customerTitleLabel = new Label("来客人数");
        Label customerNumLabel = new Label("" + shop.getSalesHistory().getTodayCustomers());
        Label customerUnitLabel = new Label("人");
        Label soldBreadTitleLabel = new Label("販売数");
        Label soldBreadNumLabel = new Label("" + shop.getSalesHistory().getTodaySoldBread());
        Label soldBreadUnitLabel = new Label("個");
        Label costTitleLabel = new Label("仕入れ額");
        Label costNumLabel = new Label(String.format("%,d", shop.getSalesHistory().getTodayCost()));
        Label costUnitLabel = new Label("G");
        Label salesTitleLabel = new Label("売上額");
        Label salesNumLabel = new Label(String.format("%,d", shop.getSalesHistory().getTodaySales()));
        Label salesUnitLabel = new Label("G");
        Label promotionCostTitleLabel = new Label("広告費");
        Label promotionCostNumLabel = new Label(String.format("%,d", shop.getSalesHistory().getTodayPromotionCost()));
        Label promotionCostUnitLabel = new Label("G");
        Label profitTitleLabel = new Label("利益");
        Label profitNumLabel = new Label(String.format("%,d", profit));
        Label profitUnitLabel = new Label("G");
        Label[] titleLabel = {customerTitleLabel,soldBreadTitleLabel,salesTitleLabel,costTitleLabel,promotionCostTitleLabel,profitTitleLabel};
        Label[] numLabel = {customerNumLabel,soldBreadNumLabel,salesNumLabel,costNumLabel,promotionCostNumLabel,profitNumLabel};
        Label[] unitLabel = {customerUnitLabel,soldBreadUnitLabel,salesUnitLabel,costUnitLabel,promotionCostUnitLabel,profitUnitLabel};
        for(Label label : titleLabel){
            label.getStyleClass().add("salesLabel");
            titleBox.getChildren().add(label);
            label.setPrefWidth(120);
        }
        for(Label label : numLabel){
            label.getStyleClass().add("salesLabel");
            numBox.getChildren().add(label);
            label.setPrefWidth(100);
            label.setAlignment(Pos.CENTER);
        }
        for(Label label : unitLabel){
            label.getStyleClass().add("salesLabel");
            unitBox.getChildren().add(label);
            label.setPrefWidth(30);
            label.setAlignment(Pos.CENTER);
        }
        Button nextButton = new Button("次の日へ ▶");
        nextButton.setOnAction(e -> {
            nextDay(stage,gamedata);
        });
        Button backButton = new Button("◀戻る");
        backButton.setOnAction(e -> {
            showResultBread(stage,gamedata);
        });
        Button finishButton = new Button("＜終わる＞");
        finishButton.setOnAction(e -> {
            ending(stage,gamedata);
        });
        nextButton.getStyleClass().add("decideButton");
        backButton.getStyleClass().add("decideButton");
        finishButton.getStyleClass().add("decideButton");
        centerContent.setAlignment(nextButton,Pos.BOTTOM_RIGHT);
        centerContent.setAlignment(backButton,Pos.BOTTOM_LEFT);
        centerContent.setAlignment(finishButton,Pos.BOTTOM_CENTER);
        nextButton.setTranslateX(-30);
        nextButton.setTranslateY(4);
        backButton.setTranslateX(30);
        backButton.setTranslateY(4);
        finishButton.setTranslateY(4);
        contentBox.getChildren().addAll(titleBox,numBox,unitBox);
        centerContent.getChildren().add(contentBox);
        centerContent.getChildren().addAll(nextButton,backButton);
        if(gamedata.getMode() == Mode.ENDLESS){
            centerContent.getChildren().add(finishButton);
        }
        contentBox.setTranslateY(90);
        contentBox.setTranslateX(170);
    }

    public void resetPromotion(){
        for(Promotion p:shop.getPromotions()){
            p.setCheck(false);
        }
    }

    public void addMoney(int num){
        shop.addMoney(num);
        haveMoney.setText(String.format("所持金：%,7dG",shop.getMoney()));
    }

    public boolean useMoney(int num){
        boolean bool = shop.useMoney(num);
        haveMoney.setText(String.format("所持金：%,7dG",shop.getMoney()));
        if(!bool){
            addLog("お金が足りません");
            refreshLog();
        }
        return bool;
    }

    public void checkLevel(){
        List<String> logList = shop.checkLevel();
        shopLevel.setText("店舗レベル：" + shop.getLevel());
        for(String log : logList){
            addLog(log);
        }
        refreshLog();
    }

    public void addPopularity(int num){
        shop.addPopularity(num);
        guessCustomer.setText("来客予想：" + shop.getPopularity() + "～" + (shop.getPopularity() + shop.getLevel() * 6) + "人");
    }

    public void switchMenuLabel(boolean bool){
        for(Button button : morningMassages){
            displayClear();
            button.setDisable(bool);
        }        
    }

    public void addLog(String message) {
        logs.add(message);
        if (logs.size() > 50) {
            logs.remove(0);
        }
    }

    public void refreshLog() {
        logArea.setText(String.join("\n", logs) + "\n");

        Platform.runLater(() -> {
            logArea.positionCaret(logArea.getLength());
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void setMenuLabel(String str){
        menuTitle.setText(str);
        centerContent.getChildren().add(menuTitleBackGround);
    }

    public void displayClear(){
        popupPane.getChildren().clear();
        centerContent.getChildren().clear();
        getChildren().remove(popupPane);
        refreshLog();
    }

    private boolean canMakeAnyBread() {
        for (BreadType type : shop.getHasBreadRecipe()) {
            boolean canMake = true;
            for (RecipeIngredient recipe : shop.getBreads().get(type).getRecipe().getIngredients()) {
                int item = shop.getInventory().getIngredient(recipe.getIngredientId());
                if (recipe.getQuantity() > item) {
                    canMake = false;
                    break;
                }
            }
            if (canMake) {
                return true;
            }
        }
        return false;
    }

    private void updatePartTimeButton(){
        boolean condition = !canMakeAnyBread() && shop.getMoney() <= 100 && shop.getInventory().getTotalBread() <= closeBread;
        doPartButton.setVisible(condition);
    }
    
    public void doPartTime(Stage stage,GameData gamedata){
        displayClear();
        Label titleLabel = new Label("アルバイト");
        titleLabel.getStyleClass().add("popupTitleLabel");
        VBox contentBox = new VBox(20);
        contentBox.getChildren().add(titleLabel);
        contentBox.setMargin(titleLabel, new Insets(0, 0, 60, 0));
        Label[] massages = {
            new Label("近所のパン屋さんから"),
            new Label("配達の手伝いを頼まれました")
        };
        for(Label label : massages){
            label.getStyleClass().add("popupLabel");
            contentBox.getChildren().add(label);
        }
        Label rewardLabel = new Label("報酬：500G");
        rewardLabel.getStyleClass().add("popupLabel");
        Button okButton = new Button("[働く]");
        Button cancelButton = new Button("[キャンセル]");
        okButton.getStyleClass().add("decideButton");
        cancelButton.getStyleClass().add("decideButton");
        HBox buttonBox = new HBox(100);
        buttonBox.setTranslateX(80);
        contentBox.setPrefWidth(450);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setTranslateY(-30);
        buttonBox.getChildren().addAll(okButton,cancelButton);
        contentBox.setMargin(buttonBox, new Insets(30, 0, 0, 0));
        contentBox.getChildren().addAll(rewardLabel,buttonBox);
        getChildren().add(popupPane);
        popupPane.getChildren().add(contentBox);
        Label label = new Label("配達中...");
        label.getStyleClass().add("popupTitleLabel");
        Button nextDay = new Button("＜次の日へ＞");
        nextDay.getStyleClass().add("decideButton");
        nextDay.setTranslateY(100);
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("workProgress");
        progressBar.setProgress(0.0);
        VBox okBox = new VBox(80);
        okBox.setPrefSize(450,300);
        okBox.setAlignment(Pos.CENTER);
        okBox.setTranslateY(-60);
        okBox.getChildren().addAll(label,progressBar);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(3),
                new KeyValue(progressBar.progressProperty(), 1))
        );
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            nextDay(stage, gamedata);
        });
        okButton.setOnAction(e -> {
            popupPane.getChildren().clear();
            popupPane.getChildren().addAll(okBox);
            timeline.play();
        });
        cancelButton.setOnAction(e -> {
            displayClear();
            switchMenuLabel(false);
        });
        timeline.setOnFinished(e -> {
            popupPane.getChildren().add(nextDay);
            label.setText("配達完了");
            addMoney(500);
            addLog("アルバイトで500G獲得しました");
        });
        nextDay.setOnAction(e -> {
            nextDay(stage, gamedata);
        });
    }

    public void nextDay(Stage stage,GameData gamedata){
        displayClear();
        if(gamedata.getMode() == Mode.NORMAL){
            if(day == 30){
                ending(stage,gamedata);
            }
        }
        day++;
        startDay(stage, gamedata);
    }

    public void ending(Stage stage,GameData gamedata){
        getChildren().remove(girlView);
        layout.getChildren().clear();
        setContent(new EndingScene(stage,gamedata,shop,day,breadMap));
    }
}
