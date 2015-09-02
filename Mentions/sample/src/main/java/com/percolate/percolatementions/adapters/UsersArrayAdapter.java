package com.percolate.percolatementions.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.percolate.percolatementions.R;
import com.percolate.percolatementions.models.User;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Adapter to the mentions list shown to display the result of an '@' mention.
 */
public class UsersArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private String currentQuery;

    public UsersArrayAdapter(Context context) {
        super(context, R.layout.item_user);

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            holder = new Holder();
            holder.name = ViewUtils.findViewById(convertView, R.id.user_full_name);
            holder.imageView = ViewUtils.findViewById(convertView, R.id.user_picture);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final User mentionsUser = getItem(position);

        if(mentionsUser != null && StringUtils.isNotBlank(mentionsUser.getFullName())) {

            // user name
            holder.name.setText(mentionsUser.getFullName(), TextView.BufferType.SPANNABLE);

            highlightSearchQueryInUserName(holder.name.getText());

            // user picture
            if (StringUtils.isNotBlank(mentionsUser.getImageUrl())) {
                Picasso.with(context).load(mentionsUser.getImageUrl()).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void setCurrentQuery(String currentQuery) {
        if (StringUtils.isNotBlank(currentQuery)) {
            this.currentQuery = currentQuery.toLowerCase(Locale.US);
        }
    }

    /**
     * Highlights the current search text in the mentions list.
     */
    public void highlightSearchQueryInUserName(CharSequence userName) {
        if (StringUtils.isNotBlank(currentQuery)) {
            int searchQueryLocation = userName.toString().toLowerCase(Locale.US).indexOf(currentQuery);

            if (searchQueryLocation != -1) {

                Spannable userNameSpannable = (Spannable) userName;

                userNameSpannable.setSpan(new ForegroundColorSpan(context.getResources()
                                                                        .getColor(R.color.orange)),
                        searchQueryLocation,
                        searchQueryLocation + currentQuery.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public class Holder {
        public TextView name;
        public ImageView imageView;
    }
}
