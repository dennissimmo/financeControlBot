package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.Source;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class LanguageCommandHandler implements InputMessageHandler{
    private UserService userService;
    private ReplyMessagesService replyMessagesService;

    public LanguageCommandHandler(UserService userService,ReplyMessagesService replyMessagesService) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }
    private SendMessage processUserInput (Message message) {
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChat_id(chat_id);
        String inputUser = message.getText();
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
        SendMessage reply = null;
        if (botState.equals(BotState.LANGUAGE_CHOOSE)) {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.language.ask");
            currentUser.setState_id(BotState.ASK_LOCALE);
            userService.saveUser(currentUser);
        }
        if (botState.equals(BotState.ASK_LOCALE)) {
            currentUser.setLanguage_code(inputUser);
            currentUser.setState_id(BotState.WAIT_OPERATION);
            userService.saveUser(currentUser);
            User updateLocale = userService.findUserByChat_id(chat_id);
            reply = new SendMessage().setChatId(chat_id).setText(String.format(" Choosen locale: %s", currentUser.getLanguage_code()));
        }
        return reply;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.LANGUAGE_CHOOSE;
    }
}
