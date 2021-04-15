package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.BotStateContext;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.Source;
import com.denchik.demo.model.User;
import com.denchik.demo.service.BalanceService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.SourceService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Pattern;

@Component
@Log4j2
public class SetBalanceCommandHandler implements InputMessageHandler {
    private BalanceService balanceService;
    private SourceService sourceService;
    private ReplyMessagesService replyMessagesService;
    private UserService userService;
    public SetBalanceCommandHandler (ReplyMessagesService replyMessagesService, UserService userService,BalanceService balanceService, SourceService sourceService) {
        this.balanceService = balanceService;
        this.sourceService = sourceService;
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
    }
    @Override
    public SendMessage handle(Message message) {
        User user = userService.findUserByChat_id(message.getChatId());
        if (user.getState_id().equals(BotState.SET_BALANCE.ordinal()))
        {
            user.setState_id(BotState.ASK_BALANCE);
            System.out.println("User state inside handle function " + user.getState_id());
            userService.saveUser(user);
        }
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SET_BALANCE;
    }

   private SendMessage processUserInput (Message message) {
        String userInput = message.getText();
        long chat_id = message.getChatId();
        User currentUser = userService.findUserByChat_id(chat_id);
        BotState botState = BotState.getBotStateById(currentUser.getState_id());
       Balance userBalance = currentUser.getBalance();
        SendMessage reply = null;
       System.out.println(botState);
        if (botState.equals(BotState.ASK_BALANCE)) {
            System.out.println("botState.equals(BotState.ASK_BALANCE)");
            if (userBalance == null) {
                reply = replyMessagesService.getReplyMessage(chat_id, "reply.command.setBalance.askBalance", Emojis.DOLLAR);
                currentUser.setState_id(BotState.CONFIRM_BALANCE_SET);
                userService.saveUser(currentUser);
            } else {
                reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.setBalance.change",Emojis.BANK,String.format("%.1f UAH",userBalance.getAmount()),Emojis.DOLLAR,Emojis.CLIP);
                currentUser.setState_id(BotState.CONFIRM_BALANCE_SET);
                userService.saveUser(currentUser);
            }
        }
        if (botState.equals(BotState.CONFIRM_BALANCE_SET)) {
                if (isDigitNumber(userInput)) {
                    double amount = Double.parseDouble(userInput);
                    String sourceType = "Готівка";
                    Source source = sourceService.getSourceByTypeSource(sourceType);
                    userBalance = new Balance(amount,source);
                    log.info("Create new balance : amount : {} source : {} id: {} ",amount,source.getTypeSource(),userBalance.getId());
                    currentUser.setBalance(userBalance);
                    reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.setBalance.confirmed",Emojis.BANK,Emojis.DOLLAR,String.format("%.1f",userBalance.getAmount()));
                    currentUser.setState_id(BotState.WAIT_OPERATION); // Якщо користувач коректно вказав баланс, переводимо бота в стан очікування ведення операції
                    balanceService.saveBalance(userBalance);
                    userService.saveUser(currentUser);

                } else if (userInput.equals("-")) {
                    reply = replyMessagesService.getReplyMessage(chat_id,"reply.information.addOperation",Emojis.INFO);
                    currentUser.setState_id(BotState.WAIT_OPERATION); // Якщо користувач коректно вказав баланс, переводимо бота в стан очікування ведення операції
                    balanceService.saveBalance(userBalance);
                    userService.saveUser(currentUser);
                }
                else {
                    reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.setBalance.incorrectInput",Emojis.ORANGE_WARNING);
                }
        }
        return reply;
    }
    private boolean isDigitNumber (String messageText) {
        String regex = "\\d+";
        Pattern digits = Pattern.compile(regex);
        boolean isOperationText = digits.matcher(messageText).matches();
        return isOperationText;
    }
}
