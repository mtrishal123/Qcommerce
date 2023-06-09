import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SaleInsightsImpl implements SaleInsights {
  @Override
    public SaleAggregate getSaleInsights(DataProvider dataProvider, int year) 
        throws IOException, AnalyticsException {
    Double tsale = 0.0;
    List<Double> totalSale = new ArrayList<Double>();
    List<SaleAggregateByMonth> saleByMonth = new ArrayList<SaleAggregateByMonth>();
    Map<Integer,Double> monthMap = new HashMap<Integer,Double>();
    String line = "";
    File csvFile = dataProvider.resolveFile();
    String vendorName = dataProvider.getProvider();
    InputStream targetStream = new FileInputStream(csvFile);
    InputStreamReader f = new InputStreamReader(targetStream,Charset.defaultCharset());
    BufferedReader br = new BufferedReader(f);  
    if (vendorName.equals("flipkart")) {
      try {
        br.readLine();
        while ((line = br.readLine()) != null) {
          String[] flipkart = line.split(",");
          int date = Integer.parseInt(flipkart[3].substring(0,4));
          int month = Integer.parseInt(flipkart[3].substring(5,7));
          String txnStatus = flipkart[4];
          if (txnStatus.equals("complete") || txnStatus.equals("paid") 
                || txnStatus.equals("shipped")) {
            if (date == year) {
              Double amount = Double.parseDouble(flipkart[5]);
              totalSale.add(amount);
              if (monthMap.containsKey(month)) {
                monthMap.put(month, monthMap.get(month) + amount);
              } else {
                monthMap.put(month, amount); 
              }     
            }
          }
        }
      } catch (RuntimeException e) { 
        throw new AnalyticsException(""); 
      }
    } else if (vendorName.equals("amazon")) {
      try {
        br.readLine();
        while ((line = br.readLine()) != null) {
          String[] amazon = line.split(",");
          int date = Integer.parseInt(amazon[4].substring(0,4));
          int month = Integer.parseInt(amazon[4].substring(5,7));
          String txnStatus = amazon[3];
          if (date == year && txnStatus.equals("shipped")) {
            Double amount = Double.parseDouble(amazon[5]);
            totalSale.add(amount);
            if (monthMap.containsKey(month)) {
              monthMap.put(month, monthMap.get(month) + amount);
            } else {
              monthMap.put(month, amount); 
            }      
          }
        }
      } catch (RuntimeException e) {
        throw new AnalyticsException("");
        
      }
    }  else if (vendorName.equals("ebay")) {
      try {
        br.readLine();
        while ((line = br.readLine()) != null) {
          String[] ebay = line.split(",");
          int date = Integer.parseInt(ebay[3].substring(6,10));
          int month = Integer.parseInt(ebay[3].substring(0,2));
          String status = ebay[2];
          if (status.equals("complete") || status.equals("Delivered")) {
            if (year == date) {
              Double amount = Double.parseDouble(ebay[4]);
              totalSale.add(amount);
              if (monthMap.containsKey(month)) {
                monthMap.put(month, monthMap.get(month) + amount);
              } else {
                monthMap.put(month, amount); 
              }     
            }
          }
        }
      } catch (RuntimeException e) {
        throw new AnalyticsException("");
      }
    }
    for (Double total : totalSale) {
      tsale = tsale + total;
    }
    for (Map.Entry<Integer,Double> month : monthMap.entrySet()) {
      SaleAggregateByMonth sale = new SaleAggregateByMonth(month.getKey(), month.getValue());
      saleByMonth.add(sale);        
    }
    DecimalFormat df = new DecimalFormat("0.00");
    df.setRoundingMode(RoundingMode.UP);
    System.out.println(csvFile);
    br.close();
    return new SaleAggregate(tsale,saleByMonth);
  }
}