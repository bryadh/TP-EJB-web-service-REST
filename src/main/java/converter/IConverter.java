package converter;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public interface IConverter {

    public double euroToOtherCurrency(double amount, String currencyCode);
    public double euroToOtherCurrencyRest(double amount, double rate);
    public Map<String, Double> euroToOtherCurrenciesRest(double amount);
    public List<String> getCurrencies();
    public Map<String,Double> getRate();

}
