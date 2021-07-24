package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;


public class CreateNewShoppingListItemFragment extends Fragment {

    public interface CreateNewItemListener {
        void closeCreateNewItemFragment();

        void shoppingItemCreated(ShoppingListItem item);

        void shoppingItemEdited(ShoppingListItem item, int position, String oldAisle);
    }

    private final static String TAG = "CreateNewShoppingItem";
    private final static String SHOPPING_LIST = "shoppingList";
    private static final String SHOPPING_LIST_ITEM = "item";
    private static final String AISLE = "aisle";
    private static final String POSITION = "position";

    private ShoppingListItem item;
    private String oldAisle;
    private int position;
    private ShoppingList shoppingList;

    private CreateNewItemListener listener;

    private ImageButton ibtnClose;
    private EditText etItem;
    private EditText etAmount;
    private EditText etUnit;
    private EditText etAisle;
    private Button btnDone;

    public CreateNewShoppingListItemFragment() {
        // Required empty public constructor
    }

    public static CreateNewShoppingListItemFragment newInstance(ShoppingList shoppingList, ShoppingListItem item, String oldAisle, int position) {
        CreateNewShoppingListItemFragment fragment = new CreateNewShoppingListItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(SHOPPING_LIST_ITEM, Parcels.wrap(item));
        args.putParcelable(SHOPPING_LIST, Parcels.wrap(shoppingList));
        args.putString(AISLE, oldAisle);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateNewShoppingListItemFragment newInstance(ShoppingList shoppingList) {
        CreateNewShoppingListItemFragment fragment = new CreateNewShoppingListItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(SHOPPING_LIST, Parcels.wrap(shoppingList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = Parcels.unwrap(getArguments().getParcelable(SHOPPING_LIST_ITEM));
            shoppingList = Parcels.unwrap(getArguments().getParcelable(SHOPPING_LIST));
            oldAisle = getArguments().getString(AISLE);
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_shopping_list_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = (CreateNewItemListener) getParentFragment();

        ibtnClose = view.findViewById(R.id.ibtnClose);
        etItem = view.findViewById(R.id.etItem);
        etAmount = view.findViewById(R.id.etAmount);
        etUnit = view.findViewById(R.id.etUnit);
        etAisle = view.findViewById(R.id.etAisle);
        btnDone = view.findViewById(R.id.btnDone);

        if (item != null)
            bind();

        setUpOnClickListeners();
    }

    private void bind() {
        etItem.setText(item.getName());
        etAmount.setText(item.getAmount() + "");
        etUnit.setText(item.getUnit());
        etAisle.setText(item.getAisle());
    }

    private void createNewItem() {
        String name = etItem.getText().toString();
        String unit = etUnit.getText().toString();

        String amountStr = etAmount.getText().toString();
        float amount = 1;
        if (!amountStr.equals(""))
            amount = Float.parseFloat(amountStr);

        String aisle = etAisle.getText().toString();

        ShoppingListItem shoppingListItem = ShoppingListItem.createShoppingListItem(shoppingList, name, aisle, amount, unit);
        shoppingListItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't create new shopping list item", e);
                    return;
                }
                Log.i(TAG, "Item created");
                listener.shoppingItemCreated(shoppingListItem);
            }
        });
    }

    void updateItem() {
        item.deleteInBackground();

        String name = etItem.getText().toString();
        String unit = etUnit.getText().toString();

        String amountStr = etAmount.getText().toString();
        float amount = 1;
        if (!amountStr.equals(""))
            amount = Float.parseFloat(amountStr);

        String aisle = etAisle.getText().toString();

        ShoppingListItem shoppingListItem = ShoppingListItem.createShoppingListItem(shoppingList, name, aisle, amount, unit);
        shoppingListItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't create new shopping list item", e);
                    return;
                }
                Log.i(TAG, "Item created");

                listener.shoppingItemEdited(shoppingListItem, position, oldAisle);
            }
        });
    }

    private void setUpOnClickListeners() {
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeCreateNewItemFragment();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item == null)
                    createNewItem();
                else
                    updateItem();
            }
        });
    }
}