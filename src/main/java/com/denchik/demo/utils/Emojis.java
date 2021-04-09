package com.denchik.demo.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
public enum Emojis {
    INCOME(EmojiParser.parseToUnicode(":chart_with_upwards_trend:")),
    EXPENSE(EmojiParser.parseToUnicode(":chart_with_downwards_trend:")),
    WARNING(EmojiParser.parseToUnicode(":exclamation:")),
    MONEYBAG(EmojiParser.parseToUnicode(":moneybag:")),
    CANCEL(EmojiParser.parseToUnicode(":no_entry:")),
    EMPTYCANCEL(EmojiParser.parseToUnicode(":no_entry_sign:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:")),
    PUSHPIN(EmojiParser.parseToUnicode(":pushpin:")),
    PENCIL(EmojiParser.parseToUnicode(":pencil2:")),
    RECORD(EmojiParser.parseToUnicode(":page_facing_up:")),
    DOLLAR(EmojiParser.parseToUnicode(":dollar:")),
    BANK(EmojiParser.parseToUnicode(":bank:")),
    BACK(EmojiParser.parseToUnicode(":back:")),
    HEART(EmojiParser.parseToUnicode(":heart:")),
    ENGLISH(EmojiParser.parseToUnicode(":gb:")),
    UKRAINE(EmojiParser.parseToUnicode(":ua:")),
    RUSSIA(EmojiParser.parseToUnicode(":ru:")),
    CHECK(EmojiParser.parseToUnicode(":white_check_mark:")),
    WRITINGHANDLE(EmojiParser.parseToUnicode(":writing_hand:")),
    EURO(EmojiParser.parseToUnicode(":euro:"));
    private String emojiName;
    @Override
    public String toString () {
        return emojiName;
    }
}
