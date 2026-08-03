import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class Inventory {
    private Map<IngredientType, Integer> ingredientStock = new LinkedHashMap<>();
    private Map<BreadType, Integer> breadStock = new LinkedHashMap<>();

    public void addBreadType(BreadType type){
        breadStock.put(type, 0);
    }
    public void addIngredientType(IngredientType type){
        ingredientStock.put(type,0);
    }
    public int getIngredient(IngredientType type){
        return ingredientStock.get(type);
    }

    public int getBread(BreadType type){
        return breadStock.get(type);
    }
    public Map<BreadType, Integer> getBreadStock(){
        return breadStock;
    }

    public void addIngredient(IngredientType type,int num){
        ingredientStock.put(type,ingredientStock.get(type) + num);
    }

    public void useIngredient(IngredientType type,int num){
        if(ingredientStock.get(type) - num < 0){
            throw new RuntimeException("材料の在庫が足りません");
        }else{
            ingredientStock.put(type,ingredientStock.get(type) - num);
        }
    }

    public void addBread(BreadType type,int num){
        breadStock.put(type,breadStock.get(type) + num);
    }

    public void useBread(BreadType type,int num){
        if(breadStock.get(type) - num < 0){
            throw new RuntimeException("パンの在庫が足りません");
        }else{
            breadStock.put(type,breadStock.get(type) - num);
        }
    }

    public void showIngredientStock(Map<IngredientType, Ingredient> ingredients,int maxLength) {
        System.out.println("===== 所持材料 =====");

        for (Map.Entry<IngredientType, Integer> entry : ingredientStock.entrySet()) {
            String name = ingredients.get(entry.getKey()).getName();
            System.out.println(name + " ".repeat(InputOutputManager.repeatNum(maxLength,name.length())) + "：" + String.format("%3d",entry.getValue()) + "個");
        }
        System.out.println();
        InputOutputManager.wait(800);
    }

    public void showBreadStock(Map<BreadType, Bread> breads,int maxLength,List<BreadType> breadList) {
        System.out.println("====== パン在庫 ======");

        for (Map.Entry<BreadType, Integer> entry : breadStock.entrySet()) {
            String name = breads.get(entry.getKey()).getName();
            System.out.println(name + " ".repeat(InputOutputManager.repeatNum(maxLength,name.length())) + "：" + String.format("%2d",entry.getValue()) + "個");
        }
        System.out.println("合計：" + getTotalBread() + "個");
        System.out.println();
        InputOutputManager.wait(800);
    }

    public int getTotalBread() {
        int total = 0;
        for (int count : breadStock.values()) {
            total += count;
        }
        return total;
    }

}
