package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.User;
import com.denchik.demo.service.Query.ParseQueryDataService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
@Log4j2
@Component
public class LanguageQueryHandler implements CallbackQueryHandler{
    private static final String LANGUAGE = "language";
    private ControlMoneyTelegramBot controlMoneyTelegramBot;
    private ParseQueryDataService parseQueryDataService;
    private ReplyMessagesService replyMessagesService;
    private UserService userService;

    public LanguageQueryHandler(ParseQueryDataService parseQueryDataService, ReplyMessagesService replyMessagesService, UserService userService,ControlMoneyTelegramBot controlMoneyTelegramBot) {
        this.parseQueryDataService = parseQueryDataService;
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        return processCallback(callbackQuery);
    }
    public SendMessage processCallback (CallbackQuery callbackQuery) {
        SendMessage reply = null;
        Integer callBackMessageId = callbackQuery.getMessage().getMessageId();
        String callBackData = callbackQuery.getData();
        String localeTag = parseQueryDataService.getLocaleTagFromChooseLanguageQuery(callbackQuery);
        long chat_id = callbackQuery.getMessage().getChatId();
        User currentUser = userService.findUserByChatId(chat_id);
        if (callBackData.contains(LANGUAGE)) {
            currentUser.setLanguage_code(localeTag);
            replyMessagesService.setLocaleMessageService(currentUser.getLanguage_code());
            log.info("Set language {} For User: {}",localeTag,currentUser.toString());
            log.info("LocaleMessage {} ", currentUser.getLanguage_code());
            controlMoneyTelegramBot.editMessage(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.language.current"));
            userService.saveUser(currentUser);
        }
        if (currentUser != null) {
            Balance userBalance = currentUser.getBalance();
            if (userBalance != null) {
                currentUser.setState_id(BotState.WAIT_OPERATION);
                reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.start.authorized.instruction",String.format("<b>%s</b>",currentUser.getFirst_name()),Emojis.GRITING).enableHtml(true);
                userService.saveUser(currentUser);
            } else {
                reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.start.authorized.setBalance",String.format("<b>%s</b>",currentUser.getFirst_name()),Emojis.GRITING).enableHtml(true);
                currentUser.setState_id(BotState.NONE);
                userService.saveUser(currentUser);
            }
        } else {
            userService.saveUser(currentUser);
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.start.setBalance", Emojis.MONEYBAG);
        }
        return reply;
    }
    @Override
    public List<String> getHandlerQueryType() {
        return List.of(LANGUAGE);
    }
}
