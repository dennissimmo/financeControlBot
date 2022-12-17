package com.denchik.demo.bot.handlers.callbackquery;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.*;
import com.denchik.demo.service.*;
import com.denchik.demo.service.Query.ParseQueryDataService;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component

public class NewOperationQueryHandler implements CallbackQueryHandler{
    private final SimpleDateFormat FULL_MONTH_UA = new SimpleDateFormat("dd MMMM yyyy",myDateUA);
    private final SimpleDateFormat FULL_MONTH_RU = new SimpleDateFormat("dd MMMM yyyy",myDateRU);
    private final SimpleDateFormat FULL_MONTH_EN = new SimpleDateFormat("dd MMMM yyyy");
    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String INCOME = "Income";
    private static final String EXPENSE = "Expense";
    private static final String CANCEL = "Cancel";
    private static final String BACK = "Back";
    private static final String CATEGORY = "ctg";
    private ParseQueryDataService parseQueryDataService;
    private ReplyMessagesService replyMessagesService;
    private UserService userService;
    private CategoryService categoryService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;
    private OperationService operationService;
    private TypeOperationService typeOperationService;
    private BalanceService balanceService;


    public NewOperationQueryHandler(ReplyMessagesService replyMessagesService, UserService userService,CategoryService categoryService,ControlMoneyTelegramBot controlMoneyTelegramBot,OperationService operationService,TypeOperationService typeOperationService,ParseQueryDataService parseQueryDataService,BalanceService balanceService) {
        this.balanceService = balanceService;
        this.parseQueryDataService = parseQueryDataService;
        this.replyMessagesService = replyMessagesService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
        this.operationService = operationService;
        this.typeOperationService = typeOperationService;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chat_id = callbackQuery.getMessage().getChatId();
        final int messageId = callbackQuery.getMessage().getMessageId(); // Need for change message with keyboard in future
        log.info("Callback query data {}",callbackQuery.getData());

        return processCallback(callbackQuery);
    }

    @Override
    public List<String> getHandlerQueryType() {
        List<String> availableQuery = new ArrayList<>();
        availableQuery.add(INCOME);
        availableQuery.add(EXPENSE);
        availableQuery.add(CANCEL);
        availableQuery.add(BACK);
        availableQuery.add(CATEGORY);
        return availableQuery;
    }
    public SendMessage processCallback (CallbackQuery callbackQuery) {
        Integer idHandledOperation = Integer.parseInt(parseQueryDataService.getIdOperationFromChooseTypeOperationQuery(callbackQuery));
        Integer callBackMessageId = callbackQuery.getMessage().getMessageId();
        String callBackText = callbackQuery.getData();
        String callBackData = parseQueryDataService.getTypeOperationFromChooseTypeOperationQuery(callbackQuery);
        long chat_id = callbackQuery.getMessage().getChatId();

        User currentUser = userService.findUserByChatId(chat_id);
        String localeTag = currentUser.getLanguage_code();
        replyMessagesService.setLocaleMessageService(localeTag);
        Operation operation = operationService.findOperationById(idHandledOperation);
            if (callBackData.equals(INCOME)) {
                List<Category> incomeCategories = categoryService.getIncomes(currentUser.getLanguage_code());
                InlineKeyboardMarkup keyboardMarkup = getListCategories(incomeCategories,operation);
                operation.setTypeOperation(typeOperationService.getTypeByName(INCOME));
                operationService.saveOperation(operation);
                List<Operation> userOperations = operationService.getUserOperations(currentUser);
                //displayOperationList(userOperations);
                //replyMessagesService.getReplyText("reply.category.chooseCategory.income",Emojis.EURO,operation.getAmount())
              controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.category.chooseCategory.income", Emojis.WRITINGHANDLE,operation.getAmount()),keyboardMarkup);
            } else if (callBackData.equals(EXPENSE)) {
                List<Category> expenseCategories = categoryService.getExpenses(currentUser.getLanguage_code());
                InlineKeyboardMarkup keyboardMarkup = getListCategories(expenseCategories,operation);
                operation.setTypeOperation(typeOperationService.getTypeByName(EXPENSE));
                operationService.saveOperation(operation);
                controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.category.chooseCategory.expense", Emojis.WRITINGHANDLE,operation.getAmount()),keyboardMarkup);
            } else if (callBackData.equals(CANCEL)) {
                Balance balance = currentUser.getBalance();
                String replyCancel = callbackQuery.getMessage().getText() + "\n - \n" + replyMessagesService.getReplyText("reply.operation.cancel.canceled",Emojis.EMPTYCANCEL);
                controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyCancel,null);
                if (Operation.isExpense(operation)) {
                    balance.upBalance(operation.getAmount());
                } else {
                    balance.downBalance(operation.getAmount());
                }
                balanceService.saveBalance(balance);
                operationService.deleteOperation(operationService.findOperationById(idHandledOperation));
            } else if (callBackData.equals(BACK)){
                controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.category.chooseTypeOperation", Emojis.RECORD),getChooseOperationReplyInlineKeyboard(idHandledOperation));
            } else if (callBackData.contains(CATEGORY)){
                int idChooseCategory = Integer.parseInt(callBackText.split("\\|")[3]);
                Category category = categoryService.findCategoryById(idChooseCategory);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                LocalDateTime dateObj = LocalDateTime.now();
                DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate= dateObj.format(dt);
                operation.setCreateAt(timestamp);
                //operation.setCreateAt(parseTimestamp(formattedDate));
                operation.setCategory(category);
                operationService.saveOperation(operation);
                String dateOperationFormatted = "";
                if (localeTag.equals("ua-UA")) {
                    dateOperationFormatted = String.format("%s року",FULL_MONTH_UA.format(operation.getCreateAt()));
                } else if (localeTag.equals("ru-RU")) {
                    dateOperationFormatted = String.format("%s года",FULL_MONTH_RU.format(operation.getCreateAt()));
                } else {
                    dateOperationFormatted = String.format("%s year",FULL_MONTH_EN.format(operation.getCreateAt()));
                }
                //System.out.println("Type category : " + category.getTypeCategory().getName_type() + replyMessagesService.getReplyText("reply.typeOperation.incomes").trim());
                if (operation.getTypeOperation().getName().equals("EXPENSE")) {
                    controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.operation.successful.add.expense", operation.getAmount(),category.getName(),dateOperationFormatted,Emojis.CHECK),cancelOperation(idHandledOperation));
                } else {
                    controlMoneyTelegramBot.editMessageWithKeyboard(chat_id,callBackMessageId,replyMessagesService.getReplyText("reply.operation.successful.add.income", operation.getAmount(),category.getName(),dateOperationFormatted,Emojis.CHECK),cancelOperation(idHandledOperation));
                }
                // Change balance after operation added
                Balance userBalance = currentUser.getBalance();
                Operation currentOperation = operationService.findOperationById(idHandledOperation);
                double amountOperation = currentOperation.getAmount();
                String currentOperationType = currentOperation.getTypeOperation().getName();
                if (currentOperationType.equals("EXPENSE")) {
                    userBalance.downBalance(amountOperation);
                } else if (currentOperationType.equals("INCOME")){
                    userBalance.upBalance(amountOperation);
                }
                operation.setCurrentBalance(userBalance.getAmount());
                operationService.saveOperation(operation);
                balanceService.saveBalance(userBalance);
            //return replyMessagesService.getReplyMessage(chat_id,"reply.query.incorrect");
        }
        return null;
    }

    public void displayOperationList (List<Operation> userOperations) {
        for(Operation op : userOperations) {
            if (op.getTypeOperation() != null) {
                System.out.println(op.toString());
            }
        }
    }
    private java.sql.Timestamp parseTimestamp(String timestamp) {
        try {
            return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public InlineKeyboardMarkup cancelOperation (Integer idOperation) {
        InlineKeyboardMarkup cancelOperationMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData(String.format("%s|%d","Cancel",idOperation));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(cancel);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row1);
        cancelOperationMarkup.setKeyboard(buttons);
        return cancelOperationMarkup;
    }
    public InlineKeyboardMarkup getChooseOperationReplyInlineKeyboard (Integer idOperation) {
        InlineKeyboardMarkup chooseOperationTypeMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton incomes = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.incomes",Emojis.INCOME)).setCallbackData(String.format("%s|%d","Income",idOperation));
        InlineKeyboardButton expenses = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.expenses",Emojis.EXPENSE)).setCallbackData(String.format("%s|%d","Expense",idOperation));
        //InlineKeyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData(String.format("%s|%d","Cancel",idOperation));
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(incomes);
        row1.add(expenses);
        //row2.add(cancel);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(row1);
        buttons.add(row2);
        chooseOperationTypeMarkup.setKeyboard(buttons);
        return chooseOperationTypeMarkup;
    }
    public InlineKeyboardMarkup getListCategories (List<Category> categoryList,Operation operation) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        //TODO: Bad approach i think, need a find better solution
        for (int i = 0; i < categoryList.size(); i+= 2) {
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            if (i + 1 < categoryList.size()) {
                for (int j = 0; j < 2; j++) {
                    InlineKeyboardButton button = new InlineKeyboardButton().setText(categoryList.get(i+j).getName()).setCallbackData(String.format("ctg|%d|%s|%d",operation.getId(),categoryList.get(i+j).getTypeCategory().getName_type(),categoryList.get(i+j).getId()));
                    listButton.add(button);
                }
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(categoryList.get(i).getName()).setCallbackData(String.format("ctg|%d|%s|%d",operation.getId(),categoryList.get(i).getTypeCategory().getName_type(),categoryList.get(i).getId()));
                listButton.add(button);
            }
            buttons.add(listButton);
        }
        List<InlineKeyboardButton> listBtn = new ArrayList<>();
        listBtn.add(new InlineKeyboardButton().setText(replyMessagesService.getReplyText("button.back",Emojis.BACK)).setCallbackData(String.format("%s|%d","Back",operation.getId())));
        buttons.add(listBtn);
        markup.setKeyboard(buttons);
        // String chat_ids = update.getMessage().getChatId().toString();
        return markup;
    }
   /* public InlineKeyboardMarkup getListCategories (List<Category> categoryList,Operation operation) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        *//*List<Category> categoryList = categoryService.findAllCategories();*//*
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(categoryList.get(i).getName()).setCallbackData(String.format("category|%d|%s|%d",operation.getId(),categoryList.get(i).getName(),categoryList.get(i).getId()));
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            buttons.add(listButton);
        }
        List<InlineKeyboardButton> listBtn = new ArrayList<>();
        listBtn.add(new InlineKeyboardButton().setText(replyMessagesService.getReplyText("button.back",Emojis.BACK)).setCallbackData(String.format("%s|%d","Back",operation.getId())));
        buttons.add(listBtn);
        markup.setKeyboard(buttons);
        // String chat_ids = update.getMessage().getChatId().toString();
        return markup;
    }*/
    private static DateFormatSymbols myDateRU = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };
    private static DateFormatSymbols myDateUA = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"січня", "лютого", "березня", "квітня", "травня", "червня",
                    "липня", "серпня","вересня", "листопада", "жовтня", "грудня"};
        }

    };
}
