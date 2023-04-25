package com.nbpapi.main.responses.averageexchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
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
