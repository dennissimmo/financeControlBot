package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.SourceService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddOperationHandler implements InputMessageHandler {
    private UserService userService;
    private ReplyMessagesService replyMessagesService;
    private OperationService operationService;
    private SourceService sourceService;
    public AddOperationHandler(UserService userService,ReplyMessagesService replyMessagesService,OperationService operationService,SourceService sourceService) {
        this.operationService = operationService;
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
        this.sourceService = sourceService;
    }

    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChatId(message.getChatId());
        replyMessagesService.setLocaleMessageService(user.getLanguage_code());
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
        User currentUser = userService.findUserByChatId(chat_id);
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
        SendMessage reply = null;
        if (botState.equals(BotState.SET_AMOUNT_OPERATION)) {
            if (isCorrectFormatOperation(userInput)) {
                Operation operation = new Operation(Double.parseDouble(userInput),userInput,sourceService.getSourceByTypeSource("Готівка"),currentUser);
                currentUser.setState_id(BotState.WAIT_OPERATION);
                operationService.addOperation(operation);
                reply = getChooseOperationReplyInlineKeyboard(message,operation.getId());
                userService.saveUser(currentUser);
            } else if (isOperationWithNote(userInput)) {
                String note = getNoteFromInput(userInput);
                Double amountOperation = getAmountFromInput(userInput);
                Operation operation = new Operation(amountOperation,userInput,note,sourceService.getSourceByTypeSource("Готівка"),currentUser);
                currentUser.setState_id(BotState.WAIT_OPERATION);
                operationService.addOperation(operation);
                reply = getChooseOperationReplyInlineKeyboard(message,operation.getId());
                userService.saveUser(currentUser);
            } else {
                System.out.println("Incorrect format operation");
                reply = replyMessagesService.getReplyMessage(chat_id,"reply.operation.exampleOperation",Emojis.WARNING).enableHtml(true);
                currentUser.setState_id(BotState.WAIT_OPERATION);
                userService.saveUser(currentUser);
            }
        }
        return reply;
    }
    public String getNoteFromInput (String input)
    {
        Pattern pattern = Pattern.compile("([\\d]{1,10}[\\.]{0,1}[\\d]{0,3})[\\s]{0,1}[\\-]{0,1}[\\s]{0,1}([a-zA-ZА-ЯҐЄІЇа-яґєії0-9\\'\\(\\)\\:\\,\\.\\s\\-\\_]+)");
        Matcher matcher = pattern.matcher(input.trim());
        matcher.find();
        String noteOperation = matcher.group(2).trim();

        return noteOperation;
    }
    public Double getAmountFromInput (String input) {
        Pattern pattern = Pattern.compile("([\\d]{1,10}[\\.]{0,1}[\\d]{0,3})[\\s]{0,1}[\\-]{0,1}[\\s]{0,1}([a-zA-ZА-ЯҐЄІЇа-яґєії0-9\\'\\(\\)\\:\\,\\.\\s\\-\\_]+)");
        Matcher matcher = pattern.matcher(input.trim());
        matcher.find();
        System.out.println(matcher.group(0));
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        Double amountOperation = Double.parseDouble(matcher.group(1).trim());
        return amountOperation;
    }
    public SendMessage getChooseOperationReplyInlineKeyboard (Message message, int idOperation) {
        Long chat_id = message.getChatId();
        SendMessage sendMessage = replyMessagesService.getReplyMessage(chat_id,"reply.category.chooseTypeOperation", Emojis.RECORD);
        InlineKeyboardMarkup chooseOperationTypeMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton incomes = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.incomes",Emojis.INCOME)).setCallbackData(String.format("%s|%d","Income",idOperation));
        InlineKeyboardButton expenses = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.expenses",Emojis.EXPENSE)).setCallbackData(String.format("%s|%d","Expense",idOperation));
        byte [] sizeCallBack = expenses.getCallbackData().getBytes(StandardCharsets.UTF_8);
        System.out.println("Lengthh byte" + sizeCallBack.length);
        //InlineKincomes.getCallbackData().encode('utf-8');eyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData(String.format("%s|%d","Cancel",idOperation));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(incomes);
        row1.add(expenses);
        //row2.add(cancel);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row1);
        buttons.add(row2);
        chooseOperationTypeMarkup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(chooseOperationTypeMarkup);
        return sendMessage;
    }
    private boolean isOperationWithNote (String messageText) {
        String regex = "[\\d]{1,7}[\\.]{0,1}[\\d]{0,3}[\\s]{0,1}[\\-]{1}[\\s]{0,1}[a-zA-ZА-ЯҐЄІЇа-яґєії0-9\\'\\(\\)\\:\\,\\.\\s\\-\\_]+";
        Pattern digits = Pattern.compile(regex);
        boolean isOperationText = digits.matcher(messageText).matches();
        return isOperationText;
    }
    private boolean isCorrectFormatOperation (String messageText) {
        String regex = "[\\d]{1,7}[\\.]{0,1}[\\d]{0,3}";
        Pattern digits = Pattern.compile(regex);
        boolean isOperationText = digits.matcher(messageText).matches();
        return isOperationText;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.WAIT_OPERATION;
    }
}
