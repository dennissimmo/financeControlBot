package com.denchik.demo.bot;

import com.denchik.demo.bot.handlers.callbackquery.CallbackQueryFacade;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Pattern;

@Log4j2
@Component
public class TelegramFacade {
    private UserService userService;
    private BotStateContext botStateContext;
    private ReplyMessagesService replyMessagesService;
    private CallbackQueryFacade callbackQueryFacade;
    public TelegramFacade(UserService userService, ReplyMessagesService replyMessagesService, BotStateContext botStateContext, CallbackQueryFacade callbackQueryFacade) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callbackQueryFacade;
        System.out.println("Inject User Service in Telegram Facade");
    }
    /**
     * Метод приймає апдейт від користувача, обробляє повідомлення яке міститься в апдейті, визначає
     * чи містить update callbackquery від кнопок, передає апдейт іншим обробникам по коду, та підготовує
     * повідомлення, яке користувач отримує в якості відповіді
     * @param update
     * @return
     */
    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            Long chat_id = update.getMessage().getChatId();
            String textMessage = message.getText();
            log.info("New message from User: {} , chat_id {} with text {}", message.getFrom(), chat_id, textMessage);
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
     * у конкретного користувача, перемикає поточний стан роботи бота, повертає відповідь для користувача в залежності
     * від поточного стану бота, та передає ці дані методу processInputMessage () класу BotStateContext
     *
     * @param message
     * @return
     */
    public SendMessage handleInputMessageText(Message message) {
        User user;
        SendMessage reply;
        BotState botState = this.getUserStateByMessage(message);
        try {
            user = userService.findUserByChatId(message.getChatId());
            if (user != null) {
                user.setState_id(botState);
                log.info("User before return reply message : {} ", user.toString());
                userService.saveUser(user);
            }
            reply = botStateContext.processInputMessage(botState, message);

        } catch (Exception e) {
            reply = new SendMessage(message.getChatId(),"Can`t handle update on state : " + botState);
            log.info("Can't handle state : {}", botState);
            e.printStackTrace();
        }
        return reply;
    }

    private BotState getUserStateByMessage(Message message) {
        BotState botState;
        String textMessage = message.getText();
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
            case "/export" :
                botState = BotState.EXPORT;
                break;
            case "/setbal" :
                botState = BotState.SET_BALANCE;
                break;
            case "/delete":
                botState = BotState.DELETE_CONNECTION;
                break;
            case "/lang" :
                botState = BotState.LANGUAGE_CHOOSE;
                break;
            case "/help" :
                botState = BotState.HELP;
                break;
            default:
                User user = userService.findUserByChatId(message.getChatId());
                if (user == null) {
                    botState = BotState.NONE;
                } else {
                    botState = user.getState_id() != null ? BotState.getBotStateById(user.getState_id()) : BotState.NONE;
                }
                break;
        }

        return botState;
    }
    private boolean isOperationAmount(String messageText) {
        String regex = "\\d+";
        Pattern digits = Pattern.compile(regex);
        boolean isOperationText = digits.matcher(messageText).matches();
        return isOperationText;
    }
}
