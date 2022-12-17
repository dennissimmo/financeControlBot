package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
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

    @Override
    public SendMessage handle(Message message) {
        SendMessage reply = null;
        long chat_id = message.getChatId();
        org.telegram.telegrambots.meta.api.objects.User telegram = message.getFrom();
        User user = userService.findUserByChatId(chat_id);
        if (user == null) {
            user = new User(chat_id, telegram.getFirstName(), telegram.getLastName(), telegram.getUserName(), "ua-UA",0);
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
            if (user.getLanguage_code() != null) {
                replyMessagesService.setLocaleMessageService(user.getLanguage_code());
            }
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
        //InlineKeyboardButton russian = new InlineKeyboardButton(replyMessagesService.getReplyText("button.language.russian",Emojis.RUSSIA)).setCallbackData("language|ru-RU");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row1.add(ukrainian);
        row2.add(english);
        //row3.add(russian);
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
