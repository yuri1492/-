public class LevelUpCondition {
    private int sales;
    private BreadType[] releaseBread;
    private IngredientType[] releaIngredient;
    private Promotion promotion;

    LevelUpCondition(int sales,BreadType[] releaseBread,IngredientType[] releIngredient,Promotion promotion){
        this.sales = sales;
        this.releaseBread = releaseBread;
        this.releaIngredient = releIngredient;
        this.promotion = promotion;
    }

    public int getSales(){
        return sales;
    }
    public BreadType[] getReleaseBread(){
        return releaseBread;
    }
    public IngredientType[] getReleaIngredient(){
        return releaIngredient;
    }
    public Promotion getPromotion(){
        return promotion;
    }
}
