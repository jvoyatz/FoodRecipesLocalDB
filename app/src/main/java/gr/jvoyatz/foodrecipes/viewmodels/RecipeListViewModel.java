package gr.jvoyatz.foodrecipes.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import gr.jvoyatz.foodrecipes.models.Recipe;
import gr.jvoyatz.foodrecipes.repositories.RecipeRepository;
import gr.jvoyatz.foodrecipes.util.Resource;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public static final String QUERY_EXHAUSTED = "QUERY_EXHAUSTED";
    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private RecipeRepository recipeRepository;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init(){
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewstate(){
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public void searchRecipesApi(String query, int pageNumber){
       if(!isPerformingQuery){
           if(pageNumber == 0){
               pageNumber = 1;
           }
           this.pageNumber = pageNumber;
           this.query = query;
           isQueryExhausted = false;
           executeSearch();
       }
    }

    public void searchNextPage(){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeSearch();
        }
    }
    public int getPageNumber() {
        return pageNumber;
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    private void executeSearch(){
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        recipes.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime)/1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is exhausted...");
                                    recipes.setValue(new Resource<List<Recipe>>(Resource.Status.ERROR, listResource.data, QUERY_EXHAUSTED));
                                    isQueryExhausted = true;
                                }
                            }
                            recipes.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime)/1000 + " seconds.");
                            isPerformingQuery = false;
                            if(listResource.message.equals(QUERY_EXHAUSTED)){
                                isQueryExhausted = true;
                            }
                            recipes.removeSource(repositorySource);
                        }
                    } else {
                        //remove observer when you re no longer need the observer
                        recipes.removeSource(repositorySource);
                    }
                }else{
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }

    public void cancelSearchRequest(){
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: cancelling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }
}
















