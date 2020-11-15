package converter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless(name = "ConverterEJB")
@Remote
public class ConverterBean implements IConverter {
    public ConverterBean() {
    }

    @Override
    public double euroToOtherCurrency(double amount, String currencyCode) {

        Double ret = 0.0;

        SAXBuilder sxb = new SAXBuilder();
        try {
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            Document document = sxb.build(con.getInputStream());

            Element racine = document.getRootElement();

            System.out.println("DEBUG ---> "+ racine.toString());

            Namespace ns = Namespace.getNamespace("http://www.ecb.int/vocabulary/2002−08−01/eurofxref");

            //Element elem = racine.getChild("Cube",ns);

            Element elem = racine.getChildren().get(2);

            System.out.println("DEBUG ---> "+ elem.toString());

            //Element cube = elem.getChild("Cube", ns);
            Element cube = elem.getChildren().get(0);

            System.out.println("DEBUG ---> "+ cube.toString());

            List<Element> elements = cube.getChildren();

            System.out.println("DEBUG ---> list size "+ elements.size());

            for(Element e : elements ){

                if(e.getAttributeValue("currency").equals(currencyCode)){
                    ret = amount * Double.parseDouble(e.getAttributeValue("rate"));
                    break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public double euroToOtherCurrencyRest(double amount, double rate) {
        return amount * rate;
    }

    @Override
    public Map<String, Double> euroToOtherCurrenciesRest(double amount) {
        Map<String, Double> result = new HashMap<>();

        for(Map.Entry<String,Double> entry : getRate().entrySet()){

            result.put(entry.getKey(), amount * entry.getValue());

        }
        return result;
    }

    @Override
    public List<String> getCurrencies() {

        List<String> currencies = new ArrayList<>();

        try {
            URL url = new URL("http://currencies.apps.grandtrunk.net/currencies");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = in.readLine()) != null){

                currencies.add(line);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencies;
    }

    @Override
    public Map<String, Double> getRate() {

        Map<String,Double> rates = new HashMap<>();

            System.out.println("DEBUG ---> before loop");
            for (String s : this.getCurrencies()){

                System.out.println("DEBUG ---> rate for "+s);
                URL url;

                try {

                    url = new URL("http://currencies.apps.grandtrunk.net/getlatest/eur/"+s);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    String line;
                    if((line = in.readLine()) != null){
                        rates.put(s, Double.parseDouble(line));
                    }


                } catch (IOException e){
                    System.out.println("DEBUG ---> no rate for currency "+s);
                }

            }

            System.out.println("DEBUG ---> after loop");

        return rates;
    }
}
