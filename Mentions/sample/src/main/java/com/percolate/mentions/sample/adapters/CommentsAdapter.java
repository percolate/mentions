package com.percolate.mentions.sample.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.percolate.mentions.sample.R;
import com.percolate.mentions.sample.models.Comment;
import com.percolate.mentions.Mentionable;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Adapter for added {@link Comment}s.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    private Context context;

    public CommentsAdapter(Context context) {
        super(context, R.layout.item_comment);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent,
                    false);
            holder = new Holder();
            holder.comment = ViewUtils.findViewById(convertView, R.id.comment);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Comment comment = getItem(position);

        // highlight any {@link Mentionable}s in the {@link Comment}s.
        if(comment != null && StringUtils.isNotBlank(comment.getComment())) {
            holder.comment.setText(comment.getComment());
            highlightMentions(holder.comment, comment.getMentions());
        }

        return convertView;


      }

    /**
     * Highlights all the {@link Mentionable}s in the test {@link Comment}.
     */
    private void highlightMentions(TextView commentTextView, List<Mentionable> mentions) {
        if(commentTextView != null && mentions != null && !mentions.isEmpty()) {

            Spannable spannable = new SpannableString(commentTextView.getText());

            for (Mentionable mention: mentions) {
                if (mention != null) {

                    int start = mention.getMentionOffset();
                    int end = start + mention.getMentionLength();

                    if (commentTextView.length() >= end) {
                        ForegroundColorSpan mentionColorSpan = new ForegroundColorSpan(context
                                .getResources().getColor(R.color.orange));
                        spannable.setSpan(mentionColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        commentTextView.setText(spannable, TextView.BufferType.SPANNABLE);
                    } else {
                        //Something went wrong.  The expected text that we're trying to highlight
                        // does not match the actual text at that position.
                        Log.w("Mentions Sample", "Mention lost. [" + mention + "]");
                    }
                }
            }
        }
    }

    public class Holder {
        public TextView comment;
    }
}
