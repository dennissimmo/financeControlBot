package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.*;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@Log4j2
public class DeleteOperationQueryHandler implements CallbackQueryHandler{
    private static final String OPERATION = "operation";
    private ReplyMessagesService replyMessagesService;
    private ParseQueryDataService parseQueryDataService;
    private OperationService operationService;
    private UserService userService;
    private BalanceService balanceService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;

    public DeleteOperationQueryHandler(ReplyMessagesService replyMessagesService, ParseQueryDataService parseQueryDataService, OperationService operationService, UserService userService, BalanceService balanceService, ControlMoneyTelegramBot controlMoneyTelegramBot) {
        this.replyMessagesService = replyMessagesService;
        this.parseQueryDataService = parseQueryDataService;
        this.operationService = operationService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        Integer idHandledOperation = Integer.parseInt(parseQueryDataService.getIdOperationFromChooseTypeOperationQuery(callbackQuery));
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Integer callBackMessageId = callbackQuery.getMessage().getMessageId();
        String operationDescription = parseQueryDataService.getOperationDescription(callbackQuery);
        String callBackText = callbackQuery.getData();
        String callBackData = parseQueryDataService.getTypeOperationFromChooseTypeOperationQuery(callbackQuery);
        long chatId = callbackQuery.getMessage().getChatId();

        User currentUser = userService.findUserByChatId(chatId);
        replyMessagesService.setLocaleMessageService(currentUser.getLanguage_code());
        Operation operation = operationService.findOperationById(idHandledOperation);
        Balance userBalance = currentUser.getBalance();
        if (callBackData.equals(OPERATION)) {
            if (Operation.isExpense(operation)) {
                userBalance.upBalance(operation.getAmount());
            } else {
                userBalance.downBalance(operation.getAmount());
            }
            userService.saveUser(currentUser);
            balanceService.saveBalance(userBalance);
            controlMoneyTelegramBot.editMessage(chatId,messageId,replyMessagesService.getReplyText("reply.operation.delete",String.format("<b>%s %s</b>",operationDescription,operation.getCategory().getName()), Emojis.WARNING));
            operationService.deleteOperation(operation);
        }
       return new SendMessage();
    }

    @Override
    public List<String> getHandlerQueryType() {
        return List.of(OPERATION);
    }
}
