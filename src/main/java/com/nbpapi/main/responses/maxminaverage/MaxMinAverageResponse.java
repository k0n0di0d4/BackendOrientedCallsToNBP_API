package com.nbpapi.main.responses.maxminaverage;

import java.util.List;

public class MaxMinAverageResponse {
    public String getTable() {
        return table;
    }

    private String table;

    public String getNo() {
        return no;
    }

    private String no;

    public String getEffectiveDate() {
        return effectiveDate;
    }

    private String effectiveDate;

    public List<MaxMinRatesResponse> getRates() {
        return rates;
    }

    private List<MaxMinRatesResponse> rates;
}
