package com.nbpapi.main.responses.averageexchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DayCodeResponse {
    public String getTable() {
        return table;
    }

    private String table;

    public String getCurrency() {
        return currency;
    }

    private String currency;

    public String getCode() {
        return code;
    }

    private String code;

    public List<NbpApiRate> getRates() {
        return rates;
    }

    private List<NbpApiRate> rates;

}


