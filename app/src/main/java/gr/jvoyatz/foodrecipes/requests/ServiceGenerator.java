package gr.jvoyatz.foodrecipes.requests;

import java.util.concurrent.TimeUnit;

import gr.jvoyatz.foodrecipes.util.Constants;

import gr.jvoyatz.foodrecipes.util.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {


    private static OkHttpClient client = new OkHttpClient.Builder()
            //time takes the application to establish a connection to the server
            .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            //time between each byte read from the server
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)

            //time between each byte sent to the server
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .client(client)
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
