package contract.insights;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleAggregate {
  private Double totalSales;
  private List<SaleAggregateByMonth> aggregateByMonths;

  public SaleAggregate(Double totalSales, List<SaleAggregateByMonth> aggregateByMonths) {
    this.totalSales = totalSales;
    this.aggregateByMonths = aggregateByMonths;
  }

}