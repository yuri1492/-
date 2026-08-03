import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DayManager {
    private int day = 0;
    private Shop shop;
    private int IngredientMaxLength;
    private int BreadMaxLength;
    private Map<BreadType, Integer> breads;
    private boolean checkPop;
    private final int maxCommand = 6;
    private final int closeBread;
    private final int upPopBread = 5;
    private final int upPopNum = 2;
    private final String[] morningMassages = {
        "パンを作る",
        "材料を仕入れる",
        "在庫を見る",
        "店を確認する",
        "宣伝をする",
        "営業を開始する"
    };

    public DayManager(Shop shop,int ingredientMaxLength,int breadMaxLength,int closeBread) {
        this.shop = shop;
        this.IngredientMaxLength = ingredientMaxLength;
        this.BreadMaxLength = breadMaxLength;
        this.closeBread = closeBread;
        breads = new LinkedHashMap<>();
    }

    public int getDay(){
        return day;
    }

    public int startDay(){
        day ++;
        shop.getSalesHistory().resetTodaydata();
        resetPromotion();
        checkPop = false;
        for(BreadType type : shop.getHasBreadRecipe()){
            breads.put(type, shop.getInventory().getBread(type));
        }
        while (true) {
            System.out.println(day + "日目");
            System.out.println("来客予想：" + shop.getPopularity() + "～" + (shop.getPopularity() + shop.getLevel() * 6) + "人\n");
            System.out.println("===== 朝の作業 =====");
            for(int i=0;i<morningMassages.length;i++){
                System.out.println((i + 1) + "．" + morningMassages[i]);
            }
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
                if(check == true) break;
                check = false;
            }
            boolean condition = !check && shop.getMoney() <= 100 && shop.getInventory().getTotalBread() <= closeBread;
            if(condition){
                System.out.println((maxCommand + 1) + "．アルバイトをする");
            }
            System.out.println();
            int command;
            if(condition){
                command = InputOutputManager.inputNumber(1, maxCommand + 1);
            }else{
                command = InputOutputManager.inputNumber(1, maxCommand);
            }

            switch (command) {
                case 1:
                    makeBread();
                    break;
                case 2:
                    buyIngredient();
                    break;
                case 3:
                    checkInventory();
                    break;
                case 4:
                    checkShop();
                    break;
                case 5:
                    promotion();
                    break;
                case 6:
                    int totalBread = shop.getInventory().getTotalBread();
                    if(totalBread == 0){
                        InputOutputManager.coloerPrintl("パンがないので、営業ができません！\n", Color.YELLOW);
                        continue;
                    }
                    return totalBread;
                case 7:
                    partJob();
                    return shop.getInventory().getTotalBread();
            }
        }
    }

    public int startSales(){
        System.out.println(day + "日目の営業を開始します");
        Random random = new Random();
        shop.getSalesHistory().setTodayCustomers(shop.getPopularity() + random.nextInt(shop.getLevel() + 6));
        Map<BreadType,Integer> breadMap = new LinkedHashMap<>();
        List<BreadType> breadList = new ArrayList<>();
        for(int i=0;i<shop.getHasBreadRecipe().size();i++){
            if(shop.getInventory().getBread(shop.getHasBreadRecipe().get(i)) > 0){
                breadList.add(shop.getHasBreadRecipe().get(i));
                breadMap.put(shop.getHasBreadRecipe().get(i), 0);
            }
        }
        int breadKinds = breadList.size();
        System.out.println("販売中...");
        InputOutputManager.wait(600);
        if(shop.getHasBreadRecipe().size() - 2 > breadKinds){
            int pop = shop.getHasBreadRecipe().size() - breadKinds;
            shop.addPopularity(pop * -1);
            System.out.println("商品の種類が少ないので、人気度が" + pop + "さがりました");
            checkPop = true;
        }else if (breadKinds >= upPopBread) {
            shop.addPopularity(upPopNum);
            System.out.println("商品が" + upPopBread + "種類を超えているので、人気度が" + upPopNum + "上がりました");
        }
        for(int i=0;i<shop.getSalesHistory().getTodayCustomers();i++){
            BreadType type = breadList.get(random.nextInt(breadList.size()));
            if(shop.getInventory().getBread(type) > 0){
                shop.getInventory().useBread(type, 1);
                shop.getSalesHistory().addTodaySales(shop.getBreads().get(type).getPrice());
                shop.getSalesHistory().addSoldBread(type, 1);
                shop.getSalesHistory().addTodaySoldBread();
                shop.addMoney(shop.getBreads().get(type).getPrice());
                breadMap.put(type, breadMap.get(type) + 1);
            }else if(random.nextInt(2) == 0){
                if(shop.getInventory().getTotalBread() != 0){
                    while (true) {
                        type = breadList.get(random.nextInt(breadList.size()));
                        if(shop.getInventory().getBread(type) > 0){
                            shop.getInventory().useBread(type, 1);
                            shop.getSalesHistory().addTodaySales(shop.getBreads().get(type).getPrice());
                            shop.getSalesHistory().addSoldBread(type, 1);
                            shop.getSalesHistory().addTodaySoldBread();
                            shop.addMoney(shop.getBreads().get(type).getPrice());
                            breadMap.put(type, breadMap.get(type) + 1);
                            break;
                        }
                    }
                }
            }
        }
        InputOutputManager.wait(900);
        System.out.println();
        System.out.println("営業終了");
        System.out.println("来客人数：" + shop.getSalesHistory().getTodayCustomers() + "人");
        System.out.println("\n===== 販売結果 =====");
        for (Map.Entry<BreadType, Integer> entry : breadMap.entrySet()) {
            String name = shop.getBreads().get(entry.getKey()).getName();
            System.out.println(name + " ".repeat(InputOutputManager.repeatNum(BreadMaxLength,name.length())) + "：" + entry.getValue() + "個");
        }
        boolean checkRemove = false;
        for (Map.Entry<BreadType, Integer> entry : breads.entrySet()) {
            String name = shop.getBreads().get(entry.getKey()).getName();
            int deadline;
            if(breadMap.get(entry.getKey()) == null){
                deadline = entry.getValue() - 0;
            }else{
                deadline = entry.getValue() - breadMap.get(entry.getKey());
            }
            if(deadline > 0){
                if(!checkRemove){
                    checkRemove = true;
                    System.err.println();
                    System.out.println("期限が来たので、以下のものを処分しました");
                }
                shop.getInventory().useBread(entry.getKey(), deadline);
                System.out.println("・" + name + "：" + deadline + "個");
            }
        }
        System.out.println();
        return shop.getSalesHistory().getTodaySoldBread();
    }

    public void endDay(int totalBread,int soldBread){
        if(totalBread != 0){
            double rate = (double) soldBread / totalBread;
            if(checkPop){
                if(shop.getSalesHistory().getTodayCustomers() / 2 > totalBread){
                    shop.addPopularity(-3);
                    System.out.println("商品が少なすぎたので、人気度が3さがりました");
                    if(totalBread == soldBread) shop.getSalesHistory().addSoldOut();

                }else{
                    if(totalBread == soldBread){
                        shop.addPopularity(2);
                        System.out.println("商品が完売したので、人気度が2あがりました");
                        shop.getSalesHistory().addSoldOut();
                    } else if(rate >= 0.7) {
                        shop.addPopularity(1);
                        System.out.println("商品がたくさん売れたので、人気度が1あがりました");
                    } else if (rate >= 0.4) {
                        System.out.println("商品が少し売れましたが、人気度は変わりませんでした");
                        // 変化なし
                    } else {
                        shop.addPopularity(-2);
                        System.out.println("商品があまり売れなかったので、人気度が2下がってしまいました");
                    }
                }
            }else{
                if(shop.getSalesHistory().getTodayCustomers() / 2 > totalBread){
                    shop.addPopularity(-4);
                    System.out.println("商品が少なすぎたので、人気度が4さがりました");
                    if(totalBread == soldBread) shop.getSalesHistory().addSoldOut();

                }else{
                    if(totalBread == soldBread){
                        shop.addPopularity(4);
                        System.out.println("商品が完売したので、人気度が4あがりました");
                        shop.getSalesHistory().addSoldOut();
                    } else if(rate >= 0.9) {
                        shop.addPopularity(3);
                        System.out.println("商品がたくさん売れたので、人気度が3あがりました");
                    } else if (rate >= 0.7) {
                        shop.addPopularity(2);
                        System.out.println("商品がそこそこ売れたので、人気度が2あがりました");
                    } else if (rate >= 0.5) {
                        shop.addPopularity(1);
                        System.out.println("商品がまあまあ売れたので、人気度が1あがりました");
                    } else if (rate >= 0.3) {
                        System.out.println("商品が少し売れましたが、人気度は変わりませんでした");
                        // 変化なし
                    } else {
                        shop.addPopularity(-2);
                        System.out.println("商品があまり売れなかったので、人気度が2下がってしまいました");
                    }
                }
            }
            InputOutputManager.wait(800);
            System.out.println();
            InputOutputManager.coloerPrintl("===== 今日の売上 =====", Color.YELLOW);
            int profit = shop.getSalesHistory().getTodaySales() - shop.getSalesHistory().getTodayCost() - shop.getSalesHistory().getTodayPromotionCost();
            System.out.println("今日の仕入れ額：" + String.format("%,d", shop.getSalesHistory().getTodayCost()) + "G");
            System.out.println("今日の売上額　：" + String.format("%,d", shop.getSalesHistory().getTodaySales()) + "G");
            System.out.println("今日の広告費　：" + String.format("%,d", shop.getSalesHistory().getTodayPromotionCost()) + "G");
            System.out.printf("今日の利益　　：");
            if(profit < 0){
                InputOutputManager.coloerSet(Color.RED);
            }
            shop.getSalesHistory().addTotalProfit(profit);
            System.out.println(String.format("%,d", profit) + "G");
            shop.getSalesHistory().updateHighSalses(shop.getSalesHistory().getTodaySales(), day);
            InputOutputManager.colorReset();
        }
        Random random = new Random();
        if(random.nextInt(3) <= 1) shop.addPopularity(random.nextInt(shop.getLevel()) * -1);
        shop.checkLevel();
        System.out.println();
        InputOutputManager.wait(800);
    }

    public void makeBread(){
        while(true){
            System.out.println("===== 作成可能パン =====");
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
                if(!check) InputOutputManager.coloerSet(Color.GRAY);
                System.out.printf("%2d",i + 1);
                System.out.println("：" + shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getName() + " ".repeat(InputOutputManager.repeatNum(BreadMaxLength,shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getName().length())) + shop.getBreads().get(shop.getHasBreadRecipe().get(i)).getPrice() + "G");
                InputOutputManager.colorReset();
                check = false;
            }
            System.out.printf("%2d：もどる\n\n",shop.getHasBreadRecipe().size() + 1);
            int menu = InputOutputManager.inputNumber(1, shop.getHasBreadRecipe().size() + 1);
            if(menu == shop.getHasBreadRecipe().size() + 1) return;
            menu --;
            String name = shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getName();
            System.out.println(name + "の作成には以下の材料が必要です。");
            int canMakeBread = 999;
            for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
                String ingredientName = shop.getIngredients().get(recipe.getIngredientId()).getName();
                int quantity = recipe.getQuantity();
                canMakeBread = Math.min(canMakeBread,shop.getInventory().getIngredient(recipe.getIngredientId())/quantity);
                System.out.println(ingredientName + " ".repeat(InputOutputManager.repeatNum(IngredientMaxLength,ingredientName.length())) + quantity + "個　(所持：" + shop.getInventory().getIngredient(recipe.getIngredientId()) + "個)");
            }
            System.out.println();
            System.out.println(shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)) + "個所持しています");
            System.out.println(canMakeBread + "個作成可能です");
            System.out.println("いくつ作成しますか？\n");
            int command = InputOutputManager.inputNumber(0, 99 - shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)));
            if(command == 0){
                System.out.println(name + "を作成しませんでした\n");
                InputOutputManager.wait(400);
                continue;
            }
            if(canMakeBread < command){
                InputOutputManager.coloerPrintl("材料がたりません！\n", Color.YELLOW);
                System.out.println("足りない食材を購入しますか？");
                System.out.println("1．購入する");
                System.out.println("2．購入しない");
                int choice = InputOutputManager.inputNumber(1, 2);
                if(choice == 1){
                    int cost = 0;
                    for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
                        cost += (recipe.getQuantity() * command - shop.getInventory().getIngredient(recipe.getIngredientId())) * shop.getIngredients().get(recipe.getIngredientId()).getPrice();
                    }
                    if(shop.useMoney(cost)){
                        for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
                            shop.getInventory().addIngredient(recipe.getIngredientId(), recipe.getQuantity() * command - shop.getInventory().getIngredient(recipe.getIngredientId()));
                        }
                        shop.getSalesHistory().addTodayCost(cost);
                    }else{
                        InputOutputManager.wait(400);
                        continue;
                    }
                }else if(choice == 2){
                    InputOutputManager.wait(400);
                    continue;
                }
            }
            System.out.println(name + "を" + command + "個作成します");
            System.out.println(name + "を" + command + "個作成しています...\n");
            shop.getSalesHistory().addMakeBread(shop.getHasBreadRecipe().get(menu), command);
            InputOutputManager.wait(1000);
            for(RecipeIngredient recipe : shop.getBreads().get(shop.getHasBreadRecipe().get(menu)).getRecipe().getIngredients()){
                int quantity = recipe.getQuantity();
                shop.getInventory().useIngredient(recipe.getIngredientId(), quantity * command);
            }
            shop.getInventory().addBread(shop.getHasBreadRecipe().get(menu), command);
            System.out.println(name + "を" + command + "個作成しました");
            System.out.println(name + "は" + shop.getInventory().getBread(shop.getHasBreadRecipe().get(menu)) +"個になりました\n");
            InputOutputManager.wait(800);
        }
    }
    public void buyIngredient(){
        while(true){
            shop.ShowMoney();
            System.out.println("===== 販売食材 =====");
            for (int i = 0; i < shop.getHasIngredientType().size(); i++) {
                String name = shop.getIngredients().get(shop.getHasIngredientType().get(i)).getName();
                System.out.printf("%2d",i + 1);
                System.out.println("：" + name + " ".repeat(InputOutputManager.repeatNum(IngredientMaxLength,name.length())) + shop.getIngredients().get(shop.getHasIngredientType().get(i)).getPrice() + "G");
            }
            System.out.printf("%2d：もどる\n\n",shop.getHasIngredientType().size() + 1);
            int menu = InputOutputManager.inputNumber(1, shop.getHasIngredientType().size() + 1);
            menu --;
            if(menu == shop.getHasIngredientType().size()) return;
            String name  = shop.getIngredients().get(shop.getHasIngredientType().get(menu)).getName();
            int price = shop.getIngredients().get(shop.getHasIngredientType().get(menu)).getPrice();
            if(shop.getMoney() < price){
                InputOutputManager.coloerPrintl(name + "を買うにはお金が足りません！\n", Color.YELLOW);
                InputOutputManager.wait(400);
                continue;
            }
            System.out.println(name + "を" + shop.getInventory().getIngredient(shop.getHasIngredientType().get(menu)) + "個所持しています");
            System.out.println("いくつ購入しますか？");
            int command = InputOutputManager.inputNumber(0,Math.min(shop.getMoney()/price,999 - shop.getInventory().getIngredient(shop.getHasIngredientType().get(menu))));
            if(command == 0){
                System.out.println(name + "を購入しませんでした\n");
                InputOutputManager.wait(400);
                continue;
            }
            if(shop.getInventory().getIngredient(shop.getHasIngredientType().get(menu)) == 999){
                InputOutputManager.coloerPrintl("これ以上" + name + "を持てません", Color.YELLOW);
            }
            if(shop.useMoney(price * command)){
                shop.getInventory().addIngredient(shop.getHasIngredientType().get(menu), command);
                shop.getSalesHistory().addTodayCost(price * command);
                System.out.println(name + "を" + command + "個購入しました");
                System.out.println(name + "は" + shop.getInventory().getIngredient(shop.getHasIngredientType().get(menu)) +"個になりました\n");
            }
            InputOutputManager.wait(800);
        }
    }
    public void checkInventory(){
        shop.getInventory().showIngredientStock(shop.getIngredients(),IngredientMaxLength);
        shop.getInventory().showBreadStock(shop.getBreads(),BreadMaxLength,shop.getHasBreadRecipe());
    }
    public void checkShop(){
        shop.ShowShop();
        InputOutputManager.wait(800);
    }
    public void promotion(){
        int maxString = 0;
        for(Promotion p:shop.getPromotions()){
            maxString = Math.max(p.getChoiceMessage().length(),maxString);
        }
        while (true) {
            shop.ShowMoney();
            System.out.println("===== 宣伝 =====");
            for(int i=0;i<shop.getPromotions().size();i++){
                System.out.printf((i + 1) + "．" + shop.getPromotions().get(i).getChoiceMessage() + " ".repeat(InputOutputManager.repeatNum(maxString,shop.getPromotions().get(i).getChoiceMessage().length())));
                System.out.printf("%,5dG　人気度＋%2d\n",shop.getPromotions().get(i).getCost(),shop.getPromotions().get(i).getPop());
            }
            System.out.println((shop.getPromotions().size() + 1) + "．もどる");
            System.out.println();
            int command = InputOutputManager.inputNumber(1, shop.getPromotions().size() + 1);
            if(command == shop.getPromotions().size() + 1){
                return;
            }else{
                command --;
                if(!shop.getPromotions().get(command).getCheck()){
                    if(shop.useMoney(shop.getPromotions().get(command).getCost())){
                        System.out.println(shop.getPromotions().get(command).getViewMessage());
                        System.out.println("人気度が" + shop.getPromotions().get(command).getPop() + "あがりました");
                        System.out.println();
                        shop.addPopularity(shop.getPromotions().get(command).getPop());
                        shop.getSalesHistory().addTodayPromotionCost(shop.getPromotions().get(command).getCost());
                        shop.getPromotions().get(command).setCheck(true);
                        InputOutputManager.wait(400);
                    }
                }else{
                    InputOutputManager.coloerPrintl("1日に1度までしか使えません\n", Color.YELLOW);
                }
            }
        }
    }
    public void resetPromotion(){
        for(Promotion p:shop.getPromotions()){
            p.setCheck(false);
        }
    }
    public void partJob(){
        System.out.println("お店を休んでアルバイトをしました！");
        System.out.println("500G獲得しました");
        shop.addMoney(500);
        System.out.println();
        shop.addPopularity(-1);
        System.out.println("お店を休んでしまったので、人気度が1下がってしまいました");
    }

}
