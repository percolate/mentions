package com.percolate.sample;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Mentions class contains a Builder through which you could set the text highlight color,
 * query listener or add mentions. Pass in an {@link EditText} view to the builder and the library
 * will setup the ability to '@' mention.
 */
public class Mentions {

    private Context context;

    private EditText editText;

    private QueryListener queryListener;

    private SuggestionsListener suggestionsListener;

    // helper classes to handle checking and inserting mentions
    private MentionCheckerUtils mentionCheckerUtils;
    private MentionInsertionUtils mentionInsertionUtils;

    private Mentions(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;

        this.mentionCheckerUtils = new MentionCheckerUtils(editText);
        this.mentionInsertionUtils = new MentionInsertionUtils(editText);
    }

    /**
     * This Builder allows you to configure mentions by setting up a highlight color, max
     * number of words to search or by pre-populating mentions.
     */
    public static class Builder {

        private Mentions mentionsLib;

        /**
         * Builder which allows you configure mentions. A {@link Context} and {@link EditText} is
         * required by the Builder.
         *
         * @param context   Context     Context
         * @param editText  EditText    The {@link EditText} view to which you want to add the
         *                              ability to mention.
         *
         */
        public Builder(Context context, EditText editText) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            } else if (editText == null) {
                throw new IllegalArgumentException("EditText must not be null.");
            }
            this.mentionsLib = new Mentions(context, editText);
        }

        /**
         * The {@link EditText} may have some text and mentions already in it. This method is used
         * to pre-populate the existing {@link Mentionable}s and highlight them.
         *
         * @param mentions  List<Mentionable>   An array of {@link Mentionable}s that are
         *                                      currently in the {@link EditText}.
         */
        public Builder addMentions(List<? extends Mentionable> mentions) {
            mentionsLib.mentionInsertionUtils.addMentions(mentions);
            return this;
        }

        /**
         * Set a color to highlight the mentions' text. The default color is orange.
         *
         * @param color     int     The color to use to highlight a {@link Mentionable}'s text.
         */
        public Builder highlightColor(int color) {
            mentionsLib.mentionInsertionUtils.setTextHighlightColor(color);
            return this;
        }

        /**
         * Set the maximum number of characters the user may have typed until the search text
         * is invalid.
         *
         * @param maxCharacters     int     The number of characters within which anything typed
         *                                  after the '@' symbol will be evaluated.
         */
        public Builder maxCharacters(int maxCharacters) {
            mentionsLib.mentionCheckerUtils.setMaxCharacters(maxCharacters);
            return this;
        }

        /**
         * Set a listener that will provide you with a valid token.
         *
         * @param queryListener     QueryListener   The listener to set to be notified of a valid
         *                                          query.
         */
        public Builder queryListener(QueryListener queryListener) {
            mentionsLib.queryListener = queryListener;
            return this;
        }

        /**
          * Set a listener to notify you whether you should hide or display a drop down with
         * {@link Mentionable}s.
         *
         * @param suggestionsListener   SuggestionsListener     The listener for display
         *                                                      suggestions.
         */
        public Builder suggestionsListener(SuggestionsListener suggestionsListener) {
            mentionsLib.suggestionsListener = suggestionsListener;
            return this;
        }

        /**
         * Builds and returns a {@link Mentions} object.
         */
        public Mentions build() {
            mentionsLib.hookupInternalTextWatcher();
            mentionsLib.hookupOnClickListener();
            return mentionsLib;
        }

    }

    /**
     * You may be pre-loading text with {@link Mentionable}s. In order to highlight and make those
     * {@link Mentionable}s recognizable by the library, you may add them by using this method.
     *
     * @param mentionables  List<? extends Mentionable>  Any pre-existing mentions that you
     *                                                   want to add to the library.
     */
    public void addMentions(List<? extends Mentionable> mentionables) {
        mentionInsertionUtils.addMentions(mentionables);
    }

    /**
     * Returns an array with all the inserted {@link Mentionable}s in the {@link EditText}.
     *
     * @return List<Mentionable>    An array containing all the inserted {@link Mentionable}s.
     */
    public List<Mentionable> getInsertedMentions() {
        return new ArrayList<>(mentionInsertionUtils.getMentions());
    }

    /**
     * If the user sets the cursor after an '@', then perform a mention.
     */
    private void hookupOnClickListener() {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mentionCheckerUtils.currentWordStartsWithAtSign()) {
                    mentionCheckerUtils.doMentionCheck();
                }
            }
        });
    }

    /**
     * Set a {@link TextWatcher} for mentions.
     */
    private void hookupInternalTextWatcher() {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                mentionInsertionUtils.checkIfProgrammaticallyClearedEditText(charSequence, start,
                        count, after);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mentionInsertionUtils.updateInternalMentionsArray(start, before, count);
                mentionInsertionUtils.highlightMentionsText();

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = mentionCheckerUtils.doMentionCheck();
                queryReceived(query);
            }
        });
    }

    /**
     * Insert a mention the user has chosen in the {@link EditText} and notify the user
     * to hide the suggestions list.
     *
     * @param mentionable   Mentionable     A {@link Mentionable} chosen by the user to display
     *                                      and highlight in the {@link EditText}.
     */
    public void insertMention(Mentionable mentionable) {
        mentionInsertionUtils.insertMention(mentionable);
        suggestionsListener.displaySuggestions(false);
    }

    /**
     * If the user typed a query that was valid then return it. Otherwise, notify you to close
     * a suggestions list.
     *
     * @param query     String      A valid query.
     */
    public void queryReceived(String query) {
        if (queryListener != null && StringUtils.isNotBlank(query)) {
            queryListener.onQueryReceived(query);
        } else {
            suggestionsListener.displaySuggestions(false);
        }
    }

}
