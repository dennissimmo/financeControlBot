package com.denchik.demo.controller;

import com.denchik.demo.bot.TelegramFacade;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final TelegramFacade telegramFacade;
    public WebHookController(TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
        System.out.println("Inject Telegram Facade in WebHook Controller");
    }

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramFacade.handleUpdate(update);
    }

}
