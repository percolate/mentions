package com.percolate.mentions;

import com.percolate.mentions.Mentionable;

import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Utility methods commonly used throughout the Unit and UI tests.
 */
public class MentionTestUtils {

    /**
     * Creates a mock mention with an offset and mention name. The lenght of the mention is
     * length of the mention name.

     * @param offset        int     The starting location of the mention.
     * @param mentionName   String  The name of the mention to display in the {@link EditText} view.
     * @return Mentionable          A mock mention.
     */
    public static Mentionable createMockMention(int offset, String mentionName) {
        Mentionable mention = mock(Mentionable.class);
        when(mention.getMentionOffset()).thenReturn(offset);
        when(mention.getMentionName()).thenReturn(mentionName);
        when(mention.getMentionLength()).thenReturn(mentionName.length());
        return mention;
    }

    /**
     * Set text and selection in {@link EditText} view.
     * @param text  String   The text to insert into the {@link EditText} view.
     */
    public static void setTextAndSelection(EditText editText, String text) {
        editText.setText(text);
        editText.setSelection(text != null ? text.length() : 0);
    }

    /**
     * Get an array of ForegroundColorSpans from a starting and ending location.
     *
     * @param editText  EditText    The edit text view containing text with ForegroundColorSpan.
     * @param start     int         The starting location to search for ForegroundColorSpans.
     * @param end       int         The ending location to search for ForegroundColorSpans.
     * @return ForegroundColorSpan[] An array of ForegroundColorSpan.
     */
    public static ForegroundColorSpan[] getForegroundColorSpans(EditText editText, int start, int end) {
        ForegroundColorSpan[] highlightSpans = editText.getText().getSpans(start, start + end, ForegroundColorSpan.class);
        return highlightSpans;
    }

}
