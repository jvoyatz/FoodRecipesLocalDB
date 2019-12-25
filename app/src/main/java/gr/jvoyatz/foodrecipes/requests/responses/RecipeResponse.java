package gr.jvoyatz.foodrecipes.requests.responses;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import gr.jvoyatz.foodrecipes.models.Recipe;

public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;
    private String error;

    @Nullable
    public Recipe getRecipe(){
        return recipe;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
