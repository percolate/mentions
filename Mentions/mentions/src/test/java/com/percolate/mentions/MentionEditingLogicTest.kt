package com.percolate.mentions

import android.annotation.SuppressLint
import android.widget.EditText
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Tests for editing and highlighting a mention.
 */
@SuppressLint("SetTextI18n")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MentionEditingLogicTest {

    /**
     * Test replacement text.
     */
    private val TEST_STRING = "Test"

    /**
     * [EditText].
     */
    private lateinit var editText: EditText

    /**
     * [Mentionable].
     */
    private lateinit var mention: Mentionable

    /**
     * [MentionInsertionLogic].
     */
    private lateinit var mentionInsertionLogic: MentionInsertionLogic

    /**
     * Create mention "Brent Watson" using Mockito and instantiate [MentionInsertionLogic]
     * with a null parameter for the [EditText] view, because it is not needed for testing
     * the [MentionInsertionLogic.updateInternalMentionsArray] method.
     */
    @Before
    fun setUp() {
        editText = EditText(RuntimeEnvironment.application)
        mentionInsertionLogic = MentionInsertionLogic(editText)

        // Create mention with name "Brent Watson" and offset 6 due to the string "Hello " before it.
        mention = MentionTestUtils.createMockMention("Hello ".length, "Brent Watson")
        editText.setText("Hello Brent Watson")
        mentionInsertionLogic.addMentionToInternalArray(mention, mention.mentionOffset)
    }

    /**
     * "Hello Brent Watson" -> "Hello Brent WatsoTestn".

     * Edit within the mention "Brent Watson" and test whether the mention was removed.
     */
    @Test
    fun testMentionRemovalByEditing() {
        editText.setText("Hello Brent WatsoTestn")
        mentionInsertionLogic.updateInternalMentionsArray(mention.mentionLength - 1, 0, TEST_STRING.length)
        assertTrue("Did not remove mention", mentionInsertionLogic.mentions.size == 0)
    }

    /**
     * "Hello Brent Watson" -> "".

     * Select all the text and remove it and test whether the mention was removed.
     */
    @Test
    fun testMentionRemovalByDeletingAllText() {
        editText.setText("")
        mentionInsertionLogic.updateInternalMentionsArray(0, "Hello Brent Watson".length, 0)
        assertTrue("Did not remove mention.", mentionInsertionLogic.mentions.size == 0)
    }

    /**
     * "Hello Brent Watson" -> "Hatson".

     * Perform selection "ello Brent W" and remove text. Test if the mention was removed.
     */
    @Test
    fun testMentionRemovalByPartialDeletion() {
        editText.setText("Hatson")
        mentionInsertionLogic.updateInternalMentionsArray(1, "ello Brent W".length, 0)
        assertTrue("Did not remove mention.", mentionInsertionLogic.mentions.size == 0)
    }

    /**
     * "Hello Brent Watson" -> "HTestatson".

     * Perform selection "ello Brent W" and replace with text `TEST_STRING`.
     * Test if the mention was removed.
     */
    @Test
    fun testMentionRemovalByPartialReplacement() {
        editText.setText("HTestatson")
        mentionInsertionLogic.updateInternalMentionsArray(1, "ello Brent W".length, TEST_STRING.length)
        assertTrue("Did not remove mention.", mentionInsertionLogic.mentions.size == 0)
    }

    /**
     * "Hello Brent Watson" -> "Hello TestBrent Watson".

     * Add `TEST_STRING` before mention "Brent Watson" and test whether the mentions'
     * offset was updated.
     */
    @Test
    fun testMentionOffsetUpdate() {
        editText.setText("Hello TestBrent Watson")
        mentionInsertionLogic.updateInternalMentionsArray("Hello ".length, 0, TEST_STRING.length)

        val mentions = mentionInsertionLogic.mentions
        assertNotNull(mentions)
        assertTrue("Mention should not have been removed.", mentions.size == 1)
        assertTrue("Mention does not have correct offset.",
                mention.mentionOffset == "Hello ".length + TEST_STRING.length)
    }
}
