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
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(),handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (currentState == BotState.START_STATE) {
            return messageHandlers.get(BotState.START_STATE);
        }
        if (isBalanceState(currentState)) {
            return messageHandlers.get(BotState.SET_BALANCE);
        }
       /* if (isTrainSearchState(currentState)) {
            return messageHandlers.get(BotState.TRAINS_SEARCH);
        }

        if (isStationSearchState(currentState)) {
            return messageHandlers.get(BotState.STATIONS_SEARCH);
        }*/

        return messageHandlers.get(currentState);
    }
    private boolean isBalanceState (BotState state) {
        switch (state) {
            case SET_BALANCE:
            case ASK_BALANCE:
            case CONFIRM_BALANCE_SET:
                return true;
            default:
                return false;
        }
    }
}
