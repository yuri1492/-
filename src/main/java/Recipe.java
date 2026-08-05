import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private List<RecipeIngredient> recipe = new ArrayList<>();

    public List<RecipeIngredient> getIngredients() {
        return recipe;
    }

    public void setRecipe(List<RecipeIngredient> recipe) {
        this.recipe = recipe;
    }
}