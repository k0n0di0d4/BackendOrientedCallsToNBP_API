package com.nbpapi.main.responses.averageexchange;

import java.time.LocalDate;

public class NbpApiRate {
    public String getNo() {
        return no;
    }

    private String no;

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    private LocalDate effectiveDate;

    public double getMid() {
        return mid;
    }

    private double mid;

    // getters and setters
}
