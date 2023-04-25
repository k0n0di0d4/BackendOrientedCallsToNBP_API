package com.nbpapi.main.responses.buyaskrate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class BuyAskRatesResponse {
    public String getNo() {
        return no;
    }

    String no;

    public String getEffectiveDate() {
        return effectiveDate;
    }

    String effectiveDate;

    public double getBid() {
        return bid;
    }

    double bid;

    public double getAsk() {
        return ask;
    }

    double ask;
}
