package gr.jvoyatz.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.util.List;

import gr.jvoyatz.foodrecipes.adapters.OnRecipeListener;
import gr.jvoyatz.foodrecipes.adapters.RecipeRecyclerAdapter;
import gr.jvoyatz.foodrecipes.models.Recipe;
import gr.jvoyatz.foodrecipes.util.Resource;
import gr.jvoyatz.foodrecipes.util.VerticalSpacingItemDecorator;
import gr.jvoyatz.foodrecipes.viewmodels.RecipeListViewModel;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if(listResource.data != null){
                        //mAdapter.setRecipes(listResource.data);
                        switch (listResource.status){
                            case LOADING:
                                if(mRecipeListViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                }else{
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            case ERROR:
                                Log.d(TAG, "onChanged: cannot refresh the cache");
                                Log.d(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.d(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(RecipeListViewModel.QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            case SUCCESS:
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESSS, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;

                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewstate().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                if(viewState != null){
                    switch (viewState){

                        case RECIPES:{
                            // recipes will show automatically from other observer
                            break;
                        }

                        case CATEGORIES:{
                            displaySearchCategories();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void searchRecipeApi(String query){
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void initRecyclerView(){
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();
        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloader);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> recyclerViewPreloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloader,
                30);

        mRecyclerView.addOnScrollListener(recyclerViewPreloader);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!mRecyclerView.canScrollVertically(1) &&
                        mRecipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.RECIPES){
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchRecipeApi(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mRecipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
        }else{
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }

    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipeApi(category);
    }

    private void displaySearchCategories(){
        mAdapter.displaySearchCategories();
    }

    private RequestManager initGlide(){
        RequestOptions options =
                new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

       return Glide.with(this)
                .setDefaultRequestOptions(options);

    }
}
















