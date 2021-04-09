package com.denchik.demo.service;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ParseQueryDataService {
    public String getIdOperationFromChooseTypeOperationQuery (CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public String getTypeOperationFromChooseTypeOperationQuery (CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[0];
    }
    public String getLocaleTagFromChooseLanguageQuery (CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public String getOperationDescription (CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[3];
    }
}
