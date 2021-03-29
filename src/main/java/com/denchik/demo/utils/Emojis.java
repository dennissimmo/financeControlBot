package com.denchik.demo.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
public enum Emojis {
    INCOME(EmojiParser.parseToUnicode(":chart_with_upwards_trend:")),
    EXPENSE(EmojiParser.parseToUnicode(":chart_with_downwards_trend:")),
    WARNING(EmojiParser.parseToUnicode(":exclamation:")),
    CANCEL(EmojiParser.parseToUnicode(":no_entry:")),
    RECORD(EmojiParser.parseToUnicode(":page_facing_up:")),
    EURO(EmojiParser.parseToUnicode(":euro:"));
    private String emojiName;
    @Override
    public String toString () {
        return emojiName;
    }
}
