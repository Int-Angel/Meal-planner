package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter that shows a list of users
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    /**
     * Interface to open a user profile
     */
    public interface UsersAdapterListener {
        void openUserDetails(ParseUser user);
    }

    private Context context;
    private List<ParseUser> users;
    private UsersAdapterListener listener;

    public UsersAdapter(Context context, List<ParseUser> users, UsersAdapterListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * User view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);

            itemView.setOnClickListener(this);
        }

        /**
         * Update de view inside of the view holder with this data
         *
         * @param user
         */
        public void bind(ParseUser user) {
            if (user.getParseFile("image") != null) {
                Glide.with(context)
                        .load(user.getParseFile("image").getUrl())
                        .transform(new CenterCrop(), new RoundedCorners(1000))
                        .into(ivProfileImage);
            } else {
                Glide.with(context)
                        .load("https://happytravel.viajes/wp-content/uploads/2020/04/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png")
                        .transform(new CenterCrop(), new RoundedCorners(1000))
                        .into(ivProfileImage);
            }

            tvUsername.setText(user.getUsername());
            tvName.setText(user.getString("name") + " " + user.getString("lastname"));
        }

        @Override
        public void onClick(View v) {
            listener.openUserDetails(users.get(getAdapterPosition()));
        }
    }
}
