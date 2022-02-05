package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {
    SendMessage handle(Message message);
    BotState getHandlerName();
}
