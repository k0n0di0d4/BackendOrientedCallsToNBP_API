package com.nbpapi.main.nbpapi;

import com.nbpapi.main.responses.buyaskrate.BuyAskRatesResponse;
import com.nbpapi.main.responses.buyaskrate.BuyAskResponse;
import com.nbpapi.main.responses.maxminaverage.MaxMinAverageResponse;
import com.nbpapi.main.responses.maxminaverage.MaxMinRatesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;

// Service class to be used by our NbpApiController
@Service
public class NbpApiService {

    // hashmap of every Non-floating holiday in poland
    private static final Map<String, Boolean> nonFloatingHolidays = new HashMap<String, Boolean>(){{
        put("01-01", true);
        put("01-06", true);
        //put("03-01", true);
        //put("04-13", true);
        put("05-01", true);
        put("05-03", true);
        //put("05-09", true);
        //put("05-26", true);
        //put("06-23", true);
        //put("06-28", true);
        put("08-15", true);
        //put("08-31", true);
        //put("10-14", true);
        put("10-16", true);
        put("11-01", true);
        put("11-11", true);
        put("12-25", true);
        put("12-26", true);
        //put("12-31", true);
    }};

    // function that finds the maximum and minimum average value from the provided response
    public Optional<double[]> getMinMaxMidByCode(MaxMinAverageResponse responses) {
        // list of rates extracted from the response
        List<MaxMinRatesResponse> rates = responses.getRates();

        // try finding the maximum average value
        OptionalDouble highestMid = rates.stream()
                .mapToDouble(MaxMinRatesResponse::getMid)
                .max();

        // try finding the minimum average value
        OptionalDouble lowestMid = rates.stream()
                .mapToDouble(MaxMinRatesResponse::getMid)
                .min();

        // initialize result
        double[] result = {0.00, 0.00};

        // if highest mid was found assign it to the first member of result
        if (highestMid.isPresent()) {
            double highestMidValue = highestMid.getAsDouble();
            result[0] = highestMidValue;
        }

        // if lowest mid was found assign it to the second member of result
        if (lowestMid.isPresent()) {
            double lowestMidValue = lowestMid.getAsDouble();
            result[1] = lowestMidValue;
        }

        // return result;
        return Optional.of(result);
    }

    // function to find the starting date, without counting the holidays and weekends
    public LocalDate getStartDateWithValidDays(int notations) {
        // get today's date in the format of yyyy-mm-dd
        LocalDate endDate = LocalDate.now();

        //initialize and assign a counter to 0
        int validDays = 0;

        // get yesterday's date
        LocalDate currentDate = endDate.minusDays(1);

        // get date of this year's Easter Sunday
        LocalDate easterSunday = dateOfEasterSunday(currentDate);

        // calculate until found all the working days (no holidays and weekends)
        while (validDays < notations) {

            // if the year changed, calculate the date of eastern sunday again
            if(currentDate.getYear() != endDate.getYear()) {
                easterSunday = dateOfEasterSunday(currentDate);
                // change it by -1 so that it won't
                endDate = endDate.minusYears(1);
            }

            // if it's not holidays or weekend then increment validDays by 1
            if(!currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)  // check if it's not saturday
                    && !currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY) // check if it's not sunday
                    && !isHolidayInPoland(currentDate) // check if it's not any non-floating holiday in Poland
                    && !currentDate.equals(easterSunday.plusDays(1)) // check if it's not Easter Monday
                    && !currentDate.equals(easterSunday.plusDays(50)) // check if it's not Corpus Christi
            ) {
                // if found increment validDays by 1
                validDays++;
            }
            // needs to be checked in case of last iteration, so it doesn't add a day
            if(validDays < notations) currentDate = currentDate.minusDays(1);
        }
        // return the resulting date in the format of yyyy-mm-dd
        return currentDate;
    }

    // function that finds the maximum difference between ask and bid in each rate
    public Optional<Double> findMaxDiff(BuyAskResponse response) {
        // make a list out of all the rates in the response
        List<BuyAskRatesResponse> rates = response.getRates();

        // find which difference is the biggest by every rate
        OptionalDouble maxDiff = rates.stream()
                .mapToDouble(rate -> rate.getAsk() - rate.getBid())
                .max();
        // return the difference
        return maxDiff.isPresent() ? Optional.of(maxDiff.getAsDouble()) : Optional.empty();
    }

    // check if it's a non-floating holiday in Poland
    public boolean isHolidayInPoland(LocalDate date) {
        // the string should be of format "00-00"
        String formattedDate = String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth());

        // check if this date is a member of holidays hashmap
        return nonFloatingHolidays.containsKey(formattedDate);
    }

    // returns the date of Easter Sunday for the given date
    // using the method of Meeus/Jones/Butcher
    public LocalDate dateOfEasterSunday(LocalDate date) {
        int year = date.getYear();
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int p = (h + l - 7 * m + 114) % 31;
        int day = p + 1;
        int month = (h + l - 7 * m + 114) / 31;
        return LocalDate.of(year, month, day);
    }
}
