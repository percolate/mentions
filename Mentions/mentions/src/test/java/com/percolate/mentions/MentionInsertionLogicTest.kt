package com.percolate.mentions

import android.text.style.ForegroundColorSpan
import android.widget.EditText
import com.percolate.mentions.MentionTestUtils.createMockMention
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*

/**
 * Tests mention insertion and highlighting.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MentionInsertionLogicTest {

    /**
     * [EditText].
     */
    private lateinit var editText: EditText

    /**
     * [MentionInsertionLogic].
     */
    private lateinit var mentionInsertionLogic: MentionInsertionLogic

    /**
     * Create [EditText] view. Use the [EditText] to instantiate
     * [MentionInsertionLogic].
     */
    @Before
    fun setUp() {
        editText = EditText(RuntimeEnvironment.application)
        mentionInsertionLogic = MentionInsertionLogic(editText)
    }

    /**
     * "Hello @Br" --> "Hello Brent Watson"
     *
     * Perform an @ mention by typing @Br and test whether Brent Watson was added to the
     * [EditText] view and whether the mention was highlighted.
     */
    @Test
    fun testMentionInsertion() {
        // perform @ mention.
        MentionTestUtils.setTextAndSelection(editText, "Hello @Br")

        // create mock mention.
        val mention = createMockMention("Hello ".length, "Brent Watson")

        // insert mention into EditText view.
        mentionInsertionLogic.insertMention(mention)

        // Test EditText has full mention name "Brent Watson".
        assertEquals(editText.text.toString(), "Hello Brent Watson ")

        // Test mention is tracked internally MentionInsertionLogic.
        assertNotNull(mentionInsertionLogic.mentions)
        assertTrue("The mention \"Brent Watson\" was not inserted.",
                mentionInsertionLogic.mentions.size == 1)

        // Test "Brent Watson" was highlighted.
        val highlightSpans = editText.editableText.getSpans("Hello ".length,
                editText.text.length, ForegroundColorSpan::class.java)
        assertTrue("Did not highlight mention", highlightSpans.size == 1)
    }

    /**
     * Test whether inserting a null mention throws a [IllegalArgumentException].
     */
    @Test(expected = IllegalArgumentException::class)
    fun testNullMentionInsertion() {
        mentionInsertionLogic.insertMention(null)
    }

    /**
     * Test whether inserting a mention with a blank name throws a [IllegalArgumentException].
     */
    @Test(expected = IllegalArgumentException::class)
    fun testBlankMentionNameInsertion() {
        mentionInsertionLogic.insertMention(createMockMention("Hello ".length, ""))
    }

    /**
     * An [EditText] view may have text pre-populated with mentions. The method will
     * tests whether the pre-existing mentions are highlighted.
     */
    @Test
    fun testPrepopulateMentions() {
        // insert text with mentions "Brent Watson" and "Doug Tabuchi" into EditText.
        MentionTestUtils.setTextAndSelection(editText, "Hello Brent Watson and Doug Tabuchi")

        // create an array of mock mentions.
        val mentions = ArrayList<Mentionable>()
        mentions.add(createMockMention("Hello ".length, "Brent Watson"))
        mentions.add(createMockMention("Hello Brent Watson and ".length,
                "Doug Tabuchi"))

        // add array of pre-existing mentions.
        mentionInsertionLogic.addMentions(mentions)

        // Test whether all the mentions are tracked internally by MentionInsertionLogic.
        assertNotNull(mentionInsertionLogic.mentions)
        assertTrue("Did not add mentions.", mentionInsertionLogic.mentions.size == 2)

        // Test mentions' test is highlighted with ForegroundColorSpan.
        val highlightSpans1 = MentionTestUtils.getForegroundColorSpans(editText,
                "Hello ".length, "Brent Watson".length)
        assertTrue("The mention \"Brent Watson\" was not highlighted.", highlightSpans1.size == 1)

        val highlightSpans2 = MentionTestUtils.getForegroundColorSpans(editText,
                "Hello Brent Watson and ".length, "Doug Tabuchi".length)
        assertTrue("The mention \"Doug Tabuchi\" was not highlighted.", highlightSpans2.size == 1)
    }

}
