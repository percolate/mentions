package com.percolate.mentions.sample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.percolate.caffeine.ViewUtils;
import com.percolate.mentions.sample.R;
import com.percolate.mentions.sample.adapters.CommentsAdapter;
import com.percolate.mentions.sample.adapters.UsersArrayAdapter;
import com.percolate.mentions.sample.models.Comment;
import com.percolate.mentions.sample.models.Mention;
import com.percolate.mentions.sample.models.User;
import com.percolate.mentions.sample.utils.MentionsLoaderUtils;
import com.percolate.mentions.Mentions;
import com.percolate.mentions.QueryListener;
import com.percolate.mentions.SuggestionsListener;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Sample App which demonstrates how to use the Mentions library. A comment box is displayed at
 * the bottom which allows you to '@' mention a user. Click on the send button will display the
 * test comment above the comment box. All the mentions you choose will be highlighted.
 */
public class MainActivity extends AppCompatActivity implements QueryListener, SuggestionsListener {

    private EditText commentField;

    private Button sendCommentButton;

    private UsersArrayAdapter usersArrayAdapter;
    private CommentsAdapter commentsAdapter;

    MentionsLoaderUtils mentionsLoaderUtils;

    public Mentions mentions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        commentField = ViewUtils.findViewById(this, R.id.comment_field);
        sendCommentButton = ViewUtils.findViewById(this, R.id.send_comment);

        mentions = new Mentions.Builder(this, commentField)
                .suggestionsListener(this)
                .queryListener(this)
                .build();

        mentionsLoaderUtils = new MentionsLoaderUtils(this);

        setupMentionsList();
        setupAddedMentionsList();
        setupSendButtonTextWatcher();

    }

    /**
     * Setups the mentions suggestions list. Creates and sets and adapter for
     * the mentions list and sets the on item click listener.
     */
    private void setupMentionsList() {
        ListView mentionsList = ViewUtils.findViewById(this, R.id.mentions_list);

        // set adapter
        usersArrayAdapter = new UsersArrayAdapter(this);
        mentionsList.setAdapter(usersArrayAdapter);

        // set on item click listener
        mentionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = usersArrayAdapter.getItem(i);

                /*
                 * We are creating a mentions object which implements the
                 * <code>Mentionable</code> interface this allows the library to set the offset
                 * and length of the mention.
                 */
                Mention mention = new Mention();
                mention.setMentionName(user.getFullName());
                mentions.insertMention(mention);
            }
        });
    }


    /**
     * After typing some text with mentions in the comment box and clicking on send, you will
     * be able to see the comment with the mentions highlighted above the comment box. This method
     * setups the adapter for this list.
     */
    private void setupAddedMentionsList() {

        // setup adapter
        final ListView commentsList = ViewUtils.findViewById(this, R.id.comments_list);
        commentsAdapter = new CommentsAdapter(this);
        commentsList.setAdapter(commentsAdapter);

        // setup send button
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isNotBlank(commentField.getText())) {

                    // hide empty view
                    ViewUtils.hideView(MainActivity.this, R.id.comments_empty_view);

                    // add comment to list
                    Comment comment = new Comment();
                    comment.setComment(commentField.getText().toString());
                    comment.setMentions(mentions.getInsertedMentions());
                    commentsAdapter.add(comment);

                    // clear comment field
                    commentField.setText("");
                }
            }
        });
    }

    /**
     * You could add your own text watcher to the edit text view in addition to the text
     * watcher the mentions library sets. This text watcher will toggle the text color
     * of the send button depending on whether something has been typed into the edit text
     * view.
     */
    private void setupSendButtonTextWatcher() {
        commentField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendCommentButton.setTextColor(MainActivity.this.getResources().getColor(R.color.orange));
                } else {
                    sendCommentButton.setTextColor(MainActivity.this.getResources().getColor(R.color.orange_faded));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

        });
    }

    @Override
    public void onQueryReceived(String query) {
        List<User> users = mentionsLoaderUtils.searchUsers(query);
        if (users != null && !users.isEmpty()) {

            usersArrayAdapter.clear();
            usersArrayAdapter.setCurrentQuery(query);
            usersArrayAdapter.addAll(users);

            showMentionsList(true);

        } else {

            showMentionsList(false);
        }

    }

    /**
     * If a user didn't enter a valid query, then this method will be called by the library to
     * notify you that the mentions list should be hidden. This method will also be called if
     * you insert a mention into the EditText view.
     *
     * @param display  boolean  true is the mentions list layout showed be shown or false
     *                 otherwise.
     */
    @Override
    public void displaySuggestions(boolean display) {
        if (display) {
            ViewUtils.showView(this, R.id.mentions_list_layout);
        } else {
            ViewUtils.hideView(this, R.id.mentions_list_layout);
        }
    }

    /**
     * Toggle the mentions list's visibility if there are search results returned for search
     * query. Shows the empty list view
     *
     * @param display boolean   true if the mentions list should be shown or false if
     *                          the empty suggestions list view should be shown.
     */
    private void showMentionsList(boolean display) {

        ViewUtils.showView(this, R.id.mentions_list_layout);

        if (display) {
            ViewUtils.showView(this, R.id.mentions_list);
            ViewUtils.hideView(this, R.id.mentions_empty_view);
        } else {
            ViewUtils.hideView(this, R.id.mentions_list);
            ViewUtils.showView(this, R.id.mentions_empty_view);
        }
    }

}
