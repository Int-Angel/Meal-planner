package com.example.mealplanner.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AbsCallback;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.R;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.adapters.ShoppingListAdapter;
import com.example.mealplanner.adapters.ShoppingListAisleAdapter;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Shows the shopping list items from a shopping list
 */
public class ShoppingListFragment extends Fragment implements
        CreateNewShoppingListItemFragment.CreateNewItemListener {

    public interface ShoppingListFragmentListener {
        void closeShoppingList();
    }

    private final static String TAG = "ShoppingListFragment";
    private final static String SHOPPING_LIST = "shopping_list";

    private CreateNewShoppingListItemFragment createShoppingListItem;

    private ShoppingListFragmentListener listener;

    private ShoppingList shoppingList;
    private List<ShoppingListItem> shoppingListItems;
    private HashMap<String, List<ShoppingListItem>> aisles;
    private List<String> aislesName;
    private ShoppingListAisleAdapter adapter;

    private RecyclerView rvShoppingList;
    private TextView tvDateRange;
    private TextView tvShoppinglistTitle;
    private ProgressBar progress_circular;
    private ImageButton ibtnBack;
    private FloatingActionButton fab;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * This fragment requires the shopping list to show
     * @param shoppingList
     * @return
     */
    public static ShoppingListFragment newInstance(ShoppingList shoppingList) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putParcelable(SHOPPING_LIST, Parcels.wrap(shoppingList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shoppingList = Parcels.unwrap(getArguments().getParcelable(SHOPPING_LIST));
        }
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

        listener = (ShoppingListFragmentListener) getParentFragment();

        aisles = new HashMap<>();
        aislesName = new ArrayList<>();

        shoppingListItems = new ArrayList<>();
        adapter = new ShoppingListAisleAdapter(getContext(), aisles, aislesName, new ShoppingListAisleAdapter.ShoppingListAisleAdapterListener() {
            @Override
            public void editItem(int position, String oldAisle, ShoppingListItem item) {
                openEditShoppingListItemFragment(position, oldAisle, item);
            }
        });

        tvDateRange = view.findViewById(R.id.tvDateRange);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);
        tvShoppinglistTitle = view.findViewById(R.id.tvShoppinglistTitle);
        progress_circular = view.findViewById(R.id.progress_circular);
        ibtnBack = view.findViewById(R.id.ibtnBack);
        fab = view.findViewById(R.id.fab);

        tvShoppinglistTitle.setText(shoppingList.getName());

        rvShoppingList.setAdapter(adapter);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(getContext()));

        String dateRange = getStringDate(shoppingList.getStartDate()) + " - " + getStringDate(shoppingList.getEndDate());
        tvDateRange.setText(dateRange);

        setUpOnClickListeners();

        queryShoppingListItems();
    }

    /**
     * Sets up all the onClickListeners
     */
    private void setUpOnClickListeners() {
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeShoppingList();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateShoppingListItemFragment();
            }
        });
    }

    /**
     * Opens the fragment to create a new shopping list item for this shopping list
     */
    void openCreateShoppingListItemFragment() {
        createShoppingListItem = CreateNewShoppingListItemFragment.newInstance(shoppingList);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, createShoppingListItem)
                .commit();
    }

    /**
     * open the fragment to edit a shopping list item
     * @param position item position in the array
     * @param oldAisle aisle before being modified
     * @param item item to be modified
     */
    void openEditShoppingListItemFragment(int position, String oldAisle, ShoppingListItem item) {
        createShoppingListItem = CreateNewShoppingListItemFragment.newInstance(shoppingList, item, oldAisle, position);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, createShoppingListItem)
                .commit();
    }

    /**
     * Returns a date in a string format
     * @param date
     * @return
     */
    private String getStringDate(Date date) {
        return DateFormat.format("MMM.dd", date).toString();
    }

    /**
     * Gets all the shopping list items form the database
     */
    private void queryShoppingListItems() {
        ParseQuery<ShoppingListItem> query = ParseQuery.getQuery(ShoppingListItem.class);
        query.whereEqualTo(ShoppingListItem.KEY_SHOPPING_LIST, shoppingList);

        query.findInBackground(new FindCallback<ShoppingListItem>() {
            @Override
            public void done(List<ShoppingListItem> objects, ParseException e) {
                progress_circular.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG, "Error while getting shopping list items", e);
                    return;
                }
                shoppingListItems.clear();
                shoppingListItems.addAll(objects);

                aisles.clear();
                generateAisles();
            }
        });
    }

    /**
     * Generates a list of aisles to split the shopping list items into aisles
     */
    private void generateAisles() {

        for (ShoppingListItem item : shoppingListItems) {
            String aisleNameStr = getShoppingListAisle(item);
            if (aisles.containsKey(aisleNameStr)) {
                aisles.get(aisleNameStr).add(item);
            } else {
                aislesName.add(aisleNameStr);
                List<ShoppingListItem> shoppingListItems = new ArrayList<>();
                shoppingListItems.add(item);
                aisles.put(aisleNameStr, shoppingListItems);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Add each item to an aisle
     * @param item
     */
    private void addItemToAisles(ShoppingListItem item) {
        String aisleNameStr = getShoppingListAisle(item);
        if (aisles.containsKey(aisleNameStr)) {
            aisles.get(aisleNameStr).add(item);
        } else {
            aislesName.add(aisleNameStr);
            List<ShoppingListItem> shoppingListItems = new ArrayList<>();
            shoppingListItems.add(item);
            aisles.put(aisleNameStr, shoppingListItems);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * returns the item aisle
     * @param item
     * @return
     */
    private String getShoppingListAisle(ShoppingListItem item) {
        String auxAisle = item.getAisle();
        return auxAisle.split(";")[0]; // some items have multiple aisle, I only use the first aisle
    }

    @Override
    public void closeCreateNewItemFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .remove(createShoppingListItem)
                .commit();
    }

    @Override
    public void shoppingItemCreated(ShoppingListItem item) {
        addItemToAisles(item);
        closeCreateNewItemFragment();
    }

    @Override
    public void shoppingItemEdited(ShoppingListItem item, int position, String oldAisle) {
        addItemToAisles(item);
        aisles.get(oldAisle).remove(position);
        if (aisles.get(oldAisle).size() == 0) {
            aislesName.remove(position);
            aisles.remove(oldAisle);
        }
        adapter.notifyDataSetChanged();
        closeCreateNewItemFragment();
    }

}