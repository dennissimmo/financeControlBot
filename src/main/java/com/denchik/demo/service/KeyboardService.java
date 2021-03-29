package com.denchik.demo.service;

import com.denchik.demo.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardService {
    private final ReplyMessagesService replyMessagesService;
    public KeyboardService(ReplyMessagesService replyMessagesService) {
        this.replyMessagesService = replyMessagesService;
        System.out.println(" Внедряем Keyboard service");
    }
    public InlineKeyboardMarkup getChooseOperationTypeMarkup () {
            InlineKeyboardMarkup chooseOperationTypeMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton incomes = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.incomes",Emojis.INCOME)).setCallbackData("Income");
            InlineKeyboardButton expenses = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.typeOperation.expenses",Emojis.EXPENSE)).setCallbackData("Expense");
            InlineKeyboardButton cancel = new InlineKeyboardButton(replyMessagesService.getReplyText("reply.operation.cancel",Emojis.CANCEL)).setCallbackData("Cancel");
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            row1.add(incomes);
            row1.add(expenses);
            row2.add(cancel);
            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            buttons.add(row1);
            buttons.add(row2);
            chooseOperationTypeMarkup.setKeyboard(buttons);
            return chooseOperationTypeMarkup;
        }
    public SendMessage sendMessageWithInlineKeyboard (final Long chat_id, String message) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getChooseOperationTypeMarkup();
        final SendMessage sendMessage = createMessageWithKeyboard(chat_id,message,replyKeyboardMarkup);
        return sendMessage;
    }
    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final InlineKeyboardMarkup inlineKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        if (inlineKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        return sendMessage;
    }
}
