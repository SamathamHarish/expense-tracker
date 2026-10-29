package expensetracker.application.repositories;

import expensetracker.application.dto.CategoryExpenseSummaryResponse;
import expensetracker.application.dto.MonthlyExpenseSummaryResponse.MonthlyExpenseSummary;
import expensetracker.application.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    Page<Expense> findByUserIdAndCategory(Long userId,String category,Pageable pageable);
    Page<Expense> findByUserIdAndExpenseDate(Long userId, LocalDate expenseDate, Pageable pageable);
    Page<Expense> findByUserIdAndCategoryAndExpenseDate(Long userId,String category,LocalDate expenseDate,Pageable pageable);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id= :userId")
    BigDecimal calculateTotalExpenses(@Param("userId") Long userId);

    long countByUserId(Long UserId);

    @Query("SELECT  new expensetracker.application.dto.CategoryExpenseSummaryResponse(e.category, SUM(e.amount)) "+"FROM Expense e WHERE e.user.id=:userId GROUP BY e.category")
    List<CategoryExpenseSummaryResponse> getCategoryWiseSummary(@Param("userId") Long userId);


    @Query(value = """
        SELECT TO_CHAR(e.expense_date,'YYYY-MM') AS month,
                SUM(e.amount) AS "totalAmount" 
        FROM expenses e 
        WHERE e.user_id= :userId 
        GROUP BY TO_CHAR(e.expense_date,'YYYY-MM') 
        ORDER BY TO_CHAR(e.expense_date,'YYYY-MM')
        """,nativeQuery = true)
    List<MonthlyExpenseSummary> getMonthWiseSummary(@Param("userId") Long userId);
}