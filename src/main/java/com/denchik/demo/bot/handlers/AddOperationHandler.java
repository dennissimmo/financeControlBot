package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class AddOperationHandler implements InputMessageHandler {
    private UserService userService;
    private ReplyMessagesService replyMessagesService;
    public AddOperationHandler(UserService userService,ReplyMessagesService replyMessagesService) {
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChat_id(message.getChatId());
        if (user.getState_id().equals(BotState.WAIT_OPERATION.ordinal()))
        {
            user.setState_id(BotState.SET_AMOUNT_OPERATION);
            System.out.println("User state inside addOperation handle function " + user.getState_id());
            userService.saveUser(user);
        }

        return processUserInput(message);
    }
    private SendMessage processUserInput (Message message) {
        String userInput = message.getText();
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChat_id(chat_id);
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
        SendMessage reply = null;
        if (botState.equals(BotState.SET_AMOUNT_OPERATION)) {
            if (isCorrectFormatOperation(userInput)) {
                reply = getChooseOperationReplyInlineKeyboard(message);
                currentUser.setState_id(BotState.CHOOSE_OPERATION_TYPE);
                userService.saveUser(currentUser);
            }
        }
        return reply;
    }
    public SendMessage getChooseOperationReplyInlineKeyboard (Message message) {
        Long chat_id = message.getChatId();
        SendMessage sendMessage = replyMessagesService.getReplyMessage(chat_id,"reply.category.chooseTypeOperation", Emojis.RECORD);
        InlineKeyboardMarkup chooseOperationTypeMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton incomes = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.incomes",Emojis.INCOME)).setCallbackData("Income");
        InlineKeyboardButton expenses = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.expenses",Emojis.EXPENSE)).setCallbackData("Expense");
        InlineKeyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData("Cancel");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(incomes);
        row1.add(expenses);
        row2.add(cancel);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row1);
        buttons.add(row2);
        chooseOperationTypeMarkup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(chooseOperationTypeMarkup);
        return sendMessage;
    }
    private boolean isCorrectFormatOperation (String messageText) {
        String regex = "\\d+";
        Pattern digits = Pattern.compile(regex);
        boolean isOperationText = digits.matcher(messageText).matches();
        return isOperationText;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.WAIT_OPERATION;
    }
}
