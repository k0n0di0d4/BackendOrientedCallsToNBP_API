# BackendOrientedCallsToNBP_API
The project that handles 3 given tasks.
## Important:
**{currencyCode}** - should be a 3-letter string that represents a code of a currency e.g. USD  
**{date}** - should be in the format of YYYY-MM-DD e.g. 2023-04-25  
**{lastQuotations}** - should be lower than 256 and an integer e.g. 243
### To start the server run this command in the project folder:
```
./gradlew bootRun
```
### To get an average exchange rate for a {currencyCode} and {date} run a command in this format:
```
curl http://localhost:8080/averageExchangeRate/{currencyCode}/{date}
```
### To get the maximum and minimum average rate for a {currencyCode} in the {lastQuotations} run a command in this format:
```
curl http://localhost:8080/minMaxLastQuotations/{currencyCode}/{lastQuotations}
```
### To get the major difference between buy and ask values for a {currencyCode} in the {lastQuotations} run a command in this format:
```
curl http://localhost:8080/majorDifferenceBuyAsk/{currencyCode}/{lastQuotations}
```
