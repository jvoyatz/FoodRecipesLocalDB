package gr.jvoyatz.foodrecipes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import gr.jvoyatz.foodrecipes.R;
import gr.jvoyatz.foodrecipes.models.Recipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView title, publisher, socialScore;
    AppCompatImageView image;
    OnRecipeListener onRecipeListener;
    RequestManager requestManager;
    ViewPreloadSizeProvider preloadSizeProvider;

    public RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener, RequestManager requestManager, ViewPreloadSizeProvider viewPreloadSizeProvider) {
        super(itemView);

        this.onRecipeListener = onRecipeListener;
        this.requestManager = requestManager;
        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        socialScore = itemView.findViewById(R.id.recipe_social_score);
        image = itemView.findViewById(R.id.recipe_image);

        itemView.setOnClickListener(this);
        this.preloadSizeProvider = viewPreloadSizeProvider;
    }

    public void onBind(Recipe recipe){
        requestManager.load(recipe.getImage_url())
                .into(image);

       title.setText(recipe.getTitle());
       publisher.setText(recipe.getPublisher());
       socialScore.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

        preloadSizeProvider.setView(image);
    }
    @Override
    public void onClick(View v) {
        onRecipeListener.onRecipeClick(getAdapterPosition());
    }
}




