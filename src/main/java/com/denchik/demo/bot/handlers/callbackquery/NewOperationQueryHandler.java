package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Log4j2
@Component

public class NewOperationQueryHandler implements CallbackQueryHandler{
    private static final String INCOME = "Income";
    private static final String EXPENSE = "Expense";
    private static final String CANCEL = "Cancel";
    private ReplyMessagesService replyMessagesService;
    private UserService userService;

    public NewOperationQueryHandler(ReplyMessagesService replyMessagesService, UserService userService) {
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chat_id = callbackQuery.getMessage().getChatId();
        final int messageId = callbackQuery.getMessage().getMessageId(); // Need for change message with keyboard in future
        log.info("Callback query data {}",callbackQuery.getData());
        return replyMessagesService.getReplyMessage(chat_id,"reply.query.incorrect");
    }

    @Override
    public List<String> getHandlerQueryType() {
        return List.of(INCOME,EXPENSE,CANCEL);
    }
}
