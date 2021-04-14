package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
@Component
public class HelpCommandHandler implements InputMessageHandler{
    private ReplyMessagesService replyMessagesService;
    private UserService userService;

    public HelpCommandHandler(ReplyMessagesService replyMessagesService, UserService userService) {
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        System.out.println("Unlucky");
        Long chat_id = message.getChatId();
        SendMessage reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.help.commands", userService.getCountUsers(),Emojis.PUNCH).enableHtml(true);
        return reply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.HELP;
    }
}
