package com.percolate.mentions.sample.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.percolate.mentions.sample.R;
import com.percolate.mentions.sample.models.User;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Adapter to the mentions list shown to display the result of an '@' mention.
 */
public class UsersAdapter extends RecyclerArrayAdapter<User, UsersAdapter.UserViewHolder> {

    /**
     * {@link Context}.
     */
    private final Context context;

    /**
     * Current search string typed by the user.  It is used highlight the query in the
     * search results.  Ex: @bill.
     */
    private String currentQuery;

    /**
     * {@link ForegroundColorSpan}.
     */
    private final ForegroundColorSpan colorSpan;

    public UsersAdapter(final Context context) {
        this.context = context;
        final int orange = ContextCompat.getColor(context, R.color.mentions_default_color);
        this.colorSpan = new ForegroundColorSpan(orange);
    }

    /**
     * Setter for what user has queried.
     */
    public void setCurrentQuery(final String currentQuery) {
        if (StringUtils.isNotBlank(currentQuery)) {
            this.currentQuery = currentQuery.toLowerCase(Locale.US);
        }
    }

    /**
     * Create UI with views for user name and picture.
     */
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Display user name and picture.
     */
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final User mentionsUser = getItem(position);

        if(mentionsUser != null && StringUtils.isNotBlank(mentionsUser.getFullName())) {
            holder.name.setText(mentionsUser.getFullName(), TextView.BufferType.SPANNABLE);
            highlightSearchQueryInUserName(holder.name.getText());
            if (StringUtils.isNotBlank(mentionsUser.getImageUrl())) {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(mentionsUser.getImageUrl())
                        .into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Highlights the current search text in the mentions list.
     */
    private void highlightSearchQueryInUserName(CharSequence userName) {
        if (StringUtils.isNotBlank(currentQuery)) {
            int searchQueryLocation = userName.toString().toLowerCase(Locale.US).indexOf(currentQuery);

            if (searchQueryLocation != -1) {
                Spannable userNameSpannable = (Spannable) userName;
                userNameSpannable.setSpan(
                        colorSpan,
                        searchQueryLocation,
                        searchQueryLocation + currentQuery.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * View holder for user.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView imageView;

        UserViewHolder(View itemView) {
            super(itemView);
            name = ViewUtils.findViewById(itemView, R.id.user_full_name);
            imageView = ViewUtils.findViewById(itemView, R.id.user_picture);
        }
    }
}
