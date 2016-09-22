package com.percolate.mentions;

/**
 * Used for testing inserting, deletion and highlighting of mentions.
 */
class Mention implements Mentionable {

    private String mentionName;

    private int offset;

    private int length;

    @Override
    public int getMentionOffset() {
        return offset;
    }

    @Override
    public int getMentionLength() {
        return length;
    }

    @Override
    public String getMentionName() {
        return mentionName;
    }

    @Override
    public void setMentionOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void setMentionLength(int length) {
        this.length = length;
    }

    @Override
    public void setMentionName(String mentionName) {
        this.mentionName = mentionName;
    }
}
