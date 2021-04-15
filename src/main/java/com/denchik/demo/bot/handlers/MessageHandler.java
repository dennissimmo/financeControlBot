package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.service.CategoryService;
import com.denchik.demo.service.KeyboardService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.StopMessageLiveLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Configuration
@Log4j2
public class MessageHandler {
   /* private final ControlMoneyTelegramBot botContext;*/
    private final UserService userService;
    private final CategoryService categoryService;
    private final ReplyMessagesService replyMessagesService;
    private final KeyboardService keyboardService;
    private Operation operation;
    public MessageHandler(ReplyMessagesService messagesService, UserService userService, CategoryService categoryService,KeyboardService keyboardService) {
        /*this.botContext = botContext;*/
        this.replyMessagesService = messagesService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.keyboardService = keyboardService;
        Category products = categoryService.findByCategoryName("Транспорт");
        System.out.println("Внедряем Message Hendler");
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chat_id = update.getMessage().getChatId();
            String textMessage = update.getMessage().getText();
            Message message = update.getMessage();
            String regex = "\\d+";
            Pattern digits = Pattern.compile(regex);
            boolean isOperationText = digits.matcher(textMessage).matches();
            if (isOperationText == true) {
                Double amountOperation  = Double.parseDouble(textMessage);
                com.denchik.demo.model.User currentUser = userService.findUserByChat_id(chat_id);
                operation = new Operation();
                operation.setAmount(amountOperation);
                log.info("Set amount operation to {}", operation.getAmount());

                log.info("New message from User: {} , chat_id {} with text {}",message.getFrom(),message.getChatId(),message.getText());
               return getChooseOperationReplyInlineKeyboard(update);
               // return new SendMessage(chat_id,"Два повідомлення в одному і тому ж блоці перевірки update");
            } else {
                switch (textMessage) {
                    case "/start" :
                        User telegramUserObj = message.getFrom();
                        log.info("New message from User: {} , chat_id {} with text {}",message.getFrom(),message.getChatId(),message.getText());

                        com.denchik.demo.model.User userFromDB = userService.findUserByChat_id(chat_id);
                        if ( userFromDB != null) {
                            return new SendMessage(chat_id,String.format("Привет %s, твой id в Базе данных Heroku = %d",telegramUserObj.getFirstName(),userFromDB.getId()));
                        } else {
                            userFromDB = new com.denchik.demo.model.User(message.getChatId(),telegramUserObj.getFirstName(),telegramUserObj.getLastName());
                            log.info("Додаємо нового користувача в базу:  chat_id {} username {} ",message.getChatId(),message.getFrom().getUserName());
                            userService.addUser(userFromDB);
                            return new SendMessage(chat_id,"Вітаю, ви користуєтесь нашим ботом вперше\nВиконуємо реєстрацію:");
                        }
                    case "hello" :
                        return new SendMessage(chat_id.toString(),"Inside messageHandler");
                    case "/delete" :
                        log.info("New message from User: {} , chat_id {} with text {}",message.getFrom(),message.getChatId(),message.getText());
                        return getConfirmationDeleteKeyboard(update);
                    case "/on" :
                        return replyMessagesService.getReplyMessage(chat_id,"reply.category.chooseTypeOperation", Emojis.RECORD);
                    case "/operation" :
                        Category category = categoryService.findByCategoryName("Спорт");
                        System.out.println(category.toString());
                        return new SendMessage(chat_id.toString(),category.toString());
                    case "/list" :
                        List<Category> allCategories = categoryService.findAllCategories();
                        return getListCategories(update,allCategories);
                    default:
                        return new SendMessage().setText("Not a command").setChatId(chat_id);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info(
                    "CallbackQuery from: {}; " +
                            "data: {}; " +
                            "message id: {}",
                    callbackQuery.getFrom().getFirstName(),
                    callbackQuery.getData(),
                    callbackQuery.getId()
            );
            return processCallbackQuery(callbackQuery,update);
        }
        return null;
    }
    public SendMessage getChooseOperationReplyInlineKeyboard (Update update) {
        Long chat_id = update.getMessage().getChatId();
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
    public InlineKeyboardMarkup listCateg (Update update) {
        List<Category> categories = categoryService.getIncomes();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        /*List<Category> categoryList = categoryService.findAllCategories();*/
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(categories.get(i).getName()).setCallbackData(categories.get(i).getId().toString());
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            buttons.add(listButton);
        }
        List<InlineKeyboardButton> listBtn = new ArrayList<>();
        listBtn.add(new InlineKeyboardButton().setText("Додати категорію").setCallbackData("Add"));
        buttons.add(listBtn);
        markup.setKeyboard(buttons);
        // String chat_ids = update.getMessage().getChatId().toString();
        return markup;
    }
    public SendMessage getConfirmationDeleteKeyboard (Update update) {
        SendMessage message = new SendMessage().setText(replyMessagesService.getReplyText("reply.useRelation.confirmation.delete",Emojis.WARNING));
        message.setChatId(update.getMessage().getChatId());
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
        message.setReplyMarkup(markup);
        return message;
    }
    public SendMessage getListCategories (Update update,List<Category> categoryList) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        /*List<Category> categoryList = categoryService.findAllCategories();*/
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(categoryList.get(i).getName()).setCallbackData(categoryList.get(i).getId().toString());
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            buttons.add(listButton);
        }
        List<InlineKeyboardButton> listBtn = new ArrayList<>();
        listBtn.add(new InlineKeyboardButton().setText("Додати категорію").setCallbackData("Add"));
        buttons.add(listBtn);
        markup.setKeyboard(buttons);
       // String chat_ids = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setText("Виберіть категорію :");
        message.setChatId("795182716");
        //message.setChatId(chat_ids);
        message.setReplyMarkup(markup);
        return message;
    }
    private BotApiMethod<?> processCallbackQuery (CallbackQuery callbackQuery,Update update) {
        final Long chat_id  = callbackQuery.getMessage().getChatId();
        final int userId = callbackQuery.getFrom().getId();
        BotApiMethod callbackAnswer;
        if (callbackQuery.getData().equals("Yes")) {
            com.denchik.demo.model.User user = userService.findUserByChat_id(chat_id);
            log.info("Видаляємо користувача із бази: id {} chat_id {} username {} ",user.getId(), user.getChat_id() ,user.getUsername());
            userService.deleteUserByChat_id(chat_id);
            callbackAnswer = new SendMessage(chat_id.toString(),"Авторизацію видалено, всі дані про ваші операції було видалено. Для повторної реєстрації, викличте команду /start");
        } else if (callbackQuery.getData().equals("No")){
            callbackAnswer = sendAnswerCallbackQuery("Операція видалення даних була скасована. Продовжуйте використання бота",true,callbackQuery);
        } else if (callbackQuery.getData().equals("Income"))
        {
            log.info("Choose category income for operation : User {} Callback query MessageID {}",userId,callbackQuery.getMessage().getMessageId());
            EditMessageText et = new EditMessageText()
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setMessageId(callbackQuery.getMessage().getMessageId())
                    .setReplyMarkup(listCateg(update))
                    .setText(replyMessagesService.getReplyText("reply.category.chooseCategory.income",Emojis.EURO,operation.getAmount()));
            callbackAnswer = et;
        } else
        {
            callbackAnswer = sendAnswerCallbackQuery("Не вдається розпізнати відповідь від клавіатури",true,callbackQuery);
        }
        return callbackAnswer;
    }
    private AnswerCallbackQuery sendAnswerCallbackQuery (String text, boolean alert, CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
