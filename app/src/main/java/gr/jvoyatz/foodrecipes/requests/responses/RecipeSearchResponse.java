package gr.jvoyatz.foodrecipes.requests.responses;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import gr.jvoyatz.foodrecipes.models.Recipe;

public class RecipeSearchResponse {

    @SerializedName("count")
    @Expose()
    private int count;

    @SerializedName("recipes")
    @Expose()
    private List<Recipe> recipes;

    public int getCount() {
        return count;
    }

    @SerializedName("error")
    @Expose()
    private String error;


    @Nullable
    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", recipes=" + recipes +
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
