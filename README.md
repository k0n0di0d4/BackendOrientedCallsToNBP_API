# BackendOrientedCallsToNBP_API
The project that handles 3 given tasks.
### To start the server run this command in the project folder:
```
./gradlew bootRun
```
### To get an average exchange rate for a {currencyCode} and {date} run this command:
```
curl http://localhost:8080/averageExchangeRate/USD/2023-04-20
```
### To provide 
```
curl http://localhost:8080/minMaxLastQuotations/USD/230
```
### To provide 
```
curl http://localhost:8080/majorDifferenceBuyAsk/USD/230
```
