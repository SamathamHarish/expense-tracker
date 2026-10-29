package expensetracker.application.services;

import expensetracker.application.config.CustomUserDetails;
import expensetracker.application.dto.*;
import expensetracker.application.entities.Expense;
import expensetracker.application.entities.User;
import expensetracker.application.exceptions.ForbiddenException;
import expensetracker.application.exceptions.ResourceNotFoundException;
import expensetracker.application.repositories.ExpenseRepository;
import expensetracker.application.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository){
        this.expenseRepository=expenseRepository;
        this.userRepository=userRepository;
    }

    public User getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }


    public ExpenseResponse createExpense(ExpenseRequest request){

        User user=getCurrentUser();

        Expense expense=new Expense();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setUser(user);
        expense.setCreatedAt(LocalDateTime.now());


        Expense savedExpense=expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    public BigDecimal TotalSum(){
        User user=getCurrentUser();
        BigDecimal total=expenseRepository.calculateTotalExpenses(user.getId());
        if(total==null){
            return BigDecimal.ZERO;
        }else{
            return total;
        }
    }

    public Page<ExpenseResponse> getAllExpenses(LocalDate expenseDate,String category, Pageable pageable){

        User user = getCurrentUser();
        Page<Expense> expenses;

        if((category==null || category.isBlank()) && (expenseDate==null)){
            expenses=expenseRepository.findByUserId(user.getId(),pageable);
        }
        else if(expenseDate==null ){
            expenses=expenseRepository.findByUserIdAndCategory(user.getId(),category,pageable);
        }
        else if(category==null || category.isBlank() ){
            expenses=expenseRepository.findByUserIdAndExpenseDate(user.getId(),expenseDate,pageable);
        }
        else {
            expenses = expenseRepository.findByUserIdAndCategoryAndExpenseDate(user.getId(), category, expenseDate , pageable);
        }
        return expenses.map(this::toResponse);
    }

    public ExpenseResponse getExpenseById(Long expenseId){
        Optional<Expense> expenseOptional=expenseRepository.findById(expenseId);

        if(expenseOptional.isEmpty()){
            throw new ResourceNotFoundException("Expense of given id is not present");
        }

        Expense expense=expenseOptional.get();

        User user=getCurrentUser();

        if(!user.getId().equals(expense.getUser().getId())){
            throw new ForbiddenException("Unauthorized access");
        }

        return toResponse(expense);
    }

    public ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
        Optional<Expense> expenseOptional=expenseRepository.findById(expenseId);

        if(expenseOptional.isEmpty()){
            throw new ResourceNotFoundException("Expense of given id is not present");
        }

        Expense expense=expenseOptional.get();

        User user=getCurrentUser();

        if(!user.getId().equals(expense.getUser().getId())){
            throw new ForbiddenException("Unauthorized access");
        }

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());

        Expense saveExpense=expenseRepository.save(expense);

        return toResponse(saveExpense);
    }

    public void deleteExpense(Long expenseId) {
        Optional<Expense> expenseOptional=expenseRepository.findById(expenseId);

        if(expenseOptional.isEmpty()){
            throw new ResourceNotFoundException("Expense with given id does not exists");
        }

        Expense expense=expenseOptional.get();

        User user=getCurrentUser();
        if(!user.getId().equals(expense.getUser().getId())){
            throw new ForbiddenException("Unauthorized access");
        }

        expenseRepository.delete(expense);
    }

    public ExpenseSummaryResponse totalExpensesAndexpenseCount(){
        User user=getCurrentUser();
        BigDecimal totalExpenses=expenseRepository.calculateTotalExpenses(user.getId());
        long expenseCount=expenseRepository.countByUserId(user.getId());
        if(totalExpenses==null){
            totalExpenses=BigDecimal.ZERO;
        }
        return new ExpenseSummaryResponse(totalExpenses,expenseCount);
    }

    public List<CategoryExpenseSummaryResponse> categoryExpenseSummaryResponseList(){
        User user=getCurrentUser();
        return expenseRepository.getCategoryWiseSummary(user.getId());
    }

    public List<MonthlyExpenseSummaryResponse> monthlyExpenseSummaryResponseList(){
        User user=getCurrentUser();
        return expenseRepository.getMonthWiseSummary(user.getId())
                .stream()
                .map(summary -> new MonthlyExpenseSummaryResponse(summary.getMonth(), summary.getTotalAmount()))
                .toList();
    }

    private ExpenseResponse toResponse(Expense expense){
        ExpenseResponse response=new ExpenseResponse();
        response.setId(expense.getId());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory());
        response.setDescription(expense.getDescription());
        response.setExpenseDate(expense.getExpenseDate());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUserId(expense.getUser().getId());

        return response;
    }
}