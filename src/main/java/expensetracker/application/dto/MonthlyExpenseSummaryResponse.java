package expensetracker.application.dto;

import java.math.BigDecimal;

public class MonthlyExpenseSummaryResponse {
    private String month;
    private BigDecimal totalAmount;

    public MonthlyExpenseSummaryResponse(String month,BigDecimal totalAmount){
        this.month=month;
        this.totalAmount=totalAmount;
    }

    public interface MonthlyExpenseSummary{
        String getMonth();
        BigDecimal getTotalAmount();
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
