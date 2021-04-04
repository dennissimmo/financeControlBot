package com.denchik.demo.contoller;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.bot.TelegramFacade;
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
    private final TelegramFacade telegramFacade;
    public WebHookController(MessageHandler messageHandler,TelegramFacade telegramFacade) {
        this.messageHandler = messageHandler;
        this.telegramFacade = telegramFacade;
        System.out.println("Внедряем Message Handler в WebHook Controller");
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
        return telegramFacade.handleUpdate(update);
        // return messageHandler.handleUpdate(update);
    }
}
