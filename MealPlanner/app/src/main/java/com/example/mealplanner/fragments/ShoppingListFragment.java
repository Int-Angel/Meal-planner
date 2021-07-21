package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.asynchttpclient.AbsCallback;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;


public class ShoppingListFragment extends Fragment {

    private final static String TAG = "ShoppingListFragment";
    private final static String COMPUTE_SHOPPING_LIST_URL = "https://api.spoonacular.com/mealplanner/shopping-list/compute?apiKey=728721c3da7543769d5413b35ac70cd7";

    private AsyncHttpClient client;
    private TextView test;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        test = view.findViewById(R.id.test);
        TEST();
    }

    private void TEST(){
        client = new AsyncHttpClient();
        String body = "{items: [4 lbs tomatoes,10 tomatoes,20 Tablespoons Olive Oil,6 tbsp Olive Oil]}";
        client.post(COMPUTE_SHOPPING_LIST_URL, body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.i(TAG,"YEII");
                try {
                    JSONArray jsonArray = json.jsonObject.getJSONArray("aisles");
                    Log.i(TAG,json.jsonObject.toString());
                } catch (JSONException e) {
                    Log.e(TAG,"NO jasonArray",e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e(TAG,"NOOO",throwable);
            }
        });

    }
}