package com.denchik.demo.contoller;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.bot.handlers.MessageHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final MessageHandler messageHandler;
    public WebHookController(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        System.out.println("Внедряем WebHook Controller");
    }
    /*private final ControlMoneyTelegramBot telegramBot;
    public WebHookController (ControlMoneyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    @RequestMapping(value = "/",method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }*/
    @RequestMapping(value = "/",method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return messageHandler.handleUpdate(update);
    }
}
