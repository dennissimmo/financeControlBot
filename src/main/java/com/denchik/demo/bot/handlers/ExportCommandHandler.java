package com.denchik.demo.bot.handlers;

import com.denchik.demo.bot.BotState;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.Balance;
import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.CategoryService;
import com.denchik.demo.service.OperationService;
import com.denchik.demo.service.ReplyMessagesService;
import com.denchik.demo.service.UserService;
import com.denchik.demo.utils.Emojis;
import com.denchik.demo.utils.OperationExcelExport;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ExportCommandHandler implements InputMessageHandler{
    private final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private UserService userService;
    private ReplyMessagesService replyMessagesService;
    private OperationService operationService;
    private CategoryService categoryService;
    private ControlMoneyTelegramBot controlMoneyTelegramBot;
    public ExportCommandHandler(UserService userService, ReplyMessagesService replyMessagesService,OperationService operationService,CategoryService categoryService,ControlMoneyTelegramBot controlMoneyTelegramBot) {
        this.userService = userService;
        this.replyMessagesService = replyMessagesService;
        this.operationService = operationService;
        this.categoryService = categoryService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        User currentUser = userService.findUserByChat_id(message.getChatId());
        List<Operation> userOperations = operationService.findOperationsByUser(currentUser);
        List<Category> distinctOperationCategory = userOperations.stream().map(operation -> operation.getCategory()).distinct().collect(Collectors.toList());
        if (currentUser.getState_id() == BotState.EXPORT.ordinal()) {
            OperationExcelExport export = new OperationExcelExport(userOperations,distinctOperationCategory,operationService);
            LocalDateTime dateObj = LocalDateTime.now();
            String currentDataTime = dateObj.format(DATE_TIME_FORMAT);
            String fileName = "ControlMoneyBot_" + currentDataTime;
            controlMoneyTelegramBot.createDocument(message.getChatId(),fileName,export.export());
            currentUser.setState_id(BotState.WAIT_OPERATION);
            log.info("User : {} Exported report: {}",currentUser.toString(),fileName);
            userService.saveUser(currentUser);
        }
        return replyMessagesService.getReplyMessage(message.getChatId(),"reply.excel.success", Emojis.POINT_UP);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EXPORT;
    }
}
