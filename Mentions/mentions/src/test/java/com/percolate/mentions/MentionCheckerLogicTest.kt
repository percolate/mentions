package com.percolate.mentions

import android.widget.EditText
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Test whether a search is valid or not by the rules defined in [MentionCheckerLogic].
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MentionCheckerLogicTest {

    /**
     * [EditText]
     */
    private lateinit var editText: EditText

    /**
     * [MentionCheckerLogic]
     */
    private lateinit var mentionCheckerLogic: MentionCheckerLogic

    /**
     * Use Robolectric to create mock [EditText] view. Instantiate [MentionCheckerLogic].
     */
    @Before
    fun setUp() {
        editText = EditText(RuntimeEnvironment.application)
        mentionCheckerLogic = MentionCheckerLogic(editText)
    }

    /**
     * Set threshold to 3 characters and test whether only string with length less than or equal
     * to 3 are considered valid queries.
     */
    @Test
    fun checkMentionCharacterLimit() {
        mentionCheckerLogic.setMaxCharacters(3)

        // 1 character (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @B")
        val query1 = mentionCheckerLogic.doMentionCheck()
        assertTrue("Query is invalid.", query1 == "B")

        // 2 characters (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Br")
        val query2 = mentionCheckerLogic.doMentionCheck()
        assertTrue("Query is invalid.", query2 == "Br")

        // 3 characters (Pass within char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Bre")
        val query3 = mentionCheckerLogic.doMentionCheck()
        assertTrue("Query is invalid.", query3 == "Bre")

        // 4 characters (Fail out of char limit)
        MentionTestUtils.setTextAndSelection(editText, "Hello @Bren")
        val query4 = mentionCheckerLogic.doMentionCheck()
        assertTrue("Query is invalid.", query4.isEmpty())
    }

    /**
     * Tests whether emails such as "hello@percolate.com" are not considered
     * queries for mentions. If there is text before the @ symbol, it should not be a valid
     * query.
     */
    @Test
    fun checkSearchFailsOnEmail() {
        MentionTestUtils.setTextAndSelection(editText, "hello@percolate.com")
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("An email is being considered as a mention.", query.isEmpty())
    }

    /**
     * If a search begins with non alphanumeric symbols, then it should fail.
     */
    @Test
    fun checkSearchFailsOnSymbols() {
        MentionTestUtils.setTextAndSelection(editText, "@!Brent W")
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("A search beginning with an non alphanumeric character was valid.", query.isEmpty())
    }

    /**
     * If a search begins with @@ then, it should not consider the extra @ symbol as search text.
     */
    @Test
    fun checkSearchFailsOnDoubleAt() {
        MentionTestUtils.setTextAndSelection(editText, "@@")
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("Double @@ symbol was valid.", query.isEmpty())
    }

    /**
     * If [EditText] is set to a blank string, then it should not return a query.
     */
    @Test
    fun checkSearchFailsOnBlankString() {
        MentionTestUtils.setTextAndSelection(editText, "")
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("A blank search returned a query.", query.isEmpty())
    }

    /**
     * If [EditText] is set to null, then it should not return a query.
     */
    @Test
    fun checkSearchFailsOnNull() {
        MentionTestUtils.setTextAndSelection(editText, null)
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("A null search returned a query.", query.isEmpty())
    }

    /**
     * Tests whether a search that begins with an alpha numeric character is valid.
     */
    @Test
    fun checkSearchPassesOnAlphaNumeric() {
        MentionTestUtils.setTextAndSelection(editText, "@Brent Watson")
        val query = mentionCheckerLogic.doMentionCheck()
        assertTrue("Search beginning with alpha numeric character failed.", !query.isEmpty())
    }

}
