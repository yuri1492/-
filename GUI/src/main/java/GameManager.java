import java.util.Map;
import java.util.stream.Collectors;

public class GameManager {

    private Map<IngredientType, Ingredient> ingredients;
    private Map<BreadType, Bread> breads;
    private int BreadMaxLength;
    private int IngredientMaxLength;

    private DayManager dayManager;
    private Shop shop;
    private final int day;
    private final int closeBread = 0;

    public GameManager() {
        DataLoader loader = new DataLoader();

        ingredients =
            loader.loadIngredients()
                  .stream()
                  .collect(Collectors.toMap(
                      Ingredient::getId,
                      i -> i
                  ));

        breads =
            loader.loadBreads()
                  .stream()
                  .collect(Collectors.toMap(
                      Bread::getId,
                      b -> b
                  ));
        for (Ingredient ingredient : ingredients.values()) {
            IngredientMaxLength = Math.max(IngredientMaxLength, ingredient.getName().length());
        }
        for (Bread bread : breads.values()) {
            BreadMaxLength = Math.max(BreadMaxLength , bread.getName().length());
        }
        
        System.out.println("\nパン屋物語 ～小さな町のベーカリー～");
        System.out.println("**Enterキーでゲーム開始**");
        InputOutputManager.scanner.nextLine();
        System.out.println("お店の名前を入力してください\n(入力しない場合、「まちのパン屋さん」になります)");
        String name = InputOutputManager.scanner.nextLine();
        System.out.println("1．通常モード");
        System.out.println("2．エンドレスモード");
        int command = InputOutputManager.inputNumber(1,2);
        if(command == 1){
            day = 30;
        }else{
            day = 999;
        }
        shop = new Shop(name,ingredients,breads);
        dayManager = new DayManager(shop,IngredientMaxLength,BreadMaxLength,closeBread);
        System.out.println();
        while (dayManager.getDay() < day) {
            int totalBread = dayManager.startDay();
            int soldBread = 0;
            if(totalBread != closeBread){
                soldBread = dayManager.startSales();
            }
            dayManager.endDay(totalBread,soldBread);
            if(day == 999){
                System.out.println("Enterキーで次の日");
                System.out.println("その他のキーで終了");
                String inputKey = InputOutputManager.scanner.nextLine();
                if(!inputKey.isEmpty()){
                    System.out.println();
                    break;
                }
            }
        }
        ending();
    }

    public void ending(){
        String[] endingMassages = {
            dayManager.getDay()  + "日の営業が終了しました",
            "",
            "=============================",
            "         エンディング",
            "=============================",
            "店名：" + shop.getName(),
            "",
            "経営日数：" + dayManager.getDay() + "日",
            "店舗レベル：" + shop.getLevel(),
            "人気度：" + shop.getPopularity(),
            "最終所持金：" + String.format("%,d", shop.getMoney()) + "G",
            "",
            "==== 経営成績 ====",
            "総売上　　　：" + String.format("%,d",shop.getSalesHistory().getTotalSales()) + "G",
            "総仕入れ額　：" + String.format("%,d",shop.getSalesHistory().getTotalCost()) + "G",
            "総広告費　　：" + String.format("%,d",shop.getSalesHistory().getTotalPromotionCost()) + "G",
            "純利益　　　：" + String.format("%,d",shop.getSalesHistory().getTotalProfit()) + "G",
            "最高売上　　：" + String.format("%,d",shop.getSalesHistory().getHighSalses()) + "G" + "(" + shop.getSalesHistory().getHighSalsesDay() + "日目)",
            "最高人気度　：" + shop.getSalesHistory().getHighPopularity(),
            "全商品完売　：" + shop.getSalesHistory().getSoldOut() + "回",
            "",
            "販売したパンの総数：" + shop.getSalesHistory().getTotalSoldBread() + "個",
            "来客人数　　　　　：" + shop.getSalesHistory().getTotalCustomers() + "人",
            "一番売ったパン　　：" + shop.getSalesHistory().showHighSoldBreadName(breads) + "(" + shop.getSalesHistory().showHighSoldBreadNum(breads) + "個)",
            "一番作ったパン　　：" + shop.getSalesHistory().showHighMakeBreadName(breads) + "(" + shop.getSalesHistory().showHighMakeBreadNum(breads) + "個)",
            "",
            "",
            "=============================",
        };
        for(int i=0;i<endingMassages.length;i++){
            System.out.println(endingMassages[i]);
            InputOutputManager.wait(300);
        }
        InputOutputManager.wait(200);
        System.out.println();
        if(shop.getLevel() == 5&&
           shop.getPopularity() >= 90&&
           shop.getSalesHistory().getTotalProfit() >= 400000){
            System.out.println("～ランクS～");
            InputOutputManager.coloerPrintl("伝説のパン職人！", Color.PURPLE);
            System.out.println("あなたのパン屋には毎日長蛇の列！");
            System.out.println("経営も技術も町一番、誰もが憧れる伝説のパン屋になりました。");
        }else if(shop.getLevel() == 5&&
           shop.getPopularity() >= 75&&
           shop.getSalesHistory().getTotalProfit() >= 300000){
            System.out.println("～ランクA～");
            InputOutputManager.coloerPrintl("町一番の人気ベーカリー！", Color.PURPLE);
            System.out.println("多くのお客さんに愛される人気店へと成長しました。");
            System.out.println("町の人々の暮らしに欠かせない存在です。");
        }else if(shop.getLevel() >= 4&&
           shop.getPopularity() >= 60&&
           shop.getSalesHistory().getTotalProfit() >= 200000){
            System.out.println("～ランクB～");
            InputOutputManager.coloerPrintl("評判のパン屋さん！", Color.PURPLE);
            System.out.println("お店は順調に成長し、常連客も増えました。");
            System.out.println("あと一歩で町を代表するパン屋です。");
        }else if(shop.getLevel() >= 3&&
           shop.getPopularity() >= 40&&
           shop.getSalesHistory().getTotalProfit() >= 120000){
            System.out.println("～ランクC～");
            InputOutputManager.coloerPrintl("地域密着のパン屋", Color.PURPLE);
            System.out.println("少しずつお客さんが増え、地域に根付いたお店になりました。");
            System.out.println("これからの成長に期待です。");
        }else if(shop.getLevel() >= 2&&
           shop.getSalesHistory().getTotalProfit() >= 50000){
            System.out.println("～ランクD～");
            InputOutputManager.coloerPrintl("修行中のパン職人", Color.PURPLE);
            System.out.println("パン作りも経営もまだ発展途上です。");
            System.out.println("経験を積めば、もっと素敵なお店になるでしょう。");
        }else{
            System.out.println("～ランクE～");
            InputOutputManager.coloerPrintl("見習いパン屋", Color.PURPLE);
            System.out.println("パン屋経営は決して簡単ではありません。");
            System.out.println("今回の経験を活かして、次こそ人気店を目指しましょう！");
        }
    }

}