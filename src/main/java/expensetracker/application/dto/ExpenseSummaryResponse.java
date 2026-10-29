package expensetracker.application.dto;

import java.math.BigDecimal;

public class ExpenseSummaryResponse {
    private BigDecimal totalExpenses;
    private long expenseCount;

    public ExpenseSummaryResponse(BigDecimal totalExpenses,long expenseCount){
        this.totalExpenses=totalExpenses;
        this.expenseCount=expenseCount;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public long getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(long expenseCount) {
        this.expenseCount = expenseCount;
    }
}
