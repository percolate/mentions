package com.percolate.sample;

import android.text.InputType;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import com.percolate.mentions.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Insert and highlights a {@link Mentionable} in the {@link EditText}. All {@link Mentionable}s
 * are appended to the <code>mentions</code> array. The {@link Mentionable}'s offset and length
 * values are updated as text is edited in the {@link EditText}. The default text highlight color
 * is orange and it is configurable.
 */
class MentionInsertionUtils {

    private EditText editText;

    /*  An internal array that keeps track of all the mentions added to {@link EditText}. */
    private List<Mentionable> mentions;

    /* Text color of the mention in the {@link EditText}. The default color is orange. */
    private int textHighlightColor;

    public MentionInsertionUtils(EditText editText) {
        this.editText = editText;
        this.mentions = new ArrayList<>();
        this.textHighlightColor = R.color.orange;
    }

    /**
     * Returns all the {@link Mentionable}s added to the {@link EditText} by invoking
     * <code>MentionInsertionUtils#insertMention</code>.
     *
     * @return List<Mentionable>    All the {@link Mentionable}s added to edit {@link EditText}.
     */
    public List<Mentionable> getMentions() {
        return mentions;
    }

    /**
     * Append predefined {@link Mentionable}s into the {@link EditText}. The {@link EditText} must
     * contain {@link Mentionable}s that you are adding.
     *
     * @param mentions List<Mentionable>   A list of mentions that you want to pre-populate.
     */
    public void addMentions(List<? extends Mentionable> mentions) {
        if (mentions == null || mentions.isEmpty()) {
            throw new IllegalArgumentException("Appended Mentions cannot be null nor empty.");
        } else if (StringUtils.isBlank(editText.getText()) || !textHasMentions(mentions)) {
            throw new IllegalArgumentException("Appended Mentions must be in the edit text.");
        }

        this.mentions.addAll(mentions);
        highlightMentionsText();
    }

    /**
     * Set text highlight of the {@link Mentionable}'s name.
     *
     * @param textHighlightColor The text color of the mention.
     */
    public void setTextHighlightColor(int textHighlightColor) {
        this.textHighlightColor = textHighlightColor;
    }

    /**
     * Inserts a {@link Mentionable} into an {@link EditText} by inserting the mentions'
     * name, highlighting it and keeping track of it in the array <code>mentions</code>.
     *
     * @param mention Mentionable     A mention to display in {@link EditText}.
     */
    public void insertMention(Mentionable mention) {

        checkMentionable(mention);
        mention.setMentionLength(mention.getMentionName().length());

        int cursorPosition = editText.getSelectionEnd();
        String text = editText.getText().toString();
        String toReplace = text.substring(0, cursorPosition);
        int start = toReplace.lastIndexOf("@");
        int newCursorPosition = start + mention.getMentionName().length() + 1;

        editText.getText().delete(start, cursorPosition);
        editText.getText().insert(start, mention.getMentionName() + " ");

        // Fix bug on LG G3 phone, where EditText messes up when using insert() method.
        int originalInputType = editText.getInputType();
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setInputType(originalInputType);
        editText.setSelection(newCursorPosition);

        addMentionToInternalArray(mention, start);
        highlightMentionsText();
    }

    /**
     * Determine if the user is not inserting a null {@link Mentionable} and the
     * {@link Mentionable}'s name was set.
     * @param mentionable   Mentionable     The {@link Mentionable} being inserted into the
     *                                      {@link EditText}.
     */
    private void checkMentionable(Mentionable mentionable) {
        if (mentionable == null) {
            throw new IllegalArgumentException("A null mentionable cannot be inserted into the " +
                    "EditText view");
        } else if (StringUtils.isBlank(mentionable.getMentionName())) {
            throw new IllegalArgumentException("The mentions name must be set before inserting " +
                    "into the EditText view.");
        }
    }

    /**
     * Appends a new {@link Mentionable} into the <code>mentions</code> array.
     *
     * @param mention Mentionable     A new mention that was inserted into {@link EditText}.
     * @param start   int             The offset of the new mention.
     */
    public void addMentionToInternalArray(Mentionable mention, int start) {
        if (mention != null && mentions != null) {
            mention.setMentionOffset(start);
            mentions.add(mention);
        }
    }

    /**
     * If you call <code>EditText#setText</code> and clear the text by insert an empty string, then
     * all the mentions added to the {@link EditText} should be removed. This method checks this
     * case and remove all the added {@link Mentionable}s from <code>mentions</code>.
     *
     * @param charSequence  CharSequence    The text that will be changed.
     * @param start         int             The initial position in <code>charSequence</code> where
     *                                      the text will be changed.
     * @param count         int             The number of characters that will be changed in
     *                                      <code>charSequence</code>.
     * @param after         int             The length of the new text entered by the user.
     */
    public void checkIfProgrammaticallyClearedEditText(CharSequence charSequence, int start, int
            count, int after) {
        if (StringUtils.isNotBlank(charSequence) && start == 0 && count == charSequence.length()
                && after == 0) {
            mentions.clear();
        }
    }

    /**
     * Need to keep mentions up-to-date
     * Consideration:
     * - If editing within an existing mention, remove it.
     * - If editing before an existing mentions, update the start of them.
     *
     * @param start  int     Initial position of the new text.
     * @param before int     Length of old text.
     * @param count  int     The number of characters in the new text.
     */
    public void updateInternalMentionsArray(int start, int before, int count) {
        if (mentions != null && !mentions.isEmpty()) {
            if (before != count) { // Text not changed if they ==.
                for (Iterator<Mentionable> iterator = mentions.iterator(); iterator.hasNext(); ) {
                    Mentionable mention = iterator.next();
                    int mentionStart = mention.getMentionOffset();
                    int mentionEnd = mentionStart + mention.getMentionLength();

                    RangeUtils<Integer> mentionsRange = RangeUtils.between(mentionStart + 1, mentionEnd - 1);
                    RangeUtils<Integer> editRange = RangeUtils.between(start, start + before);

                    // Editing within mention - remove mention
                    if (editRange.isOverlappedBy(mentionsRange)) {
                        iterator.remove();
                    } else if (start <= mentionStart) {
                        //Editing text before mention - change offset
                        int diff = count - before;
                        mention.setMentionOffset(mentionStart + diff);
                    }
                }
            }
        }
    }

    /**
     * Highlight all the {@link Mentionable}s in the {@link EditText}. A {@link ForegroundColorSpan}
     * is set at the starting and ending locations of the {@link Mentionable}s.
     */
    public void highlightMentionsText() {

        // Clear current highlighting (note: just using clearSpans(); makes EditText fields act
        // strange).
        ForegroundColorSpan[] spans = editText.getEditableText().getSpans(0,
                editText.getText().length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            editText.getEditableText().removeSpan(span);
        }

        if (mentions != null && !mentions.isEmpty()) {

            for (Iterator<Mentionable> iterator = mentions.iterator(); iterator.hasNext(); ) {
                Mentionable mention = iterator.next();
                int start = mention.getMentionOffset();
                int end = start + mention.getMentionLength();
                if (editText.length() >= end && StringUtils.equals(editText.getText()
                        .subSequence(start, end), mention.getMentionName())) {
                    ForegroundColorSpan fbSpan = new ForegroundColorSpan(editText.getContext()
                                                    .getResources().getColor(textHighlightColor));
                    editText.getEditableText().setSpan(fbSpan, start, end,
                                                              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    //Something went wrong.  The expected text that we're trying to highlight does
                    // not match the actual text at that position.
                    Log.w("Mentions", "Mention lost. [" + mention.getMentionName() + "]");
                    iterator.remove();
                }
            }
        }
    }

    /**
     * If you prepopulate an {@link EditText} with {@link Mentionable}s, then we check whether the
     * view has all the {@link Mentionable}s at their offsets and length.
     *
     * @param mentions List<Mentionable>   List of pre-defined mentions in the {@link EditText}.
     * @return true or false
     */
    public boolean textHasMentions(List<? extends Mentionable> mentions) {
        if (editText != null && mentions != null && mentions.size() > 0) {
            for (Mentionable mention : mentions) {
                int mentionStart = mention.getMentionOffset();
                int mentionEnd = mention.getMentionLength();
                if (mentionEnd <= editText.getText().length()) {
                    String displayText = StringUtils.substring(editText.getText().toString(),
                            mentionStart, mentionEnd);
                    if (StringUtils.isBlank(displayText) || !StringUtils.equals(displayText,
                            mention.getMentionName())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


}
