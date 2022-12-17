package com.denchik.demo.service.Query;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class ReportQuery {
    private Direction direction;
    private int month;
    private int year;

    public Direction getDirection() {
        return direction;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public ReportQuery(Direction direction, int month, int year) {
        this.direction = direction;
        this.month = month;
        this.year = year;
    }

    public static ReportQuery parseReportQuery(CallbackQuery callbackQuery) {
        String[] query = callbackQuery.getData().split("\\|");
        Direction nextOrPrevious = query[0].equals("Next") ? Direction.NEXT : Direction.PREVIOUS;
        int indexCurrentMonth = Integer.parseInt(query[1]);
        int currentYear = Integer.parseInt(query[2]);
        return new ReportQuery(nextOrPrevious, indexCurrentMonth, currentYear);
    }

}
