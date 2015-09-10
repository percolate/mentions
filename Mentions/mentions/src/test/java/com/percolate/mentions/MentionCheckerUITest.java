package com.percolate.mentions;

import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Test whether a search is valid or not by the rules defined in
 * {@link MentionCheckerUtils}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MentionCheckerUITest {

    // Mock EditText
    private EditText editText;

    // MentionCheckerUtils
    private MentionCheckerUtils mentionCheckerUtils;

    /**
     * Use Robolectric to create mock {@link EditText} view.
     * Instaniate {@link MentionCheckerUtils}.
     */
    @Before
    public void setUp() {
        editText = new EditText(RuntimeEnvironment.application);
        mentionCheckerUtils = new MentionCheckerUtils(editText);
    }

    /**
     * Set threshold to 3 characters and test whether only string with length
     * less than or equal to 3 are considered valid queries.
     */
    @Test
    public void checkMentionCharacterLimit() {
        mentionCheckerUtils.setMaxCharacters(3);

        // 1 character (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @B");
        String query1 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query1.equals("B"));

        // 2 characters (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Br");
        String query2 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query2.equals("Br"));

        // 3 characters (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Bre");
        String query3 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query3.equals("Bre"));

        // 4 characters (Fail out of char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Bren");
        String query4 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query4.isEmpty());
    }

    /**
     * Tests whether emails such as "hello@percolate.com" are not considered
     * queries for mentions. If there is text before the @ symbol, it should not be a valid
     * query.
     */
    @Test
    public void checkSearchFailsOnEmail() {
        MentionTestUtils.setTextAndSelection(editText, "hello@percolate.com");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("An email is being considered as a mention.", query.isEmpty());
    }

    /**
     * If a search begins with non alphanumeric symbols, then it should fail.
     */
    @Test
    public void checkSearchFailsOnSymbols() {
        MentionTestUtils.setTextAndSelection(editText, "@!Brent W");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("A search beginning with an non alphanumeric character was valid.", query.isEmpty());
    }

    /**
     * If a search begins with @@ then, it should not consider the extra @ symbol as search text.
     */
    @Test
    public void checkSearchFailsOnDoubleAt() {
        MentionTestUtils.setTextAndSelection(editText, "@@");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("Double @@ symbol was valid.", query.isEmpty());
    }

    /**
     * If {@link EditText} is set to a blank string, then it should not return a query.
     */
    @Test
    public void checkSearchFailsOnBlankString() {
        MentionTestUtils.setTextAndSelection(editText, "");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("A blank search returned a query.", query.isEmpty());
    }

    /**
     * If {@link EditText} is set to null, then it should not return a query.
     */
    @Test
    public void checkSearchFailsOnNull() {
        MentionTestUtils.setTextAndSelection(editText, null);
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("A null search returned a query.", query.isEmpty());
    }

    /**
     * Tests whether a search that begins with an alpha numeric character is valid.
     */
    @Test
    public void checkSearchPassesOnAlphaNumeric() {
        MentionTestUtils.setTextAndSelection(editText, "@Brent Watson");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("Search beginning with alpha numeric character failed.", !query.isEmpty());
    }

}
