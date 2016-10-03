package com.percolate.mentions.sample.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.percolate.mentions.Mentionable;
import com.percolate.mentions.sample.R;
import com.percolate.mentions.sample.models.Comment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Adapter for added {@link Comment}s.
 */
public class CommentsAdapter extends RecyclerArrayAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    /**
     * {@link Context}.
     */
    private final Context context;

    /**
     * Orange color.
     */
    private final int orange;

    public CommentsAdapter(final Context context) {
        this.context = context;
        this.orange = ContextCompat.getColor(context, R.color.mentions_default_color);
    }

    /**
     * Create UI with comment and image of commenter.
     */
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * Display comment and commenter's image. Highlight any {@link Mentionable}s in the {@link Comment}s.
     */
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        final Comment comment = getItem(position);
        if(comment != null && StringUtils.isNotBlank(comment.getComment())) {
            holder.comment.setText(comment.getComment());
            highlightMentions(holder.comment, comment.getMentions());
        }
    }

    /**
     * Highlights all the {@link Mentionable}s in the test {@link Comment}.
     */
    private void highlightMentions(final TextView commentTextView, final List<Mentionable> mentions) {
        if(commentTextView != null && mentions != null && !mentions.isEmpty()) {
            final Spannable spannable = new SpannableString(commentTextView.getText());

            for (Mentionable mention: mentions) {
                if (mention != null) {
                    final int start = mention.getMentionOffset();
                    final int end = start + mention.getMentionLength();

                    if (commentTextView.length() >= end) {
                        spannable.setSpan(new ForegroundColorSpan(orange), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        commentTextView.setText(spannable, TextView.BufferType.SPANNABLE);
                    } else {
                        //Something went wrong.  The expected text that we're trying to highlight does not
                        // match the actual text at that position.
                        Log.w("Mentions Sample", "Mention lost. [" + mention + "]");
                    }
                }
            }
        }
    }

    /**
     * View holder for comment.
     */
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        final TextView comment;

        CommentViewHolder(View itemView) {
            super(itemView);
            comment = ViewUtils.findViewById(itemView, R.id.comment);
        }
    }

}
