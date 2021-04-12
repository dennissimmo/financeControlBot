package com.denchik.demo.bot;

public enum BotState {
    NONE,
    CHOOSE_OPERATION_TYPE,
    SET_BALANCE,
    EDIT_DATE_OPERATION,
    WAIT_OPERATION,
    CHOOSE_SOURCE_TYPE,
    SHOW_HELP_MENU,
    SETUP_CATEGORY,
    ADD_NEW_CATEGORY,
    SHOW_LAST_OPERATIONS,
    EXPORT,
    СHOOSE_SETTING,
    ASK_BALANCE,
    GET_BALANCE,
    LANGUAGE_CHOOSE,
    ASK_LOCALE,
    REPORT,
    SET_AMOUNT_OPERATION,
    CONFIRM_BALANCE_SET,
    START_STATE,
    LIST_OPERATION,
    DELETE_CONNECTION;

    private static BotState[] botStates;
    public static BotState getBotStateById (int id) {
        if (botStates == null) {
            botStates = BotState.values();
        }
        return botStates[id];
    }
}
