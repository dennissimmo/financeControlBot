package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.User;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import com.denchik.demo.utils.ReportBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

@Component
@Log4j2
public class ReportCommandHandler implements InputMessageHandler{

    private final ReplyMessagesService replyMessagesService;
    private final UserService userService;
    private final ReportBuilder reportBuilder;

    public ReportCommandHandler(UserService userService, ReplyMessagesService replyMessagesService, ReportBuilder reportBuilder) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
        this.reportBuilder = reportBuilder;
    }

    @Override
    public SendMessage handle(Message message) {
        log.info("Start monthly report building");
        SendMessage reply = new SendMessage().enableHtml(true);
        Long chat_id = message.getChatId();
        User currentUser = userService.findUserByChatId(message.getChatId());
        Balance userBalance = currentUser.getBalance();
        if (userBalance == null) {
            reply = replyMessagesService.getReplyMessage(chat_id,"reply.command.start.authorized.setBalance", String.format("<b>%s</b>", currentUser.getFirst_name()), Emojis.GRITING);
            return reply;
        }

        String reportMessage = this.reportBuilder.buildReportMessage(currentUser, LocalDate.now());
        log.info("Report message: {}", reportMessage);
        currentUser.setState_id(BotState.WAIT_OPERATION);
        userService.saveUser(currentUser);
        LocalDate date = LocalDate.now();
        reply.setReplyMarkup(this.reportBuilder.getMonthKeyBoard(date.getMonthValue(), date.getYear()));
        log.info("Finish Handle");
        return reply.setText(reportMessage).setChatId(chat_id);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.REPORT;
    }
}
