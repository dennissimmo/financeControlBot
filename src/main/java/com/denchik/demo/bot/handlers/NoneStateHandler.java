package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.handlers.InputMessageHandler;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
@Component
public class NoneStateHandler implements InputMessageHandler {
    private ReplyMessagesService replyMessagesService;

    public NoneStateHandler(ReplyMessagesService replyMessagesService) {
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        Long chat_id = message.getChatId();
        SendMessage reply = replyMessagesService.getReplyMessage(chat_id,"reply.user.new", Emojis.POINT_RIGHT,Emojis.POINT_RIGHT,Emojis.POINT_RIGHT);
        return reply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.NONE;
    }
}
