// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

class TextChunkImpl implements TextChunk {
    private final String keyword;
    private final String text;
    private final String language;
    private final String translated;
    private final int type;

    public TextChunkImpl(String string0, String string1, String string2, String string3, int int0) {
        this.keyword = string0;
        this.text = string1;
        this.language = string2;
        this.translated = string3;
        this.type = int0;
    }

    @Override
    public String getKeyword() {
        return this.keyword;
    }

    @Override
    public String getTranslatedKeyword() {
        return this.translated;
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getType() {
        return this.type;
    }
}
