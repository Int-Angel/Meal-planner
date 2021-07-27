package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealplanner.R;
import com.example.mealplanner.adapters.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SocialFragment extends Fragment {

    private final static String TAG = "SocialFragment";

    private List<ParseUser> users;
    private UsersAdapter adapter;
    private String queryUsername;

    private SearchView search_users;
    private RecyclerView rvUsers;
    private ProgressBar progress_circular;
    private TextView tvNoUsers;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryUsername = "";
        users = new ArrayList<>();

        search_users = view.findViewById(R.id.search_users);
        rvUsers = view.findViewById(R.id.rvUsers);
        progress_circular = view.findViewById(R.id.progress_circular);
        tvNoUsers = view.findViewById(R.id.tvNoUsers);

        adapter = new UsersAdapter(getContext(), users);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        setUpSearchBar();
        queryUsers();
    }

    private void setUpSearchBar(){
        search_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryUsername = query;
                queryUsers();
                search_users.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        search_users.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                queryUsername = "";
                queryUsers();
                search_users.clearFocus();
                return false;
            }
        });
    }

    private void queryUsers() {
        progress_circular.setVisibility(View.VISIBLE);
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        if (!queryUsername.equals("")){
            query.whereEqualTo("username", queryUsername);
        }


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                progress_circular.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG, "Error while getting users", e);
                    return;
                }
                if (objects.size() == 0) {
                    tvNoUsers.setVisibility(View.VISIBLE);
                } else {
                    tvNoUsers.setVisibility(View.GONE);
                }

                users.clear();
                users.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}