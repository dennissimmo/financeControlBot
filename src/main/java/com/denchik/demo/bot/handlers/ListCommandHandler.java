package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.TypeCategory;
import com.denchik.demo.model.TypeOperation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.*;
import com.denchik.demo.utils.Emojis;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.TypeCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class ListCommandHandler implements InputMessageHandler{
    private ReplyMessagesService replyMessagesService;
    private UserService userService;
    private OperationService operationService;
    private TypeOperationService typeOperationService;

    public ListCommandHandler(ReplyMessagesService replyMessagesService, UserService userService, OperationService operationService,TypeOperationService typeOperationService) {
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
        this.operationService = operationService;
        this.typeOperationService = typeOperationService;
    }
    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChat_id(message.getChatId());
        if (user.getState_id().equals(BotState.LIST_OPERATION.ordinal()))
        {
            user.setState_id(BotState.WAIT_OPERATION); // Choose operation for delete
            System.out.println("User state inside addOperation handle function " + user.getState_id());
            userService.saveUser(user);
        }
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LIST_OPERATION;
    }

    private SendMessage processUserInput (Message message) {
        String userInput = message.getText();
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChat_id(chat_id);
        List<Operation> userOperations = operationService.findOperationsByUser(currentUser);
        TypeOperation income = typeOperationService.getTypeByName("Income");
        List<Operation> incomeOperations = operationService.findAllOperationByTypeCategory(income,currentUser);
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
        SendMessage reply = null;
        if (userOperations.isEmpty()) {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.empty.list",Emojis.SCROLL);
        } else {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.list",Emojis.SCROLL);
            reply.setReplyMarkup(getListOperations(incomeOperations));
        }
        List<Operation> monthNumbers = operationService.getOperationPerNumberMonth(4);
        monthNumbers.forEach(month -> System.out.println(month.toString()));
       /* List<Operation> groupedOperations = operationService.getSumOperationByCategoryPerMonth(4);
        log.info("Size of unique categories group {}",groupedOperations.size());*/
        /*List<Operation> dayOperations = operationService.getOperationByDay();
        dayOperations.forEach(operation -> System.out.println(operation.getCreateAt()));*/
                userService.saveUser(currentUser);
        return reply;
    }
    private InlineKeyboardMarkup getListOperations (List<Operation> operations) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        /*List<Category> categoryList = categoryService.findAllCategories();*/
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < operations.size(); i++) {
            Operation currentOperation = operations.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton().setText(String.format("%.2f %s %s",currentOperation.getAmount(),currentOperation.getCategory().getName(),currentOperation.getCreateAt())).setCallbackData(String.format("operation|%d|%s",currentOperation.getId(),currentOperation.getCategory().getName()));
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            buttons.add(listButton);
        }
        markup.setKeyboard(buttons);
        // String chat_ids = update.getMessage().getChatId().toString();
        return markup;
    }
}
