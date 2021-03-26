package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Category;
import com.denchik.demo.service.CategoryService;
import com.denchik.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.StopMessageLiveLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@Log4j2
public class MessageHandler {
    private final ControlMoneyTelegramBot botContext;
    private final CategoryService categoryService;
    public MessageHandler(ControlMoneyTelegramBot botContext, CategoryService categoryService) {
        this.botContext = botContext;
        this.categoryService = categoryService;
        Category products = categoryService.findByCategoryName("Транспорт");
        System.out.println(" внедряем Message Hendler");
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chat_id = update.getMessage().getChatId();
            String textMessage = update.getMessage().getText();

            switch (textMessage) {
                case "/start" :
                    Message message = update.getMessage();
                        log.info("New message from User: {} , chat_id {} with text {}",message.getFrom(),message.getChatId(),message.getText());
                    break;
                    case "hello" :
                    return new SendMessage(chat_id.toString(),"Inside messageHandler");
                case "/operation" :
                    Category category = categoryService.findByCategoryName("Спорт");
                    System.out.println(category.toString());
                    return new SendMessage(chat_id.toString(),category.toString());
                case "/cat" :
                    List<Category> categoryList = categoryService.findAllCategories();
                    for (Category cat : categoryList) {
                        System.out.println(cat.toString());
                    }
                    break;
                case "/incomes" :
                    List<Category> incomes = categoryService.getIncomes();
                    return getListCategories(update,incomes);
                case "/expenses" :
                    List<Category> expenses = categoryService.getExpenses();
                    return getListCategories(update,expenses);
                case "/list" :
                    List<Category> allCategories = categoryService.findAllCategories();
                    return getListCategories(update,allCategories);
                default:
                    return new SendMessage().setText("Not a command").setChatId(chat_id);
            }
        }
        return null;
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
        String chat_ids = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setText("Виберіть категорію :");
        message.setChatId(chat_ids);
        message.setReplyMarkup(markup);
        return message;
    }
}
