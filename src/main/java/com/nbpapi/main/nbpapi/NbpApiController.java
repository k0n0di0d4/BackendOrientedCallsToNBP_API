package com.nbpapi.main.nbpapi;

import com.nbpapi.main.responses.ResponseMessage;
import com.nbpapi.main.responses.averageexchange.DayCodeResponse;
import com.nbpapi.main.responses.buyaskrate.BuyAskResponse;
import com.nbpapi.main.responses.maxminaverage.MaxMinAverageResponse;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Controller class for endpoints to the API
@RestController
public class NbpApiController {

    // initialize our service for this
    private final NbpApiService nbpApiService;

    // constructor injection
    @Autowired
    public NbpApiController(NbpApiService nbpApiService) {
        this.nbpApiService = nbpApiService;
    }

    // simple endpoint to test if server works correctly
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello World!");
    }

    // The function that answers the first task: "Given a date (formatted YYYY-MM-DD)
    // and a currency code, provide its average exchange rate."

    // Input: (Currency code e.g "USD", Date e.g "2023-04-20") -> Output: (Average Exchange Rate e.g 4.2024)
    @GetMapping("/averageExchangeRate/{currencyCode}/{date}")
    public ResponseEntity<?> getAverageFromDayByCode(@PathVariable String currencyCode,
                                                     @PathVariable String date) {
        // prepare 1 call to NBP API
        String url = String.format("http://api.nbp.pl/api/exchangerates/rates/a/%s/%s/", currencyCode, date);

        // initialize resttemplate to make requests and receive responses
        RestTemplate restTemplate = new RestTemplate();

        // try to gracefully handle errors
        try{
            // request to fetch a DayCodeResponse object from the endpoint provided in urls
            DayCodeResponse response = restTemplate.getForObject(url, DayCodeResponse.class);

            // initialize and assign the value of 0.00 to mid
            double mid = 0.00;

            // assign the value of mid from our response
            mid = response.getRates().get(0).getMid();

            // then after everything's correct return result with HTTP Code 200
            return new ResponseEntity<>(mid, HttpStatus.OK);

            // if NBP API threw an exception, return different Messages with HTTP codes related to these exceptions
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            else {
                // if the exception was not caused by other errors, re-throw the exception
                throw ex;
            }
        }
    }

    // The function that answers the second task: "Given a currency code and the number of last
    // quotations N (N <= 255), provide the max and min average value (every day has a different average)."

    // Input: (Currency code e.g "USD", Last Quotations e.g 230) -> Output: (Maximum and Minimum Average Value e.g [5.0381, 4,1905])
    @GetMapping("/minMaxLastQuotations/{currencyCode}/{lastQuotations}")
    public ResponseEntity<?> getMaxMinFromLastQuotations(@PathVariable String currencyCode,
                                                         @PathVariable int lastQuotations) {
        // check if the limit of 255 Quotations is exceeded and return code 400
        if (lastQuotations > 255) {
            return new ResponseEntity<>(new ResponseMessage("Invalid input! Maximum value of lastNotations is 255."), HttpStatus.BAD_REQUEST);
        }

        // make sure that the code that is sent is correct by making it uppercase
        currencyCode = currencyCode.toUpperCase();

        // set the ending date to today
        LocalDate endDate = LocalDate.now();

        // find the starting date except holidays and weekends
        LocalDate startDate = nbpApiService.getStartDateWithValidDays(lastQuotations);

        // initialize List of type String for urls to be processed
        List<String> urls = new ArrayList<>();

        // initialize resttemplate to make requests and receive responses
        RestTemplate restTemplate = new RestTemplate();

        // find the days between the two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        // NBP Api can't accept more than 367 days, so in that case there needs to be 2 calls to NBP API
        if (daysBetween > 367){
            // get half of the date
            LocalDate midDate = endDate.minusDays(daysBetween / 2);

            // prepare 2 calls to NBP API that have close to equal size
            String url1 = String.format("http://api.nbp.pl/api/exchangerates/rates/a/%s/%s/%s", currencyCode, startDate, midDate);
            String url2 = String.format("http://api.nbp.pl/api/exchangerates/rates/a/%s/%s/%s", currencyCode, midDate.plusDays(1), endDate);

            // Add them at the end of the empty list
            urls.add(url1);
            urls.add(url2);
        } else {
            // prepare 1 call to NBP API
            String url = String.format("http://api.nbp.pl/api/exchangerates/rates/a/%s/%s/%s", currencyCode, startDate, endDate);

            //Add it at the end of the empty list
            urls.add(url);
        }

        // try to gracefully handle errors
        try {
            // assign max to the lowest double value possible
            double max = Double.MIN_VALUE;

            // assign min to the highest double value possible
            double min = Double.MAX_VALUE;

            // iterate through every url
            for(String x: urls) {
                // request to fetch a MaxMinAverageResponse object from the endpoint provided in urls
                MaxMinAverageResponse response = restTemplate.getForObject(x, MaxMinAverageResponse.class);

                // find minimum and maximum average value
                Optional<double[]> minMaxMid = nbpApiService.getMinMaxMidByCode(response);

                // check if there's value inside
                if(minMaxMid.isPresent()) {
                    //
                    double[] minMax = minMaxMid.get();

                    //check if retrieved max is bigger than our current max
                    if (minMax[0] > max) max = minMax[0];

                    //check if min is smaller than our current min
                    if (minMax[1] < min) min = minMax[1];
                }
            }
            // make an array with max and min values
            Optional<double[]> result = Optional.of(new double[]{max, min});

            // then after everything's correct return result with HTTP Code 200
            return new ResponseEntity<>(result, HttpStatus.OK);

            // if NBP API threw an exception, return different Messages with HTTP codes related to these exceptions
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(new ResponseMessage("Didn't find any!"), HttpStatus.NOT_FOUND);
            }
            else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(new ResponseMessage("Invalid input!"), HttpStatus.BAD_REQUEST);
            }
            // if the exception was not caused by other errors, re-throw the exception
            else throw ex;
        }
    }

    // The function that answers the third task: "Given a currency code and the number of last quotations
    // N (N <= 255), provide the major difference between the buy and ask rate (every day has different rates)."

    // Input: (Currency code e.g "USD", Last Quotations e.g 230) -> Output: (Major Difference Between Buy and Ask rate e.g 0.1004)
    @GetMapping("majorDifferenceBuyAsk/{currencyCode}/{lastQuotations}")
    public ResponseEntity<?> getMajorDifferenceBetweenBuyAskFromLastQuotations(@PathVariable String currencyCode,
                                                                               @PathVariable int lastQuotations) {

        // check if the limit of 255 Quotations is exceeded and return code 400
        if (lastQuotations > 255) {
            return new ResponseEntity<>(new ResponseMessage("Invalid input! Maximum value of lastNotations is 255."), HttpStatus.BAD_REQUEST);
        }
        // make sure that the code that is sent is correct by making it uppercase
        currencyCode = currencyCode.toUpperCase();

        // set the ending date to today
        LocalDate endDate = LocalDate.now();

        // find the starting date except holidays and weekends
        LocalDate startDate = nbpApiService.getStartDateWithValidDays(lastQuotations);

        // initialize List of type String for urls to be processed
        List<String> urls = new ArrayList<>();

        // initialize resttemplate to make requests and receive responses
        RestTemplate restTemplate = new RestTemplate();

        // find the days between the two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        // NBP Api can't accept more than 367 days, so in that case there needs to be 2 calls to NBP API
        if (daysBetween > 367) {
            // get half of the date
            LocalDate midDate = endDate.minusDays(daysBetween / 2);

            // prepare 2 calls to NBP API that have close to equal size
            String url1 = String.format("http://api.nbp.pl/api/exchangerates/rates/c/%s/%s/%s", currencyCode, startDate, midDate);
            String url2 = String.format("http://api.nbp.pl/api/exchangerates/rates/c/%s/%s/%s", currencyCode, midDate.plusDays(1), endDate);

            // Add them at the end of the empty list
            urls.add(url1);
            urls.add(url2);
        } else {
            // prepare 1 call to NBP API
            String url = String.format("http://api.nbp.pl/api/exchangerates/rates/c/%s/%s/%s", currencyCode, startDate, endDate);

            //Add it at the end of the empty list
            urls.add(url);
        }

        // try to gracefully handle errors
        try{
            // assign result to the lowest double value
            double result = Double.MIN_VALUE;

            // for every member of our urls
            for(String x: urls) {
                // request to fetch a BuyAskResponse object from the endpoint provided in urls
                BuyAskResponse response = restTemplate.getForObject(x, BuyAskResponse.class);

                // find major difference between the buy and ask rate
                Optional<Double> maxDiff = nbpApiService.findMaxDiff(response);

                // check if there's value inside
                if(maxDiff.isPresent()) {
                    // get the value out of the Optional
                    double mDiff = maxDiff.get();

                    // if this value is bigger than current result, make it the new result
                    if(mDiff > result) result = mDiff;
                }
            }
            // then after everything's correct return result with HTTP Code 200
            return new ResponseEntity<>(result, HttpStatus.OK);

            // if NBP API threw an exception, return different Messages with HTTP codes related to these exceptions
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(new ResponseMessage("Didn't find any!"), HttpStatus.NOT_FOUND);
            }
            else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(new ResponseMessage("Invalid input!"), HttpStatus.BAD_REQUEST);
            }
            // if the exception was not caused by other errors, re-throw the exception
            else throw ex;
        }
    }
}
