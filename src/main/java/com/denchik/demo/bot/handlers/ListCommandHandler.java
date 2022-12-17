package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.TypeOperation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class ListCommandHandler implements InputMessageHandler{
    private final SimpleDateFormat DAY_MONTH_YEAR = new SimpleDateFormat("dd.MM.yyyy");
    private final String INCOME = "INCOME";
    private final String EXPENSE = "EXPENSE";
    private ReplyMessagesService replyMessagesService;
    private UserService userService;
    private OperationService operationService;

    public ListCommandHandler(ReplyMessagesService replyMessagesService, UserService userService, OperationService operationService) {
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
        this.operationService = operationService;
    }
    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChatId(message.getChatId());
        if (user.getState_id().equals(BotState.LIST_OPERATION.ordinal())) {
            user.setState_id(BotState.WAIT_OPERATION); // Choose operation for delete
            log.info("User state inside list operations handle function: {}", user.getState_id());
            userService.saveUser(user);
        }
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LIST_OPERATION;
    }

    private SendMessage processUserInput(Message message) {
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChatId(chat_id);
        LocalDate date = LocalDate.now();
        int currentMonth = date.getMonthValue();
        int currentYear = date.getYear();
        List<Operation> currentMonthOperations = operationService.getOperationPerNumberMonth(currentMonth, currentUser.getId(), currentYear);
        currentMonthOperations.forEach(operation -> System.out.println(operation.toString()));
        SendMessage reply;
        if (currentMonthOperations.isEmpty()) {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.empty.list",Emojis.SCROLL);
            log.info("User operations are empty");
        } else {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.list",Emojis.SCROLL,Emojis.POINT_DOWN)
                    .setReplyMarkup(buildOperationsListKeyboard(currentMonthOperations));
            log.info("User operations are not empty");
        }
        userService.saveUser(currentUser);
        return reply;
    }
    private InlineKeyboardMarkup buildOperationsListKeyboard(List<Operation> operations) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        operations.forEach((operation -> {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(this.formatOperationText(operation)).setCallbackData(this.getCallbackData(operation));
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            buttons.add(listButton);
        }));
        markup.setKeyboard(buttons);
        return markup;
    }

    private String formatOperationText(Operation operation) {
        String operationText = String.format("%s %s %s %s",
                Emojis.WASTEBUSKET,
                DAY_MONTH_YEAR.format(operation.getCreateAt()),
                this.getOperationAmount(operation),
                operation.getCategory().getName());
        return operationText;
    }

    private String getCallbackData(Operation operation) {
        String operationCallbackData = String.format("operation|%d|%s|%s", operation.getId(), operation.getCategory().getId(), this.getOperationAmount(operation));
        return operationCallbackData;
    }
    public String getOperationAmount(Operation operation) {
        TypeOperation typeOperation = operation.getTypeOperation();
        if (typeOperation.getName().equals(EXPENSE)) {
            return String.format("- %.2f", operation.getAmount());
        }
        if (typeOperation.getName().equals(INCOME)) {
            return String.format("+ %.2f", operation.getAmount());
        }
        return "";
    }
}
