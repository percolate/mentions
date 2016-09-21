package com.percolate.mentions;

import android.annotation.SuppressLint;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for editing and highlighting a mention.
 */
@SuppressLint("SetTextI18n")
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MentionEditingUnitTest {

    /**
     * Test replacement text.
     */
    private final String TEST_STRING = "Test";

    /**
     * {@link EditText}.
     */
    private EditText editText;

    /**
     * Mock {@link Mentionable}.
     */
    private Mentionable mention;

    /**
     * {@link MentionInsertionUtils}.
     */
    private MentionInsertionUtils mentionInsertionUtils;

    /**
     * Create mention "Brent Watson" using Mockito and instantiate {@link MentionInsertionUtils}
     * with a null parameter for the {@link EditText} view, because it is not needed for testing
     * the {@link MentionInsertionUtils#updateInternalMentionsArray(int, int, int)} method.
     */
    @Before
    public void setUp() {
        editText = new EditText(RuntimeEnvironment.application);
        mentionInsertionUtils = new MentionInsertionUtils(editText);

        // Create mention with name "Brent Watson" and offset 6 due to the string "Hello " before it.
        mention = MentionTestUtils.createMockMention("Hello ".length(), "Brent Watson");
        editText.setText("Hello Brent Watson");
        mentionInsertionUtils.addMentionToInternalArray(mention, mention.getMentionOffset());
    }

    /**
     * "Hello Brent Watson" -> "Hello Brent WatsoTestn".
     *
     * Edit within the mention "Brent Watson" and test whether the mention was removed.
     */
    @Test
    public void testMentionRemovalByEditing() {
        editText.setText("Hello Brent WatsoTestn");
        mentionInsertionUtils.updateInternalMentionsArray(mention.getMentionLength() - 1, 0, TEST_STRING.length());
        assertTrue("Did not remove mention", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "".
     *
     * Select all the text and remove it and test whether the mention was removed.
     */
    @Test
    public void testMentionRemovalByDeletingAllText() {
        editText.setText("");
        mentionInsertionUtils.updateInternalMentionsArray(0, "Hello Brent Watson".length(), 0);
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "Hatson".
     *
     * Perform selection "ello Brent W" and remove text. Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialDeletion() {
        editText.setText("Hatson");
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(), 0);
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "HTestatson".
     *
     * Perform selection "ello Brent W" and replace with text {@code TEST_STRING}.
     * Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialReplacement() {
        editText.setText("HTestatson");
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(), TEST_STRING.length());
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "Hello TestBrent Watson".
     *
     * Add {@code TEST_STRING} before mention "Brent Watson" and test whether the mentions'
     * offset was updated.
     */
    @Test
    public void testMentionOffsetUpdate() {
        editText.setText("Hello TestBrent Watson");
        mentionInsertionUtils.updateInternalMentionsArray(6, 0, TEST_STRING.length());

        final List<Mentionable> mentions = mentionInsertionUtils.getMentions();
        assertNotNull(mentions);
        assertTrue("Mention should not have been removed.", mentions.size() == 1);
        assertTrue("Mention does not have correct offset.",
                mention.getMentionOffset() == "Hello ".length() + TEST_STRING.length());
    }
}
