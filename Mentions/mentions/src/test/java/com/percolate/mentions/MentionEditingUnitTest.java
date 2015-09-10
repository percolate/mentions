package com.percolate.mentions;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Tests for editing and highlighting a mention.
 */
public class MentionEditingUnitTest {

    // Test replacement text
    private final String TEST_STRING = "Test";

    // Mock Mention
    private Mentionable mention;

    // MentionInsertionUtils
    private MentionInsertionUtils mentionInsertionUtils;

    /**
     * Create mention "Brent Watson" using Mockito and instaniate {@link MentionInsertionUtils}
     * with a null parameter for the {@link EditText} view, because it is not needed for testing
     * the <code>MentionInsertionUtils#updateInternalMentionsArray</code> method.
     */
    @Before
    public void setUp() {

        // create mention with name "Brent Watson" and offset 6 due to the string "Hello " before it.
        mention = MentionTestUtils.createMockMention("Hello ".length(), "Brent Watson");

        // create {@link MentionInsertionUtils} and add mention.
        mentionInsertionUtils = new MentionInsertionUtils(null);
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
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "Hatson".
     *
     * Perform selection "ello Brent W" and remove text. Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialDeletion() {
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(), 0);
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
    }

    /**
     * "Hello Brent Watson" -> "HTestatson".
     *
     * Perform selection "ello Brent W" and replace with text <code>TEST_STRING</code>.
     * Test if the mention was removed.
     */
    @Test
    public void testMentionRemovalByPartialReplacement() {
        mentionInsertionUtils.updateInternalMentionsArray(1, "ello Brent W".length(), TEST_STRING.length());
        assertTrue("Did not remove mention.", mentionInsertionUtils.getMentions().size() == 0);
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
        assertTrue("Mentions should not have been removed.", mentions.size() == 1);
        verify(mention).setMentionOffset("Hello ".length() + TEST_STRING.length());
    }
}
