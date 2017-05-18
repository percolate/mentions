package com.percolate.mentions

import android.text.style.ForegroundColorSpan
import android.widget.EditText

/**
 * Utility methods commonly used throughout the Unit and UI tests.
 */
internal object MentionTestUtils {

    /**
     * Creates a mention object with an offset and mention name. The lenght of the mention is
     * length of the mention name.
     *
     * @param offset        int     The starting location of the mention.
     * @param mentionName   String  The name of the mention to display in the [EditText] view.
     *
     * @return Mentionable          A mock mention.
     */
    fun createMockMention(offset: Int, mentionName: String): Mentionable {
        return Mention().apply {
            this.mentionOffset = offset
            this.mentionName = mentionName
            this.mentionLength = mentionName.length
        }
    }

    /**
     * Set text and selection in [EditText] view.
     * @param text  String   The text to insert into the [EditText] view.
     */
    fun setTextAndSelection(editText: EditText, text: String?) {
        editText.setText(text)
        editText.setSelection(text?.length ?: 0)
    }

    /**
     * Get an array of ForegroundColorSpans from a starting and ending location.
     *
     * @param editText  EditText    The edit text view containing text with ForegroundColorSpan.
     * @param start     int         The starting location to search for ForegroundColorSpans.
     * @param end       int         The ending location to search for ForegroundColorSpans.
     *
     * @return ForegroundColorSpan[] An array of ForegroundColorSpan.
     */
    fun getForegroundColorSpans(editText: EditText, start: Int, end: Int): Array<ForegroundColorSpan> {
        return editText.text.getSpans(start, start + end, ForegroundColorSpan::class.java)
    }

}
