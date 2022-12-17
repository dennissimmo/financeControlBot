package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.User;
import com.denchik.demo.service.Query.ParseQueryDataService;
import com.denchik.demo.service.Query.ReportQuery;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.ReportBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.List;
@Component
@Log4j2
public class GenerateReportQueryHandler implements CallbackQueryHandler {
    private static final String NEXT = "Next";
    private static final String PREVIOUS = "Previous";
    private UserService userService;
    private ParseQueryDataService parseQueryDataService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;
    private ReportBuilder reportBuilder;

    public GenerateReportQueryHandler(ParseQueryDataService parseQueryDataService, UserService userService, ControlMoneyTelegramBot controlMoneyTelegramBot, ReportBuilder reportBuilder) {
        this.parseQueryDataService = parseQueryDataService;
        this.userService = userService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
        this.reportBuilder = reportBuilder;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        log.info("Start Handle");
        Integer messageId = callbackQuery.getMessage().getMessageId();
        ReportQuery reportQuery = parseQueryDataService.getReportQuery(callbackQuery);
        Long chatId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.findUserByChatId(chatId);
        LocalDate queryDate = LocalDate.from(LocalDate.of(reportQuery.getYear(), reportQuery.getMonth(), 1));
        String reportMessage = this.reportBuilder.buildReportMessage(currentUser, queryDate);
        currentUser.setState_id(BotState.WAIT_OPERATION);
        userService.saveUser(currentUser);
        log.info("Finish Handle");
        InlineKeyboardMarkup monthKeyboard = this.reportBuilder.getMonthKeyBoard(reportQuery.getMonth(), reportQuery.getYear());
        controlMoneyTelegramBot.editMessageWithKeyboard(chatId, messageId, reportMessage, monthKeyboard);
        return new SendMessage();
    }

    @Override
    public List<String> getHandlerQueryType() {
        return List.of(NEXT, PREVIOUS);
    }
}
