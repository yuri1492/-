public class LevelUpCondition {
    private int sales;
    private BreadType[] releaseBread;
    private IngredientType[] releaseIngredient;
    private Promotion promotion;

    LevelUpCondition(int sales,BreadType[] releaseBread,IngredientType[] releseIngredient,Promotion promotion){
        this.sales = sales;
        this.releaseBread = releaseBread;
        this.releaseIngredient = releseIngredient;
        this.promotion = promotion;
    }

    public int getSales(){
        return sales;
    }
    public BreadType[] getReleaseBread(){
        return releaseBread;
    }
    public IngredientType[] getReleaseIngredient(){
        return releaseIngredient;
    }
    public Promotion getPromotion(){
        return promotion;
    }
}
