package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mealplanner.R;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.adapters.ShoppingListManagerAdapter;
import com.example.mealplanner.models.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows all the shopping lists that user has saved.
 */
public class ShoppingListManagerFragment extends Fragment implements
        CreateShoppingListFragment.CreateShoppingListFragmentListener,
        ShoppingListFragment.ShoppingListFragmentListener {

    private final static String TAG = "ShoppingList LISTS";

    private final CreateShoppingListFragment createShoppingListFragment = new CreateShoppingListFragment();
    private ShoppingListFragment shoppingListFragment;

    private List<ShoppingList> shoppingLists;
    private ShoppingListManagerAdapter adapter;
    private RecyclerView rvShoppingList;
    private ProgressBar progress_circular;
    private FloatingActionButton fab;

    public ShoppingListManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shoppingLists = new ArrayList<>();

        progress_circular = view.findViewById(R.id.progress_circular);
        fab = view.findViewById(R.id.fab);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);

        adapter = new ShoppingListManagerAdapter(getContext(), shoppingLists, new ShoppingListManagerAdapter.ShoppingListListAdapterListener() {
            @Override
            public void openShoppingList(ShoppingList shoppingList) {
                openShoppingListDetails(shoppingList);
            }
        });

        rvShoppingList.setAdapter(adapter);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rvShoppingList);

        setUpOnClickListeners();
        queryShoppingLists();
    }

    /**
     * Sets up all the onClickListeners
     */
    private void setUpOnClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewShoppingList();
            }
        });
    }

    /**
     * Opens the fragment to create a new shopping list
     */
    private void createNewShoppingList() {
        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, createShoppingListFragment).commit();
    }

    /**
     * Gets all the shopping lists from the database
     */
    private void queryShoppingLists() {
        ParseQuery<ShoppingList> query = ParseQuery.getQuery(ShoppingList.class);

        query.whereEqualTo(ShoppingList.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ShoppingList>() {
            @Override
            public void done(List<ShoppingList> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting shopping lists", e);
                    return;
                }
                progress_circular.setVisibility(View.GONE);
                shoppingLists.clear();
                shoppingLists.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Open the shopping list details to show all it's items
     * @param shoppingList
     */
    private void openShoppingListDetails(ShoppingList shoppingList) {
        shoppingListFragment = ShoppingListFragment.newInstance(shoppingList);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, shoppingListFragment)
                .commit();
    }

    @Override
    public void closeCreateShoppingListFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .remove(createShoppingListFragment)
                .commit();
    }

    @Override
    public void shoppingListCreated(ShoppingList shoppingList) {
        closeCreateShoppingListFragment();
        shoppingLists.add(0, shoppingList);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void closeShoppingList() {
        queryShoppingLists();
        getChildFragmentManager()
                .beginTransaction()
                .remove(shoppingListFragment)
                .commit();
    }
}