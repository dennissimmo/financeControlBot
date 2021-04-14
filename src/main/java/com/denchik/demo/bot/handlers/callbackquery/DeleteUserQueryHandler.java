package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.User;
import com.denchik.demo.service.BalanceService;
import com.denchik.demo.service.OperationService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
@Component
@Log4j2
public class DeleteUserQueryHandler implements CallbackQueryHandler {
    private static final String YES = "Yes";
    private static final String NO = "No";
    private UserService userService;
    private OperationService operationService;
    private BalanceService balanceService;
    private ReplyMessagesService replyMessagesService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;

    public DeleteUserQueryHandler(UserService userService, OperationService operationService, BalanceService balanceService, ReplyMessagesService replyMessagesService, ControlMoneyTelegramBot controlMoneyTelegramBot) {
        this.userService = userService;
        this.operationService = operationService;
        this.balanceService = balanceService;
        this.replyMessagesService = replyMessagesService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Integer callBackMessageId = callbackQuery.getMessage().getMessageId();
        String callBackText = callbackQuery.getData();
        long chat_id = callbackQuery.getMessage().getChatId();
        User currentUser = userService.findUserByChat_id(chat_id);

        if (currentUser != null) {
            String localeUser = currentUser.getLanguage_code();
            if (localeUser != null) {
                replyMessagesService.setLocaleMessageService(localeUser);
            }
        }

        if (callBackText.equals(YES)) {
            operationService.deleteAllOperationByUserID(currentUser.getId());
            if (currentUser.getBalance() != null) {
                balanceService.deleteBalanceById(currentUser.getBalance().getId());
            }
            log.info("Delete user : {}", currentUser.toString());
            userService.deleteUserByChat_id(chat_id);
            controlMoneyTelegramBot.editMessage(chat_id,messageId,replyMessagesService.getReplyText("reply.delete.user"));
        }
        if (callBackText.equals(NO)) {
            // TODO: Add message if user not confirm deleting
            currentUser.setState_id(BotState.WAIT_OPERATION);
            userService.saveUser(currentUser);
            controlMoneyTelegramBot.editMessage(chat_id,messageId,replyMessagesService.getReplyText("reply.delete.cancel",Emojis.CHECK));
        }
        return new SendMessage();
    }

    @Override
    public List<String> getHandlerQueryType() {
        return List.of(YES,NO);
    }
}
