package com.percolate.mentions;

import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for editing and highlighting a mention.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MentionInsertionUtilsTest {

    private final String TEST_STRING = "Test";

    private EditText editText;

    @Mock
    private Mentionable mention;

    private MentionInsertionUtils mentionInsertionUtils;

    @Before
    public void setUp() {
        initMocks(this);
        createMention();
        editText = new EditText(RuntimeEnvironment.application);
        mentionInsertionUtils = new MentionInsertionUtils(editText);
        mentionInsertionUtils.addMentionToInternalArray(mention, mention.getMentionOffset());
    }

    /**
     * "Hello Brent Watson" -> "Hello Brent WatsoTestn".
     *
     * Edit within the mention "Brent Watson" and test whether the mention was removed.
     */
    @Test
    public void testMentionRemovalByEditing() {
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
        mentionInsertionUtils.updateInternalMentionsArray(0, "Hello Brent Watson".length(), 0);
        assertTrue("Did not remove mention", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "Hatson".
     *
     * Perform selection "ello Brent W" and remove text. Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialDeletion() {
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(), 0);
        assertTrue("Did not remove mention", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "HTestatson".
     *
     * Perform selection "ello Brent W" and replace with text <code>TEST_STRING</code>.
     * Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialReplacement() {
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(),
                TEST_STRING.length());
        assertTrue("Did not remove mention", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "Hello TestBrent Watson".
     *
     * Add <code>TEST_STRING</code> before mention "Brent Watson" and test whether the mentions'
     * offset was updated.
     */
    @Test
    public void testMentionOffsetUpdate() {
        mentionInsertionUtils.updateInternalMentionsArray(6, 0, TEST_STRING.length());

        List<Mentionable> mentions = mentionInsertionUtils.getMentions();

        assertNotNull(mentions);
        assertTrue("Mentions should not have been removed", mentions.size() == 1);
        verify(mention).setMentionOffset("Hello ".length() + TEST_STRING.length());
    }

    /**
     * Test whether the mention "Brent Watson" is highlighted in the text "Hello Brent Watson".
     */
    @Test
    public void testMentionHighlighting() {
        editText.setText("Hello Brent Watson");
        mentionInsertionUtils.highlightMentionsText();
        ForegroundColorSpan[] highlightSpans = editText.getEditableText().getSpans(6,
                                            editText.getText().length(), ForegroundColorSpan.class);
        assertNotNull(highlightSpans);
        assertTrue("Did not highlight mention", highlightSpans.length == 1);
    }

    /**
     * Creates a mock mention with name "Brent Watson" at offset 6. The offset is 6, because we are
     * inserting the string "Hello " before the mention.
     */
    private void createMention() {
        when(mention.getMentionOffset()).thenReturn("Hello ".length());
        when(mention.getMentionName()).thenReturn("Brent Watson");
        when(mention.getMentionLength()).thenReturn("Brent Watson".length());
    }

}
