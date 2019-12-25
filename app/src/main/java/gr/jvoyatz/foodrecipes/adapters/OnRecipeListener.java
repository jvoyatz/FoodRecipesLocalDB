package gr.jvoyatz.foodrecipes.adapters;

public interface OnRecipeListener {

    void onRecipeClick(int position);

    void onCategoryClick(String category);
}
