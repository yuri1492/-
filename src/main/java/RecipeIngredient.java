public class RecipeIngredient {
    private IngredientType ingredientId;
    private int quantity;

    public IngredientType getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(IngredientType ingredientId) {
        this.ingredientId = ingredientId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}