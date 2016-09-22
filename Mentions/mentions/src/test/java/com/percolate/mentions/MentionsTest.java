package com.percolate.mentions;

import android.annotation.SuppressLint;
import android.widget.EditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MentionsTest {

    @Test
    public void testBuilder() {
        // Create mentions object.
        final EditText editText = new EditText(RuntimeEnvironment.application);
        final QueryListener queryListener = new QueryListener() {
            @Override
            public void onQueryReceived(final String query) {}
        };
        final SuggestionsListener suggestionsListener = new SuggestionsListener() {
            @Override
            public void displaySuggestions(final boolean display) {}
        };

        final Mentions mentions = new Mentions.Builder(RuntimeEnvironment.application, editText)
                .maxCharacters(10)
                .highlightColor(android.R.color.darker_gray)
                .queryListener(queryListener)
                .suggestionsListener(suggestionsListener)
                .build();

        //Verify all params for mentions object
        assertNotNull("Context is null.", mentions.context);
        assertTrue("Max characters is not 10.", mentions.mentionCheckerUtils.maxCharacters == 10);
        assertTrue("Highlight color is not gray.", mentions.mentionInsertionUtils.textHighlightColor == android.R.color.darker_gray);
        assertNotNull("Query listener is null.", mentions.queryListener == queryListener);
        assertNotNull("Suggestion listener is null.", mentions.suggestionsListener == suggestionsListener);
    }

    @Test
    @SuppressLint("SetTextI18n")
    public void testAddMentions() {
        final EditText editText = new EditText(RuntimeEnvironment.application);
        editText.setText("Brent Watson");
        final Mentions mentionsLib = new Mentions.Builder(RuntimeEnvironment.application, editText).build();

        //Add mentions
        final List<Mentionable> mentions = new ArrayList<>();
        final Mention mention = new Mention();
        mention.setMentionName("Brent Watson");
        mention.setMentionOffset(0);
        mention.setMentionLength("Brent Watson".length());
        mentions.add(mention);
        mentionsLib.addMentions(mentions);

        //Verify mentions were added
        final List<Mentionable> insertedMentions = mentionsLib.getInsertedMentions();
        assertTrue("No inserted mentions.", insertedMentions.size() == 1);
        assertEquals(insertedMentions.get(0).getMentionName(), "Brent Watson");
        assertEquals(insertedMentions.get(0).getMentionOffset(), 0);
        assertEquals(insertedMentions.get(0).getMentionLength(), "Brent Watson".length());
    }
}
