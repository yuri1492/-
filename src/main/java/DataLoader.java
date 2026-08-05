import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class DataLoader {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Bread> loadBreads() {
        try {
            return mapper.readValue(
                getClass().getResourceAsStream("/Bread.json"),
                new TypeReference<List<Bread>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Ingredient> loadIngredients() {
        try {
            return mapper.readValue(
                getClass().getResourceAsStream("/Ingredient.json"),
                IngredientData.class
            ).getIngredients();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}