package gr.jvoyatz.foodrecipes.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import gr.jvoyatz.foodrecipes.models.Recipe;
import gr.jvoyatz.foodrecipes.repositories.RecipeRepository;
import gr.jvoyatz.foodrecipes.util.Resource;


public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository recipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return recipeRepository.searchRecipesApi(recipeId);
    }



}





















