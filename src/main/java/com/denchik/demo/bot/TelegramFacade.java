package com.denchik.demo.bot;

import com.denchik.demo.bot.handlers.callbackquery.CallbackQueryFacade;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.denchik.demo.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Log4j2
@Component
public class TelegramFacade {
    private UserService userService;
    private BotStateContext botStateContext;
    private ReplyMessagesService replyMessagesService;
    private CallbackQueryFacade callbackQueryFacade;
    public TelegramFacade(UserService userService,ReplyMessagesService replyMessagesService,BotStateContext botStateContext, CallbackQueryFacade callbackQueryFacade) {
        this.userService = userService;
        this.replyMessagesService =replyMessagesService;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callbackQueryFacade;
        System.out.println("Внедряем User Service в Telegram Facade");
    }
    /**
     * Метод приймає апдейт від користувача, обробляє повідомлення яке міститься в апдейті, визначає
     * чи містить update callbackquery від кнопок, передає апдейт іншим обробникам по коду, та підготовує
     * повідомлення, яке користувач отримує в якості відповіді
     * @param update
     * @return
     */
    public SendMessage handleUpdate (Update update) {
        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            Long chat_id = update.getMessage().getChatId();
            String textMessage = message.getText();
            log.info("New message from User: {} , chat_id {} with text {}",message.getFrom(),message.getChatId(),message.getText());
            replyMessage =
                    handleInputMessageText(message);
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info(
                    "CallbackQuery from User: {}; " +
                            "data: {}; " +
                            "callbackQuery id: {}",
                    callbackQuery.getFrom().getUserName(),
                    callbackQuery.getData(),
                    callbackQuery.getId()
            );
            replyMessage = callbackQueryFacade.processCallbackQuery(callbackQuery);
            //replyMessage = processCallbackQuery(callbackQuery,update);
        } else {
            log.error("Incorrect update from Telegram, can't handle update");
        }
        return replyMessage;
    }

    /**
     * Метод обробляє текст повідомлення, визначає його приналежність до конкретної команди, визначає поточний стан бота
     * у конкретного користувача,перемикає поточний стан роботи бота, повертає відповідь для користувача в залежності
     * від поточного стану бота, та передає ці дані методу processInputMessage () класу BotStateContext
     *
     * @param message
     * @return
     */
    public SendMessage handleInputMessageText (Message message) {
        BotState botState;
        User user;
        SendMessage reply = null;
        String textMessage = message.getText();
        /*if (isOperationAmount(textMessage)) {
            botState = BotState.CHOOSE_OPERATION_TYPE;
            //reply = getChooseOperationReplyInlineKeyboard(message);
            *//*Double amountOperation = Double.parseDouble(textMessage);
            User currentUser = userService.findUserByChat_id(chat_id);
            operation = new Operation();
            operation.setAmount(amountOperation);
            log.info("Set amount operation to {0}", operation.getAmount());

            log.info("New message from User: {} , chat_id {} with text {}", message.getFrom(), message.getChatId(), message.getText());
            return getChooseOperationReplyInlineKeyboard(update);*//*
            // return new SendMessage(chat_id,"Два повідомлення в одному і тому ж блоці перевірки update");
        } else {*/
            switch (textMessage) {
                case "/start":
                    botState = BotState.START_STATE;
                    break;
                case "/list" :
                    botState = BotState.LIST_OPERATION;
                    break;
                case "/balance" :
                    botState = BotState.GET_BALANCE;
                    break;
                case "/report" :
                    botState = BotState.REPORT;
                    break;
                case "/setbal" :
                    botState = BotState.SET_BALANCE;
                    break;
                case "/delete":
                    botState = BotState.DELETE_CONNECTION;
                    //reply = getConfirmationDeleteKeyboard(message);
                    break;
                case "/lang" :
                    botState = BotState.LANGUAGE_CHOOSE;
                    break;
                default:
                    user = userService.findUserByChat_id(message.getChatId());
                    if (user.getState_id() != null) {
                        botState = BotState.getBotStateById(user.getState_id());
                    } else {
                        botState = BotState.NONE;
                    }
                    break;
            }
        try {
            user = userService.findUserByChat_id(message.getChatId());
            if (user != null) {
                user.setState_id(botState);
                log.info("User before return reply message : {} ", user.toString());
                userService.saveUser(user);
            }
            reply = botStateContext.processInputMessage(botState,message);
        } catch (Exception e) {
            reply = new SendMessage(message.getChatId(),"Can`t handle update on state : " + botState);
            log.info("Can't handle state : {}",botState);
            e.printStackTrace();
        }
        return reply;
    }

    public SendMessage getConfirmationDeleteKeyboard (Message message) {
        SendMessage replyMessage = new SendMessage().setText(replyMessagesService.getReplyText("reply.useRelation.confirmation.delete",Emojis.WARNING));
        replyMessage.setChatId(message.getChatId());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton().setText("Так");
        InlineKeyboardButton noButton = new InlineKeyboardButton().setText("Ні");
        yesButton.setCallbackData("Yes");
        noButton.setCallbackData("No");
        button1.add(yesButton);
        button1.add(noButton);
        buttons.add(button1);
        markup.setKeyboard(buttons);
        replyMessage.setReplyMarkup(markup);
        return replyMessage;
    }
        private boolean isOperationAmount (String messageText) {
            String regex = "\\d+";
            Pattern digits = Pattern.compile(regex);
            boolean isOperationText = digits.matcher(messageText).matches();
            return isOperationText;
        }
        public SendMessage getChooseOperationReplyInlineKeyboard (Message message) {
        Long chat_id = message.getChatId();
        SendMessage sendMessage = replyMessagesService.getReplyMessage(chat_id,"reply.category.chooseTypeOperation",Emojis.RECORD);
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

}
