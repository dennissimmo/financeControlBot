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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class StartCommandHandler implements InputMessageHandler{

    private final UserService userService;
    private final ReplyMessagesService replyMessagesService;
    public StartCommandHandler(UserService userService,ReplyMessagesService replyMessagesService) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
    }

    /*@Override
    public SendMessage handle(Message message) {
        SendMessage reply = null;
        Long chat_id = message.getChatId();
        org.telegram.telegrambots.meta.api.objects.User telegram = message.getFrom();
        User currentUser = userService.findUserByChat_id(message.getChatId());
        if (currentUser != null) {
            Balance userBalance = currentUser.getBalance();
            if (userBalance != null) {
                currentUser.setState_id(BotState.WAIT_OPERATION);
                reply = replyMessagesService.getReplyMessage(message.getChatId(),"reply.command.start.authorized.instruction",currentUser.getFirst_name());
                userService.saveUser(currentUser);
            } else {
                reply = replyMessagesService.getReplyMessage(message.getChatId(),"reply.command.start.authorized.setBalance",currentUser.getFirst_name());
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
    }*/
    @Override
    public SendMessage handle(Message message) {
        SendMessage reply = null;
        long chat_id = message.getChatId();
        org.telegram.telegrambots.meta.api.objects.User telegram = message.getFrom();
        User user = userService.findUserByChat_id(chat_id);
        if (user == null) {
            user = new User(chat_id, telegram.getFirstName(), telegram.getLastName(),"ua-UA", telegram.getUserName(), 0);
            user.setState_id(BotState.NONE);
            userService.saveUser(user);
            log.info("Add new user: username : {} chat_id : {} firstname : {}",telegram.getUserName(),message.getChatId(),telegram.getFirstName());
            reply = new SendMessage()
                    .setText(replyMessagesService.getReplyText("reply.language.ask", Emojis.RECORD))
                    .setChatId(chat_id)
                    .setReplyMarkup(getChooseLanguageReplyInlineKeyboard());
        } else {
            user.setState_id(BotState.WAIT_OPERATION);
            userService.saveUser(user);
            replyMessagesService.setLocaleMessageService(user.getLanguage_code());
            reply = new SendMessage()
                    .setText(replyMessagesService.getReplyText("reply.language.ask", Emojis.RECORD))
                    .setChatId(chat_id)
                    .setReplyMarkup(getChooseLanguageReplyInlineKeyboard());
        }
        return reply;
    }
    public InlineKeyboardMarkup getChooseLanguageReplyInlineKeyboard () {
        InlineKeyboardMarkup chooseLanguageMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton english = new InlineKeyboardButton(replyMessagesService.getReplyText("button.language.english",Emojis.ENGLISH)).setCallbackData("language|en-UK");
        InlineKeyboardButton ukrainian = new InlineKeyboardButton(replyMessagesService.getReplyText("button.language.ukrainian",Emojis.UKRAINE)).setCallbackData("language|ua-UA");
        InlineKeyboardButton russian = new InlineKeyboardButton(replyMessagesService.getReplyText("button.language.russian",Emojis.RUSSIA)).setCallbackData("language|ru-RU");
        //InlineKeyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData(String.format("%s|%d","Cancel",idOperation));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row1.add(english);
        row2.add(ukrainian);
        row3.add(russian);
        //row2.add(cancel);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);
        chooseLanguageMarkup.setKeyboard(buttons);
        return  chooseLanguageMarkup;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.START_STATE;
    }
}
