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

@Component
@Log4j2
public class DeleteCommandHandler implements InputMessageHandler{
    private UserService userService;
    private ReplyMessagesService replyMessagesService;

    public DeleteCommandHandler(UserService userService, ReplyMessagesService replyMessagesService) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChatId(message.getChatId());
        replyMessagesService.setLocaleMessageService(user.getLanguage_code());
        if (user != null) {
            return getConfirmationDeleteKeyboard(message);
        }
        return replyMessagesService.getReplyMessage(message.getChatId(),"reply.user.new");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.DELETE_CONNECTION;
    }

    public SendMessage getConfirmationDeleteKeyboard (Message message) {
        SendMessage replyMessage = new SendMessage().setText(replyMessagesService.getReplyText("reply.useRelation.confirmation.delete", Emojis.WARNING));
        replyMessage.setChatId(message.getChatId());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton().setText(replyMessagesService.getReplyText("button.confirmation.yes"));
        InlineKeyboardButton noButton = new InlineKeyboardButton().setText(replyMessagesService.getReplyText("button.confirmation.no"));
        yesButton.setCallbackData("Yes");
        noButton.setCallbackData("No");
        button1.add(yesButton);
        button1.add(noButton);
        buttons.add(button1);
        markup.setKeyboard(buttons);
        replyMessage.setReplyMarkup(markup);
        return replyMessage;
    }
}
