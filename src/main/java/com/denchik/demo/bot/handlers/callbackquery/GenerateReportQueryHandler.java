package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.*;
import com.denchik.demo.service.*;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Log4j2
public class GenerateReportQueryHandler implements CallbackQueryHandler {
    private final SimpleDateFormat formatData = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat FULL_MONTH_UA = new SimpleDateFormat("MMMM",myDateUA);
    private final SimpleDateFormat FULL_MONTH_RU = new SimpleDateFormat("MMMM",myDateRU);
    private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String NEXT = "Next";
    private static final String PREVIOUS = "Previous";
    private OperationService operationService;
    private ReplyMessagesService replyMessagesService;
    private UserService userService;
    private TypeOperationService typeOperationService;
    private ParseQueryDataService parseQueryDataService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;

    public GenerateReportQueryHandler(OperationService operationService, ReplyMessagesService replyMessagesService, ParseQueryDataService parseQueryDataService, UserService userService, TypeOperationService typeOperationService,ControlMoneyTelegramBot controlMoneyTelegramBot) {
        this.operationService = operationService;
        this.replyMessagesService = replyMessagesService;
        this.parseQueryDataService = parseQueryDataService;
        this.userService = userService;
        this.typeOperationService = typeOperationService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        log.info("Start Handle");
        String callbackQueryData = callbackQuery.getData();
        SendMessage reply = new SendMessage().enableHtml(true);
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String nextOrPrevious = parseQueryDataService.getTypeOperationFromChooseTypeOperationQuery(callbackQuery);
        int indexCurrentMonth = Integer.parseInt(parseQueryDataService.getMonthNumberQuery(callbackQuery));
        int currentYear = Integer.parseInt(parseQueryDataService.getYearNumberQuery(callbackQuery));
        Long chatId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.findUserByChatId(chatId);
        String localeTag = currentUser.getLanguage_code();
        replyMessagesService.setLocaleMessageService(localeTag);
        StringBuilder response = new StringBuilder();
        LocalDate currentData = LocalDate.now();
        Month currentMonthName = currentData.getMonth();
        Month month = Month.of(indexCurrentMonth);
        Date getDateForHeader = getDateFromLocalDate(currentYear,indexCurrentMonth);
        String headerMessage;
        log.info ("Month : {} Year: {} Number: {} ",currentMonthName,currentYear,currentMonthName.getValue());
        List<Operation> userOperations = operationService.getOperationPerNumberMonth(indexCurrentMonth,currentUser.getId(),LocalDate.now().getYear());
        Balance userBalance = currentUser.getBalance();
        TypeOperation incomeType = typeOperationService.getTypeByName("Income");
        TypeOperation expenseType = typeOperationService.getTypeByName("Expense");
        response.append(String.format("<b>%s</b>",replyMessagesService.getReplyText("report.header",
                Emojis.NOTEPAD,String.format(" %s %d ",getNameMonthByLocale(localeTag,getDateForHeader,month),currentYear))));
        response.append("\n\n");
        List<Operation> incomeOperations = userOperations.stream().filter(operation -> operation.getTypeOperation().getName().equals(incomeType.getName())).collect(Collectors.toList());
        //List<Operation> incomeOperations = operationService.findAllOperationByTypeCategory(incomeType,currentUser);
        if (incomeOperations.size() > 0) {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.income", Emojis.INCOME)).append(String.format(": %.2f \n",operationService.sumAmountOperationsByTypeOperation(incomeType,currentUser,indexCurrentMonth,2021)) + "</b>");
        } else {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.income", Emojis.INCOME)).append(": 0 </b>\n");
        }
        List<Category> uniqueCategories = incomeOperations.stream().map(operation -> operation.getCategory()).distinct().collect(Collectors.toList());
        if (uniqueCategories.size() > 0) {
            for (Category category : uniqueCategories) {
                if (category != null) {
                    response.append(String.format("\n    <code>%s %s %.2f</code> \n", replyMessagesService.getReplyText("emojis.empty", Emojis.PUSHPIN), category.getName(), operationService.sumAmountOperationsByCategory(category, currentUser)));
                }
            }
        } else {
            response.append("\n");
        }
        List<Operation> expenseOperations = userOperations.stream().filter(operation -> operation.getTypeOperation().getName().equals(expenseType.getName())).collect(Collectors.toList());
        List<Category> expenseCategories = expenseOperations.stream().map(operation -> operation.getCategory()).distinct().collect(Collectors.toList());
        response.append("\n");
        if (expenseOperations.size() > 0) {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.expense", Emojis.EXPENSE)).append(String.format(": %.2f \n",operationService.sumAmountOperationsByTypeOperation(expenseType,currentUser,indexCurrentMonth,2021))+ "</b>");
        } else {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.expense", Emojis.EXPENSE)).append(": 0 \n</b>");
        }
        expenseCategories.forEach(category -> response.append(String.format("\n   <code> %s %s %.2f </code>\n",replyMessagesService.getReplyText("emojis.empty",Emojis.PUSHPIN),category.getName(),operationService.sumAmountOperationsByCategory(category,currentUser))));
        response.append("\n");
        response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.lastOperation",Emojis.RECORD) + "</b>");
        List<Operation> lastOperations = operationService.findLastOperationsUser(currentUser.getId(),3,indexCurrentMonth,currentYear);
        for (Operation operation : lastOperations) {
            if (operation.getCreateAt() != null) {
                response.append(String.format("\n   <code>∟ %s %.2f => %s </code>\n",formatData.format(operation.getCreateAt()),operation.getAmount(),operation.getCategory().getName()));
            }
        }
        //lastOperations.forEach(operation -> response.append(String.format("\n   <code>∟ %s %.2f => %s </code>\n",formatData.format(operation.getCreateAt()),operation.getAmount(),operation.getCategory().getName())));
        StringBuilder footer = new StringBuilder();
        footer.append("\n")
                .append("<code>******************************************")
                .append("\n")
                .append(replyMessagesService.getReplyText("report.perMonth.balance",Emojis.MONEYBAG,String.format("%.2f ",userBalance.getAmount())))
                .append("\n")
                .append("************************************************")
                .append("\n\n</code>")
                .append(replyMessagesService.getReplyText("report.operation",Emojis.PENCIL));
        response.append(footer);
        currentUser.setState_id(BotState.WAIT_OPERATION);
        userService.saveUser(currentUser);
        log.info("Finish Handle");
        controlMoneyTelegramBot.editMessage(chatId,messageId,response.toString(),changeMonthKeyboard(indexCurrentMonth,currentYear, nextOrPrevious,localeTag,getDateForHeader));
        return new SendMessage();
    }
    public String getNameMonthByLocale (String localeTag,Date date,Month month) {
        if (localeTag.equals("ua-UA")) {
                   return FULL_MONTH_UA.format(date);
        } else if (localeTag.equals("ru-RU")) {
            return FULL_MONTH_RU.format(date);
        } else {
            return month.toString();
        }
    }
    public InlineKeyboardMarkup changeMonthKeyboard (int month, int year,String operationType,String localeTag, Date date) {
        int nextMonth = month + 1;
        int nextYear = year;
        int previousMonth = month - 1;
        int previousYear = year;
        if (month == 12) {
            nextMonth = 1;
            nextYear += 1;
        }
        if (month == 1) {
            previousMonth = 12;
            previousYear -= 1;
        }
         InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton previousButton = new InlineKeyboardButton().setText("<<<").setCallbackData(String.format("%s|%d|%d","Previous",previousMonth,previousYear));
        Date getDateForHeader = getDateFromLocalDate(year,month);
        InlineKeyboardButton nextButton = new InlineKeyboardButton().setText(">>>").setCallbackData(String.format("%s|%d|%d","Next",nextMonth,nextYear));
        button1.add(previousButton);
        button1.add(nextButton);
        buttons.add(button1);
        markup.setKeyboard(buttons);
        return markup;
    }
    public Date getDateFromLocalDate (int currentYear, int indexCurrentMonth) {
        LocalDate myLocalDate = LocalDate.of(currentYear,indexCurrentMonth,1);
        Date date = null;
        try {
            date = defaultFormat.parse(myLocalDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static DateFormatSymbols myDateRU = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"январь", "февраль", "март", "апрель", "май", "июнь",
                    "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};
        }

    };
    private static DateFormatSymbols myDateUA = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"січень", "лютий", "березень", "квітень", "травень", "червень",
                    "липень", "серпень","вересень", "листопад", "жовтень", "грудень"};
        }
    };

    @Override
    public List<String> getHandlerQueryType() {
        return List.of(NEXT,PREVIOUS);
    }
}
