package contract.insights;

import contract.exceptions.AnalyticsException;
import contract.resolver.DataProvider;
import java.io.IOException;

public interface SaleInsights {

  SaleAggregate getSaleInsights(DataProvider dataProvider, int year)
      throws IOException, AnalyticsException;

}