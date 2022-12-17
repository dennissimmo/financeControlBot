package com.denchik.demo.bot;

import com.denchik.demo.bot.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    /**
     * Return message handler by bot state
     * @param currentState
     * @param message
     * @return
     */
    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isStartState(currentState)) {
            return messageHandlers.get(BotState.START_STATE);
        }
        if (isBalanceState(currentState)) {
            return messageHandlers.get(BotState.SET_BALANCE);
        }
        if (isOperationAdd(currentState)) {
            return messageHandlers.get(BotState.WAIT_OPERATION);
        }
        return messageHandlers.get(currentState);
    }
    private boolean isOperationAdd(BotState state) {
        switch (state) {
            case WAIT_OPERATION:
            case SET_AMOUNT_OPERATION:
                return true;
            default:
                return false;
        }
    }
    private boolean isBalanceState(BotState state) {
        switch (state) {
            case SET_BALANCE:
            case ASK_BALANCE:
            case CONFIRM_BALANCE_SET:
                return true;
            default:
                return false;
        }
    }
    private boolean isStartState(BotState state) {
        switch (state) {
            case START_STATE:
            case LANGUAGE_CHOOSE:
            case ASK_LOCALE:
                return true;
            default:
                return false;
        }
    }
}
