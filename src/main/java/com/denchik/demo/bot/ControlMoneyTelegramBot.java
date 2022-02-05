package com.denchik.demo.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@PropertySource("classpath:application.properties")
public class ControlMoneyTelegramBot extends TelegramWebhookBot {
    @Value("${telegrambot.webHookPath}")
    private String webHookPath;
    @Value("${telegrambot.botToken}")
    private String botToken;
    @Value("${telegrambot.userName}")
    private String botUserName;

    public ControlMoneyTelegramBot () {
        System.out.println("Create instance control money telegram bot");
    }
    @Override
    public String getBotPath() {
        return webHookPath;
    }
    @Override
    public String getBotUsername () {
        return botUserName;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived (Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chat_id = update.getMessage().getChatId();
            String textMessage = update.getMessage().getText();

            switch (textMessage) {
                case "/start" :
                    try {
                        execute(new SendMessage(
                                chat_id.toString(),
                                "Hello i can help control your money especially income and expenses"
                        ));
                        System.out.println("I am get a \"/start\" command");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/another" :
                    try {
                        String formattedOutput = String.format("{\n location : %s;\n chat_id: %d;\n first_name_user : %s\n}",update.getMessage().getLocation(),update.getMessage().getChatId(),update.getMessage().getFrom().getFirstName());
                        execute(new SendMessage(chat_id.toString(),formattedOutput));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/operation" :
                    return getTypeOperation(update);
                case "/keyboard" :
                    try {
                        SendMessage reply = new SendMessage();
                        reply.setText("Hi");
                        reply.setReplyMarkup(getMenuBot());
                        reply.setChatId(chat_id.toString());
                        execute(reply);
                    }catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        execute(new SendMessage(chat_id.toString(),"Not a command"));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        return null;
    }
    public void editMessage (Long chat_id, Integer messageID, String textMessage) {
        BotApiMethod editedReply = null;
        EditMessageText et = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(messageID)
                .enableHtml(true)
                .setText(textMessage);
        try {
            execute(et);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void createDocument(Long chatId, String fileName, ByteArrayInputStream byteArray ) {
        InputStream stream = byteArray;
        SendDocument document = new SendDocument();
        document.setChatId(chatId.toString());
        document.setDocument(new InputFile(stream, String.format("%s.xlsx", fileName)));
        try {
            execute(document);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void editMessage (Long chat_id, Integer messageID, String textMessage, InlineKeyboardMarkup inlineKeyboardMarkup) {
        BotApiMethod editedReply = null;
        EditMessageText et = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(messageID)
                .setReplyMarkup(inlineKeyboardMarkup)
                .enableHtml(true)
                .setText(textMessage);
        try {
            execute(et);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private ReplyKeyboardMarkup getMenuBot () {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Balance");
        row1.add("Income");
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(row1);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }
    private SendMessage getTypeOperation (Update update) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Витрати");
        inlineKeyboardButton1.setCallbackData("Expenses");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Надходження");
        inlineKeyboardButton2.setCallbackData("Amounts");

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Відміна");
        inlineKeyboardButton3.setCallbackData("Cancel");
        List<InlineKeyboardButton> inlineKeyboardButtonsList1 = new ArrayList<>();
        inlineKeyboardButtonsList1.add(inlineKeyboardButton1);
        inlineKeyboardButtonsList1.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> inlineKeyboardButtonsList2 = new ArrayList<>();
        inlineKeyboardButtonsList2.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(inlineKeyboardButtonsList1);
        buttons.add(inlineKeyboardButtonsList2);
        markup.setKeyboard(buttons);
        String chat_ids = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setText("Test inlineKeyboard");
        message.setChatId(chat_ids);
        message.setReplyMarkup(markup);
        return message;
    }
    public void executeSendMessage (SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }
}