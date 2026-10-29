package expensetracker.application.controllers;

import expensetracker.application.dto.*;
import expensetracker.application.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){

        this.expenseService=expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request){
        return new ResponseEntity<>(expenseService.createExpense(request),HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(LocalDate expenseDate, String category, Pageable pageable){
        return new ResponseEntity<>(expenseService.getAllExpenses(expenseDate,category,pageable),HttpStatus.OK);
    }

    //since spring gives httpstatus as ok for the get request
    @GetMapping("/{expenseId}")
    public ExpenseResponse getExpenseById(@PathVariable Long expenseId){
        return expenseService.getExpenseById(expenseId);
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponse> totalSummary(){
        return new ResponseEntity<>(expenseService.totalExpensesAndexpenseCount(),HttpStatus.OK);
    }

    @GetMapping("/summary/category")
    public ResponseEntity<List<CategoryExpenseSummaryResponse>> getCategoryWiseSummary(){
        return new ResponseEntity<>(expenseService.categoryExpenseSummaryResponseList(),HttpStatus.OK);
    }

    @GetMapping("/summary/month")
    public ResponseEntity<List<MonthlyExpenseSummaryResponse>> getMonthWiseSummary(){
        return new ResponseEntity<>(expenseService.monthlyExpenseSummaryResponseList(),HttpStatus.OK);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long expenseId,
                                                        @Valid @RequestBody ExpenseUpdateRequest request){
        return new ResponseEntity<>(expenseService.updateExpense(expenseId,request), HttpStatus.OK);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
