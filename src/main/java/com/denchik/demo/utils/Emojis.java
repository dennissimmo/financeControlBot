package com.denchik.demo.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
public enum Emojis {
    INCOME(EmojiParser.parseToUnicode(":chart_with_upwards_trend:")),
    EXPENSE(EmojiParser.parseToUnicode(":chart_with_downwards_trend:")),
    WARNING(EmojiParser.parseToUnicode(":exclamation:")),
    ORANGE_WARNING(EmojiParser.parseToUnicode(":warning:")),
    CLIP(EmojiParser.parseToUnicode(":paperclip:")),
    INFO(EmojiParser.parseToUnicode(":information_source:")),
    MONEYBAG(EmojiParser.parseToUnicode(":moneybag:")),
    CANCEL(EmojiParser.parseToUnicode(":no_entry:")),
    NOTEPAD(EmojiParser.parseToUnicode(":spiral_note_pad:")),
    EMPTYCANCEL(EmojiParser.parseToUnicode(":no_entry_sign:")),
    SCROLL(EmojiParser.parseToUnicode(":scroll:")),
    PUSHPIN(EmojiParser.parseToUnicode(":pushpin:")),
    PENCIL(EmojiParser.parseToUnicode(":pencil2:")),
    GRITING(EmojiParser.parseToUnicode(":raised_hand:")),
    RECORD(EmojiParser.parseToUnicode(":page_facing_up:")),
    DOLLAR(EmojiParser.parseToUnicode(":dollar:")),
    SMILE(EmojiParser.parseToUnicode(":smiley:")),
    PUNCH(EmojiParser.parseToUnicode(":punch:")),
    POINT_UP(EmojiParser.parseToUnicode(":point_up_2:")),
    POINT_DOWN(EmojiParser.parseToUnicode(":point_down:")),
    POINT_RIGHT(EmojiParser.parseToUnicode(":point_right:")),
    BANK(EmojiParser.parseToUnicode(":bank:")),
    BACK(EmojiParser.parseToUnicode(":back:")),
    HEART(EmojiParser.parseToUnicode(":heart:")),
    ENGLISH(EmojiParser.parseToUnicode(":gb:")),
    UKRAINE(EmojiParser.parseToUnicode(":ua:")),
    RUSSIA(EmojiParser.parseToUnicode(":ru:")),
    WASTEBUSKET(EmojiParser.parseToUnicode(":wastebasket:")),
    CHECK(EmojiParser.parseToUnicode(":white_check_mark:")),
    WRITINGHANDLE(EmojiParser.parseToUnicode(":writing_hand:")),
    EURO(EmojiParser.parseToUnicode(":euro:"));
    private String emojiName;
    @Override
    public String toString () {
        return emojiName;
    }
}
