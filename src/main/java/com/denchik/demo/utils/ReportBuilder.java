package com.denchik.demo.utils;

import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.TypeOperation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.TypeOperationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
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

@Service
@Log4j2
public class ReportBuilder {

    private final SimpleDateFormat formatData = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat FULL_MONTH_UA = new SimpleDateFormat("MMMM",myDateUA);
    private final SimpleDateFormat FULL_MONTH_RU = new SimpleDateFormat("MMMM",myDateRU);
    private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String NEXT = "Next";
    private static final String PREVIOUS = "Previous";
    private OperationService operationService;
    private ReplyMessagesService replyMessagesService;
    private TypeOperationService typeOperationService;

    public ReportBuilder(OperationService operationService, ReplyMessagesService replyMessagesService, TypeOperationService typeOperationService) {
        this.operationService = operationService;
        this.replyMessagesService = replyMessagesService;
        this.typeOperationService = typeOperationService;
    }

    public String buildReportMessage(User user, LocalDate date) {
        String localeTag = user.getLanguage_code();
        replyMessagesService.setLocaleMessageService(localeTag);
        StringBuilder response = new StringBuilder();
        LocalDate currentData = date;
        Month currentMonthName = currentData.getMonth();
        int indexCurrentMonth = currentMonthName.getValue();
        int currentYear = currentData.getYear();

        log.info ("Month : {} Year: {} Number: {} ", currentMonthName, currentYear, currentMonthName.getValue());
        List<Operation> userOperations = operationService.getOperationPerNumberMonth(indexCurrentMonth, user.getId(), currentYear);

        String headerMessage = this.getReportHeader(localeTag, currentData);
        response.append(headerMessage);
        response.append("\n");
        response.append("\n");
        String incomeExpensePart = this.getTotalIncomesExpensesSection(userOperations);
        response.append(incomeExpensePart);
        String lastOperationsTitle = "<b>" + replyMessagesService.getReplyText("report.perMonth.lastOperation",Emojis.RECORD) + "</b>";
        response.append(lastOperationsTitle);
        response.append("\n");
        List<Operation> lastOperations = operationService.findLastOperationsUser(user.getId(),3, indexCurrentMonth, currentYear);
        lastOperations.forEach(operation -> {
            if (operation.getCreateAt() != null) {
                String operationRow = String.format("\n   <code>∟ %s %.2f => %s </code>\n",
                        formatData.format(operation.getCreateAt()),
                        operation.getAmount(),
                        operation.getCategory().getName());
                response.append(operationRow);
            }
        });
        String footer = this.getReportFooter(user.getBalance().getAmount());
        response.append("\n");
        response.append(footer);
        return response.toString();
    }

    private String getReportHeader(String locale, LocalDate date) {
        String headerMessage;
        int year = date.getYear();
        int monthIndex = date.getMonthValue();
        Date getDateForHeader = getDateFromLocalDate(year, monthIndex);
        if (locale.equals("ua-UA")) {
            headerMessage = String.format("<b>%s</b>",replyMessagesService.getReplyText("report.header", Emojis.NOTEPAD,String.format(" %s %d ", FULL_MONTH_UA.format(getDateForHeader), year)));
        } else if (locale.equals("ru-RU")) {
            headerMessage = replyMessagesService.getReplyText("report.header", Emojis.NOTEPAD, String.format("%s %d", FULL_MONTH_RU.format(getDateForHeader), year));
        } else {
            headerMessage = replyMessagesService.getReplyText("report.header", Emojis.NOTEPAD, String.format("%s %d", getMonthNameFromLocalDate(year, monthIndex), year));
        }

        return headerMessage;
    }

    private String getReportFooter(Double balance) {
        StringBuilder footer = new StringBuilder();
        footer.append("\n")
                .append("<code>******************************************")
                .append("\n")
                .append(replyMessagesService.getReplyText("report.perMonth.balance",Emojis.MONEYBAG,String.format("%.2f ", balance)))
                .append("\n")
                .append("************************************************")
                .append("\n\n</code>")
                .append(replyMessagesService.getReplyText("report.operation",Emojis.PENCIL));
        return footer.toString();
    }

    private String getTotalAmountTitle(List<Operation> operations, String replyPath, Emojis operationType) {
        StringBuilder incomeBuilder = new StringBuilder();
        String replyText = "<b>" + replyMessagesService.getReplyText(replyPath, operationType);
        double totalAmount = operations.stream().mapToDouble(Operation::getAmount).sum();
        incomeBuilder.append(replyText);
        String amountPart = operations.size() > 0 ? String.format(": %.2f </b>\n", totalAmount) : ": 0 </b>\n";
        incomeBuilder.append(amountPart);
        return incomeBuilder.toString();
    }

    private String getTotalIncomesExpensesSection(List<Operation> userOperations) {
        StringBuilder totalSectionBuilder = new StringBuilder();
        TypeOperation incomeType = typeOperationService.getTypeByName("Income");
        TypeOperation expenseType = typeOperationService.getTypeByName("Expense");
        List<Operation> incomeOperations = userOperations.stream()
                .filter(operation -> operation.getTypeOperation().getName().equals(incomeType.getName()))
                .collect(Collectors.toList());
        // get income title
        String incomeTitle = this.getTotalAmountTitle(incomeOperations, "report.perMonth.income", Emojis.INCOME);
        totalSectionBuilder.append(incomeTitle);
        List<Category> incomeCategories = incomeOperations.stream()
                .map(Operation::getCategory).distinct().collect(Collectors.toList());
        incomeCategories.forEach(category -> {
            List<Operation> categoryOperations = incomeOperations.stream()
                    .filter(operation -> operation.getCategory().getName().equals(category.getName()))
                    .collect(Collectors.toList());
            double totalAmount = categoryOperations.stream().mapToDouble(Operation::getAmount).sum();
            String categoryRow = String.format("\n    <code>%s %s %.2f</code> \n", replyMessagesService.getReplyText("emojis.empty", Emojis.PUSHPIN), category.getName(), totalAmount);
            totalSectionBuilder.append(categoryRow);
        });

        List<Operation> expenseOperations = userOperations.stream()
                .filter(operation -> operation.getTypeOperation().getName().equals(expenseType.getName()))
                .collect(Collectors.toList());
        List<Category> expenseCategories = expenseOperations.stream()
                .map(Operation::getCategory).distinct().collect(Collectors.toList());
        totalSectionBuilder.append("\n");
        String expenseTitle = this.getTotalAmountTitle(expenseOperations, "report.perMonth.expense", Emojis.EXPENSE);
        totalSectionBuilder.append(expenseTitle);

        expenseCategories.forEach(category -> {
            List<Operation> categoryOperations = expenseOperations.stream()
                    .filter(operation -> operation.getCategory().getName().equals(category.getName()))
                    .collect(Collectors.toList());
            double totalAmount = categoryOperations.stream().mapToDouble(Operation::getAmount).sum();
            String categoryRow = String.format("\n    <code>%s %s %.2f</code> \n", replyMessagesService.getReplyText("emojis.empty", Emojis.PUSHPIN), category.getName(), totalAmount);
            totalSectionBuilder.append(categoryRow);
        });
        totalSectionBuilder.append("\n");
        return totalSectionBuilder.toString();
    }

    public Date getDateFromLocalDate(int currentYear, int indexCurrentMonth) {
        LocalDate myLocalDate = LocalDate.of(currentYear, indexCurrentMonth,1);
        Date date = null;
        try {
            date = defaultFormat.parse(myLocalDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public String getMonthNameFromLocalDate(int currentYear, int indexCurrentMonth) {
        LocalDate myLocalDate = LocalDate.of(currentYear,indexCurrentMonth,1);
        Month month = myLocalDate.getMonth();
        System.out.println(month.toString());
        return month.toString();
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            return null;
        }
    }
    public InlineKeyboardMarkup getMonthKeyBoard(int month, int year) {
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
        InlineKeyboardButton previousButton = new InlineKeyboardButton().setText("<<<").setCallbackData(String.format("%s|%d|%d", PREVIOUS, previousMonth, previousYear));
        Date getDateForHeader = getDateFromLocalDate(year,month);
        InlineKeyboardButton nextButton = new InlineKeyboardButton().setText(">>>").setCallbackData(String.format("%s|%d|%d", NEXT, nextMonth, nextYear));
        button1.add(previousButton);
        button1.add(nextButton);
        buttons.add(button1);
        markup.setKeyboard(buttons);
        return markup;
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

}
