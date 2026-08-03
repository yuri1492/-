import java.util.LinkedHashMap;
import java.util.Map;

public class SalesHistory {
    private int todaySales;
    private int todayCost;
    private int todayCustomers;
    private int todaySoldBread;
    private int todayPromotionCost;
    private int totalSales;
    private int totalCost;
    private int totalCustomers;
    private int totalSoldBread;
    private int totalPromotionCost;
    private int totalProfit;
    private int highSalses;
    private int highSalsesDay;
    private int highPopularity;
    private int soldOut;
    private Map<BreadType, Integer> makeBread = new LinkedHashMap<>();
    private Map<BreadType, Integer> soldBread = new LinkedHashMap<>();

    public SalesHistory(int popularity) {
        for (BreadType type : BreadType.values()) {
            makeBread.put(type, 0);
            soldBread.put(type, 0);
        }
        highPopularity = popularity;
    }

    public int getTodaySales() {
        return todaySales;
    }
    public int getTodayCost() {
        return todayCost;
    }
    public int getTodayCustomers() {
        return todayCustomers;
    }
    public int getTodaySoldBread() {
        return todaySoldBread;
    }
    public int getTodayPromotionCost(){
        return todayPromotionCost;
    }
    public int getTotalSales() {
        return totalSales;
    }
    public int getTotalCost(){
        return totalCost;
    }
    public int getTotalCustomers() {
        return totalCustomers;
    }
    public int getTotalSoldBread() {
        return totalSoldBread;
    }
    public int getTotalPromotionCost(){
        return totalPromotionCost;
    }
    public int getTotalProfit(){
        return totalProfit;
    }
    public int getHighSalses(){
        return highSalses;
    }
    public int getHighSalsesDay(){
        return highSalsesDay;
    }
    public int getHighPopularity(){
        return highPopularity;
    }
    public int getSoldOut(){
        return soldOut;
    }
    
    public int getMakeBread(BreadType type){
        return makeBread.get(type);
    }
    public int getSoldBread(BreadType type){
        return soldBread.get(type);
    }

    public void addTodaySales(int sales){
        todaySales += sales;
        totalSales += sales;
    }
    public void addTodayCost(int cost){
        todayCost += cost;
        totalCost += cost;
    }
    public void setTodayCustomers(int customers) {
        this.todayCustomers = customers;
        totalCustomers += customers;
    }
    public void addTodaySoldBread() {
        todaySoldBread ++;
        totalSoldBread ++;
    }
    public void addSoldOut(){
        soldOut ++;
    }
    public void addTodayPromotionCost(int cost){
        todayPromotionCost += cost;
        totalPromotionCost += cost;
    }
    public void addTotalProfit(int profit){
        totalProfit += profit;
    }

    public void updateHighSalses(int salses,int day){
        if(highSalses < salses){
            highSalses = salses;
            highSalsesDay = day;
        }
    }
    public void updateHighPopularity(int pop){
        highPopularity = Math.max(highPopularity, pop);
    }

    public void addMakeBread(BreadType type,int add){
        makeBread.put(type, makeBread.get(type) + add);
    }
    public void addSoldBread(BreadType type,int add){
        soldBread.put(type, soldBread.get(type) + add);
    }

    public String showHighMakeBreadName(Map<BreadType, Bread> breads){
        String name = "--";
        int max = 0;
        for (Map.Entry<BreadType, Integer> entry : makeBread.entrySet()) {
            if(max < entry.getValue()){
                name = breads.get(entry.getKey()).getName();
                max = entry.getValue();
            }
        }
        return name;
    }
    public int showHighMakeBreadNum(Map<BreadType, Bread> breads){
        int breadNum =0;
        int max = 0;
        for (Map.Entry<BreadType, Integer> entry : makeBread.entrySet()) {
            if(max < entry.getValue()){
                breadNum = entry.getValue();
                max = entry.getValue();
            }
        }
        return breadNum;
    }
    public String showHighSoldBreadName(Map<BreadType, Bread> breads){
        String name = "--";
        int max = 0;
        for (Map.Entry<BreadType, Integer> entry : soldBread.entrySet()) {
            if(max < entry.getValue()){
                name = breads.get(entry.getKey()).getName();
                max = entry.getValue();
            }
        }
        return name;
    }
    public int showHighSoldBreadNum(Map<BreadType, Bread> breads){
        int breadNum =0;
        int max = 0;
        for (Map.Entry<BreadType, Integer> entry : soldBread.entrySet()) {
            if(max < entry.getValue()){
                breadNum = entry.getValue();
                max = entry.getValue();
            }
        }
        return breadNum;
    }

    public void resetTodaydata(){
        todayCost = 0;
        todayCustomers = 0;
        todaySales = 0;
        todaySoldBread = 0;
        todayPromotionCost = 0;
    }
}
