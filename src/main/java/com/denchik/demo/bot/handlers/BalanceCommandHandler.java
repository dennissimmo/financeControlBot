package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.Source;
import com.denchik.demo.model.User;
import com.denchik.demo.service.BalanceService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BalanceCommandHandler implements InputMessageHandler{
    private UserService userService;
    private BalanceService balanceService;
    private ReplyMessagesService replyMessagesService;
    private static final double COURSE_DOLLAR = 27.99;

    public BalanceCommandHandler(UserService userService, BalanceService balanceService,ReplyMessagesService replyMessagesService) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {

        return processUserInput(message);
    }
    private SendMessage processUserInput (Message message) {
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChatId(chat_id);
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
        SendMessage reply = null;
        if (botState.equals(BotState.GET_BALANCE)) {
            Balance userBalance = currentUser.getBalance();
            if (userBalance != null) {
                replyMessagesService.setLocaleMessageService(currentUser.getLanguage_code());
                currentUser.setState_id(BotState.WAIT_OPERATION);
                Source source = userBalance.getSource();
                reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.balance", Emojis.BANK,Emojis.DOLLAR,String.format("%.1f",userBalance.getAmount()),convertToDollar(userBalance.getAmount()));
                userService.saveUser(currentUser);
                } else {
                currentUser.setState_id(BotState.NONE);
                reply =  replyMessagesService.getReplyMessage(message.getChatId(),"reply.command.start.authorized.setBalance",currentUser.getFirst_name(),Emojis.SMILE);
                userService.saveUser(currentUser);
            }
        }
        return reply;
    }
    public String convertToDollar (double amount) {
        String formatted = String.format("%.2f",amount / COURSE_DOLLAR);
        return formatted;
    }
    @Override
    public BotState getHandlerName() {
        return BotState.GET_BALANCE;
    }
}
