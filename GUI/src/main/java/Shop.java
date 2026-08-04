import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shop {
    private String name;
    private int money;
    private int level;
    private int popularity; //人気度
    private Inventory inventory;
    private SalesHistory salesHistory;
    private List<BreadType> hasBreadRecipe;
    private List<IngredientType> hasIngredientType;
    private Map<IngredientType, Ingredient> ingredients;
    private Map<BreadType, Bread> breads;
    private List<Promotion> promotions;
    private final LevelUpCondition[] levelUpCondition = {
        new LevelUpCondition(0, new BreadType[]{BreadType.Shokupan,BreadType.RollBread,BreadType.MelonPan,BreadType.Anpan},
            new IngredientType[]{IngredientType.Flour,IngredientType.Butter,IngredientType.Egg,IngredientType.Milk,IngredientType.Sugar,IngredientType.RedBeanPaste},
            new Promotion("チラシを配る","チラシを配りました",100,2)),
        new LevelUpCondition(20000, new BreadType[]{BreadType.ChocoCornet,BreadType.Croissant},new IngredientType[]{IngredientType.Chocolate},null),
        new LevelUpCondition(70000, new BreadType[]{BreadType.CurryBread,BreadType.CheeseBread},new IngredientType[]{IngredientType.Cheese},new Promotion("ＳＮＳに投稿する","SNSに投稿しました",300,5)),
        new LevelUpCondition(250000, new BreadType[]{BreadType.HamCheeseBread,BreadType.HotDog},new IngredientType[]{IngredientType.Sausage,IngredientType.Ham},null),
        new LevelUpCondition(500000,new BreadType[]{null},new IngredientType[]{null},new Promotion("新聞広告を出す","新聞広告を出しました",1000,15))
    };

    public Shop(String name,Map<IngredientType, Ingredient> ingredients,Map<BreadType, Bread> breads) {
        this.name = name;
        this.money = 2000;
        this.level = 0;
        this.popularity = 10;
        this.breads = breads;
        this.ingredients = ingredients;

        this.inventory = new Inventory();
        this.salesHistory = new SalesHistory(popularity);
        hasBreadRecipe = new ArrayList<>();
        hasIngredientType = new ArrayList<>();
        promotions = new ArrayList<>();
        checkLevel();
    }

    public String getName() {
        return name;
    }
    public int getMoney() {
        return money;
    }
    public int getLevel() {
        return level;
    }
    public int getPopularity() {
        return popularity;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public SalesHistory getSalesHistory() {
        return salesHistory;
    }
    public List<BreadType> getHasBreadRecipe(){
        return hasBreadRecipe;
    }
    public List<IngredientType> getHasIngredientType(){
        return hasIngredientType;
    }
    public Map<IngredientType, Ingredient> getIngredients(){
        return ingredients;
    }
    public Map<BreadType, Bread> getBreads(){
        return breads;
    }
    public List<Promotion> getPromotions(){
        return promotions;
    }
    public int getNextLevelSales(){
        return levelUpCondition[level].getSales();
    }

    public void addMoney(int num){
        this.money += num;
    }

    public boolean useMoney(int num){
        if(this.money - num < 0){
            return false;
        }else{
            this.money -= num;
            return true;
        }
    }
    
    public void addPopularity(int pop){
        popularity += pop;
        if(popularity < 0){
            popularity = 0;
        }
        if(popularity > 100){
            popularity = 100;
        }
        salesHistory.updateHighPopularity(popularity);
    }

    public void addPromotions(Promotion prom){
        promotions.add(prom);
    }
    public void addHasBreadRecipe(BreadType type){
        if (!hasBreadRecipe.contains(type)) {
            hasBreadRecipe.add(type);
        }
    }
    public void addHasIngredientType(IngredientType type){
        hasIngredientType.add(type);
    }
    public List<String> checkLevel(){
        List<String> logList = new ArrayList<>();
        for(int i=level;i<levelUpCondition.length;i++){
            if(levelUpCondition[i].getSales() <= salesHistory.getTotalSales()){
                level = i + 1;
                if(level != 1){
                    logList.add("店舗レベルが" + level + "になりました！");
                }
                if(levelUpCondition[i].getPromotion() != null){
                    addPromotions(levelUpCondition[i].getPromotion());
                    if(level != 1){
                        logList.add("宣伝 「" + levelUpCondition[i].getPromotion().getChoiceMessage() + "」が使用できるようになりました！");
                    }
                }
                for(BreadType type:levelUpCondition[i].getReleaseBread()){
                    if(type != null){
                        if(level != 1){
                            logList.add(breads.get(type).getName() + "が作れるようになりました！");
                        }
                        addHasBreadRecipe(type);
                        inventory.addBreadType(type);
                    }
                }
                for(IngredientType type : levelUpCondition[i].getReleaIngredient()){
                    if(type != null){
                        addHasIngredientType(type);
                        inventory.addIngredientType(type);
                    }
                }
            }else{
                break;
            }
        }
        return logList;
    }

}