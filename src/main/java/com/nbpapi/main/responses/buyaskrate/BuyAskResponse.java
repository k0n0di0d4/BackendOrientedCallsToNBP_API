package com.nbpapi.main.responses.buyaskrate;

import java.util.List;

public class BuyAskResponse {

    public String getTable() {
        return table;
    }
    String table;

    public String getCurrency() {
        return currency;
    }

    String currency;

    public String getCode() {
        return code;
    }

    String code;
    public List<BuyAskRatesResponse> getRates() {
        return rates;
    }

    List<BuyAskRatesResponse> rates;
}
