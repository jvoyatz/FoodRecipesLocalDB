package gr.jvoyatz.foodrecipes.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import gr.jvoyatz.foodrecipes.models.Recipe;

@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertRecipes(Recipe... recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);


    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :image_url, social_rank = :social_rank " +
            " WHERE recipe_id = :recipeId ")
    void updateRecipe(String recipeId, String title, String publisher, String image_url, float social_rank);


    @Query("SELECT * FROM recipes where title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%'" +
        "ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);


    @Query("SELECT * FROM recipes WHERE recipe_id = :recipe_id")
    LiveData<Recipe> getRecipe(String recipe_id);
}
