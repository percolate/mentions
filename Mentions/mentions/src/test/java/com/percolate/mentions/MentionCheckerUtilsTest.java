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
public class MentionCheckerUtilsTest {

    private EditText editText;

    private MentionCheckerUtils mentionCheckerUtils;

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
        setTextAndSelection("Hello @B");
        String query1 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query1.equals("B"));

        // 2 characters (Pass within char limit)
        setTextAndSelection("Hello @Br");
        String query2 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query2.equals("Br"));

        // 3 characters (Pass within char limit)
        setTextAndSelection("Hello @Bre");
        String query3 = mentionCheckerUtils.doMentionCheck();
        assertTrue("Query is invalid.", query3.equals("Bre"));

        // 4 characters (Fail out of char limit)
        setTextAndSelection("Hello @Bren");
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
        setTextAndSelection("hello@percolate.com");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("An email is being considered as a mention.", query.isEmpty());
    }

    /**
     * If a search begins with non alphanumeric symbols, then it should fail.
     */
    @Test
    public void checkSearchFailsOnSymbols() {
        setTextAndSelection("@!Brent W");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("A search beginning with an non alphanumeric character was valid", query.isEmpty());
    }

    /**
     * Tests whether a search that begins with an alpha numeric character is valid.
     */
    @Test
    public void checkSearchPassesOnAlphaNumeric() {
        setTextAndSelection("@Brent Watson");
        String query = mentionCheckerUtils.doMentionCheck();
        assertTrue("Search beginning with alpha numeric character failed.", !query.isEmpty());
    }

    /**
     * Set text and selection in {@link EditText} view.
     */
    private void setTextAndSelection(String text) {
        editText.setText(text);
        editText.setSelection(text.length());
    }

}
