package com.percolate.mentions;

import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static com.percolate.mentions.MentionTestUtils.createMockMention;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests mention insertion and highlighting.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MentionInsertionUITest {

    /**
     * {@link EditText}.
     */
    private EditText editText;

    /**
     * {@link MentionInsertionUtils}.
     */
    private MentionInsertionUtils mentionInsertionUtils;

    /**
     * Create {@link EditText} view. Use the {@link EditText} to instantiate
     * {@link MentionInsertionUtils}.
     */
    @Before
    public void setUp() {
        editText = new EditText(RuntimeEnvironment.application);
        mentionInsertionUtils = new MentionInsertionUtils(editText);
    }

    /**
     * "Hello @Br" --> "Hello Brent Watson"
     *
     * Perfrom an @ mention by typing @Br and test whether Brent Watson was added to the
     * {@link EditText} view and whether the mention was highlighted.
     */
    @Test
    public void testMentionInsertion() {
        // perform @ mention
        MentionTestUtils.setTextAndSelection(editText, "Hello @Br");

        // create mock mention
        Mentionable mention = createMockMention("Hello ".length(), "Brent Watson");

        // insert mention into {@link EditText} view
        mentionInsertionUtils.insertMention(mention);

        // Test {@link EditText} has full mention name "Brent Watson"
        assertEquals(editText.getText().toString(), "Hello Brent Watson ");

        // Test mention is tracked internally {@link MentionInertionUtils}
        assertNotNull(mentionInsertionUtils.getMentions());
        assertTrue("The mention \"Brent Watson\" was not inserted.",
                    mentionInsertionUtils.getMentions().size() == 1);

        // Test "Brent Watson" was highlighted
        final ForegroundColorSpan[] highlightSpans = editText.getEditableText().getSpans("Hello ".length(),
                                            editText.getText().length(), ForegroundColorSpan.class);
        assertTrue("Did not highlight mention", highlightSpans.length == 1);
    }

    /**
     * Test whether inserting a null mention throws a {@link IllegalArgumentException}.
     */
    @Test(expected= IllegalArgumentException.class)
    public void testNullMentionInsertion() {
        mentionInsertionUtils.insertMention(null);
    }

    /**
     * Test whether inserting a mention with a blank name throws a {@link IllegalArgumentException}.
     */
    @Test(expected= IllegalArgumentException.class)
    public void testBlankMentionNameInsertion() {
        mentionInsertionUtils.insertMention(createMockMention("Hello ".length(), ""));
    }

    /**
     * An {@link EditText} view may have text pre-populated with mentions. The mehtod will
     * tests whether the pre-existing mentions are highlighted.
     */
    @Test
    public void testPrepopulateMentions() {
        // insert text with mentions "Brent Watson" and "Doug Tabuchi" into {@link EditText}.
        MentionTestUtils.setTextAndSelection(editText, "Hello Brent Watson and Doug Tabuchi");

        // create an array of mock mentions
        List<Mentionable> mentions = new ArrayList<>();
        mentions.add(createMockMention("Hello ".length(), "Brent Watson"));
        mentions.add(createMockMention("Hello Brent Watson and ".length(),
                                                                                   "Doug Tabuchi"));

        // add array of pre-existing mentions
        mentionInsertionUtils.addMentions(mentions);

        // Test whether all the mentions are tracked internally by {@link MentionInsertionUtils}.
        assertNotNull(mentionInsertionUtils.getMentions());
        assertTrue("Did not add mentions.", mentionInsertionUtils.getMentions().size() == 2);

        // Test mentions' test is highlighted with ForegroundColorSpan.
        ForegroundColorSpan[] highlightSpans1 = MentionTestUtils.getForegroundColorSpans(editText,
                                                        "Hello ".length(), "Brent Watson".length());
        assertTrue("The mention \"Brent Watson\" was not highlighted.", highlightSpans1.length == 1);

        ForegroundColorSpan[] highlightSpans2 = MentionTestUtils.getForegroundColorSpans(editText,
                                       "Hello Brent Watson and ".length(), "Doug Tabuchi".length());
        assertTrue("The mention \"Doug Tabuchi\" was not highlighted.", highlightSpans2.length == 1);
    }

}
