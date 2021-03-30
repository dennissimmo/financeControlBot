package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Log4j2
@Component
public class StartCommandHandler implements InputMessageHandler{

    private final UserService userService;
    private final ReplyMessagesService replyMessagesService;
    public StartCommandHandler(UserService userService,ReplyMessagesService replyMessagesService) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage reply = null;
        Long chat_id = message.getChatId();
        org.telegram.telegrambots.meta.api.objects.User telegram = message.getFrom();
        User currentUser = userService.findUserByChat_id(message.getChatId());
        if (currentUser != null) {
            Balance userBalance = currentUser.getBalance();
            if (userBalance != null) {
                currentUser.setState_id(BotState.NONE);
                reply = replyMessagesService.getReplyMessage(message.getChatId(),"reply.command.start.authorized.instruction",currentUser.getFirst_name());
                userService.saveUser(currentUser);
            } else {
                reply =  replyMessagesService.getReplyMessage(message.getChatId(),"reply.command.start.authorized.setBalance",currentUser.getFirst_name());
                currentUser.setState_id(BotState.NONE);
                userService.saveUser(currentUser);
            }
        } else {
            currentUser = new User(chat_id,telegram.getFirstName(),telegram.getLastName(),telegram.getUserName(),telegram.getLanguageCode(),0);
            userService.saveUser(currentUser);
            log.info("Add new user: username : {} chat_id : {} firstname : {}",telegram.getUserName(),message.getChatId(),telegram.getFirstName());
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.start.setBalance", Emojis.MONEYBAG);
        }
        return reply;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.START_STATE;
    }
}
