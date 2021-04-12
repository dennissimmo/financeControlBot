package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.model.*;
import com.denchik.demo.service.*;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ReportCommandHandler implements InputMessageHandler{
    private final SimpleDateFormat formatData = new SimpleDateFormat("dd.MM.yyyy");
    private ReplyMessagesService replyMessagesService;
    private OperationService operationService;
    private UserService userService;
    private TypeOperationService typeOperationService;
    private CategoryService categoryService;

    public ReportCommandHandler(OperationService operationService,UserService userService,TypeOperationService typeOperationService,ReplyMessagesService replyMessagesService,CategoryService categoryService) {
        this.categoryService = categoryService;
        this.typeOperationService = typeOperationService;
        this.userService = userService;
        this.operationService = operationService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        log.info("Start Handle");
        SendMessage reply = new SendMessage().enableHtml(true);
        StringBuilder response = new StringBuilder();
        response.append("\n");
        Long chat_id = message.getChatId();
        User currentUser = userService.findUserByChat_id(message.getChatId());
        List<Operation> userOperations = operationService.findOperationsByUser(currentUser);
        Balance userBalance = currentUser.getBalance();
        /*if (userOperations.size() == 0) {
            if (userBalance == null) {

            }
            return replyMessagesService.getReplyMessage()
        }*/

        TypeOperation incomeType = typeOperationService.getTypeByName("Income");
        TypeOperation expenseType = typeOperationService.getTypeByName("Expense");
        List<Operation> incomeOperations = operationService.findAllOperationByTypeCategory(incomeType,currentUser);
        if (incomeOperations.size() > 0) {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.income", Emojis.INCOME)).append(String.format(": %.2f \n",operationService.sumAmountOperationsByTypeOperation(incomeType,currentUser)) + "</b>");
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
        List<Operation> expenseOperations = operationService.getUserOperations(currentUser).stream().filter(operation -> operation.getTypeOperation().getName().equalsIgnoreCase("Expense")).collect(Collectors.toList());
        List<Category> expenseCategories = expenseOperations.stream().map(operation -> operation.getCategory()).distinct().collect(Collectors.toList());
        //List<Category> expenseCategories = categoryService.findDistinctOperationCategoryByUserAndTypeOperationName(currentUser.getId(),"EXPENSE");
        response.append("\n");
        if (expenseOperations.size() > 0) {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.expense", Emojis.EXPENSE)).append(String.format(": %.2f \n",operationService.sumAmountOperationsByTypeOperation(expenseType,currentUser))+ "</b>");
        } else {
            response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.expense", Emojis.EXPENSE)).append(": 0 \n</b>");
        }

        expenseCategories.forEach(category -> response.append(String.format("\n   <code> %s %s %.2f </code>\n",replyMessagesService.getReplyText("emojis.empty",Emojis.PUSHPIN),category.getName(),operationService.sumAmountOperationsByCategory(category,currentUser))));
        response.append("\n");
        response.append("<b>" + replyMessagesService.getReplyText("report.perMonth.lastOperation",Emojis.RECORD) + "</b>");
        List<Operation> lastOperations = operationService.findLastOperationsUser(currentUser.getId(),3);
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
                .append("******************************************")
                .append("\n\n</code>")
                .append(replyMessagesService.getReplyText("report.operation",Emojis.PENCIL));
                response.append(footer);
        //expenseOperations.forEach(expense -> response.append(String.format("Amount : %.2f, Category: %s\n ",expense.getAmount(),expense.getCategory().getName())));
        //expenseOperations.forEach(expense -> System.out.println(expense.toString()));

    /*    for (Operation op : incomeOperations) {
            System.out.println(op.getCategory().getName());
            System.out.println(op.toString());
        }*/
        currentUser.setState_id(BotState.WAIT_OPERATION);
        userService.saveUser(currentUser);
        log.info("Finish Handle");
        return reply.setText(response.toString()).setChatId(chat_id);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.REPORT;
    }
}
