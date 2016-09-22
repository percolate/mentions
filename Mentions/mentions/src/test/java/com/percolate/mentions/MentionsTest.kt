package com.percolate.mentions

import android.annotation.SuppressLint
import android.widget.EditText
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MentionsTest {

    @Test
    fun testBuilder() {
        // Create mentions object.
        val editText = EditText(RuntimeEnvironment.application)
        val queryListener = QueryListener { }
        val suggestionsListener = SuggestionsListener { }

        val mentions = Mentions.Builder(RuntimeEnvironment.application, editText)
                .maxCharacters(10)
                .highlightColor(android.R.color.darker_gray)
                .queryListener(queryListener)
                .suggestionsListener(suggestionsListener).build()

        //Verify all params for mentions object
        assertNotNull("Context is null.", mentions.context)
        assertTrue("Max characters is not 10.", mentions.mentionCheckerLogic.maxCharacters == 10)
        assertTrue("Highlight color is not gray.", mentions.mentionInsertionLogic.textHighlightColor == android.R.color.darker_gray)
        assertNotNull("Query listener is null.", mentions.queryListener === queryListener)
        assertNotNull("Suggestion listener is null.", mentions.suggestionsListener === suggestionsListener)
    }

    @Test
    @SuppressLint("SetTextI18n")
    fun testAddMentions() {
        val editText = EditText(RuntimeEnvironment.application)
        editText.setText("Brent Watson")
        val mentionsLib = Mentions.Builder(RuntimeEnvironment.application, editText).build()

        //Add mentions
        val mentions = ArrayList<Mentionable>()
        val mention = Mention()
        mention.mentionName = "Brent Watson"
        mention.mentionOffset = 0
        mention.mentionLength = "Brent Watson".length
        mentions.add(mention)
        mentionsLib.addMentions(mentions)

        //Verify mentions were added.
        val insertedMentions = mentionsLib.insertedMentions
        assertTrue("No inserted mentions.", insertedMentions.size == 1)
        assertEquals(insertedMentions[0].mentionName, "Brent Watson")
        assertEquals(insertedMentions[0].mentionOffset.toLong(), 0)
        assertEquals(insertedMentions[0].mentionLength.toLong(), "Brent Watson".length.toLong())
    }

    @Test
    fun testQueryReceived() {
        //Create MentionsLib object with query and suggestion listener.
        val editText = EditText(RuntimeEnvironment.application)
        val queryListener = Mockito.mock(QueryListener::class.java)
        val suggestionsListener = Mockito.mock(SuggestionsListener::class.java)
        val mentionsLib = Mentions.Builder(RuntimeEnvironment.application, editText)
                .queryListener(queryListener)
                .suggestionsListener(suggestionsListener)
                .build()

        //Test and verify whether onQueryReceived was called.
        mentionsLib.queryReceived("Doug Tabuchi")
        verify(mentionsLib.queryListener).onQueryReceived(anyString())

        //Test and verify whether displaySuggestions was called.
        mentionsLib.queryReceived(null)
        verify(mentionsLib.suggestionsListener).displaySuggestions(anyBoolean())

    }
}
