package expensetracker.application.services;

import expensetracker.application.config.CustomUserDetails;
import expensetracker.application.dto.*;
import expensetracker.application.entities.Expense;
import expensetracker.application.entities.User;
import expensetracker.application.exceptions.ForbiddenException;
import expensetracker.application.exceptions.ResourceNotFoundException;
import expensetracker.application.repositories.ExpenseRepository;
import expensetracker.application.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createExpenseSuccessfully(){
        User user=new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        CustomUserDetails userDetails=new CustomUserDetails(user);
        Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ExpenseRequest request=new ExpenseRequest();
        request.setAmount(new BigDecimal("500.00"));
        request.setCategory("FOOD");
        request.setDescription("LUNCH");
        request.setExpenseDate(LocalDate.of(2026,10,28));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse result=expenseService.createExpense(request);

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"),result.getAmount());
        assertEquals("FOOD",result.getCategory());
        assertEquals("LUNCH",result.getDescription());

        verify(expenseRepository,times(1)).save(any(Expense.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void getExpenseByIdWhenExpenseBelongsToAnotherUser(){
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setName("User A");
        currentUser.setEmail("usera@example.com");

        CustomUserDetails userDetails=new CustomUserDetails(currentUser);

        Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User anotherUser=new User();
        anotherUser.setId(2L);
        anotherUser.setName("User B");
        anotherUser.setEmail("userb@example.com");

        Expense expense=new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("Food");
        expense.setDescription("Lunch");
        expense.setUser(anotherUser);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        assertThrows(ForbiddenException.class,()->
        {
            expenseService.getExpenseById(10L);
        });

        verify(expenseRepository).findById(10L);

        SecurityContextHolder.clearContext();
    }


    @Test
    void getExpenseByIdWhenExpenseDoesNotExist() {

        when(expenseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.getExpenseById(999L);
        });

        verify(expenseRepository).findById(999L);
    }


    @Test
    void getExpenseByIdSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("FOOD");
        expense.setDescription("Lunch");
        expense.setExpenseDate(LocalDate.of(2026, 10, 28));
        expense.setUser(user);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        ExpenseResponse result = expenseService.getExpenseById(10L);

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals("FOOD", result.getCategory());
        assertEquals("Lunch", result.getDescription());

        verify(expenseRepository).findById(10L);

        SecurityContextHolder.clearContext();
    }


    @Test
    void updateExpenseSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("FOOD");
        expense.setDescription("Lunch");
        expense.setExpenseDate(LocalDate.of(2026, 10, 28));
        expense.setUser(user);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseUpdateRequest request = new ExpenseUpdateRequest();
        request.setAmount(new BigDecimal("750.00"));
        request.setCategory("TRAVEL");
        request.setDescription("Taxi");
        request.setExpenseDate(LocalDate.of(2026, 10, 27));

        ExpenseResponse result = expenseService.updateExpense(10L, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("750.00"), result.getAmount());
        assertEquals("TRAVEL", result.getCategory());
        assertEquals("Taxi", result.getDescription());
        assertEquals(LocalDate.of(2026, 10, 27), result.getExpenseDate());

        verify(expenseRepository).findById(10L);
        verify(expenseRepository).save(any(Expense.class));

        SecurityContextHolder.clearContext();
    }


    @Test
    void updateExpenseWhenExpenseBelongsToAnotherUser() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("usera@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("userb@example.com");

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("FOOD");
        expense.setDescription("Lunch");
        expense.setUser(anotherUser);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        ExpenseUpdateRequest request = new ExpenseUpdateRequest();
        request.setAmount(new BigDecimal("750.00"));
        request.setCategory("TRAVEL");
        request.setDescription("Taxi");
        request.setExpenseDate(LocalDate.of(2026, 10, 28));

        assertThrows(ForbiddenException.class, () -> {
            expenseService.updateExpense(10L, request);
        });

        verify(expenseRepository).findById(10L);
        verify(expenseRepository, never()).save(any(Expense.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteExpenseSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("FOOD");
        expense.setDescription("Lunch");
        expense.setUser(user);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(10L);

        verify(expenseRepository).findById(10L);
        verify(expenseRepository).delete(expense);
        verify(expenseRepository, never()).save(any(Expense.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteExpenseWhenExpenseBelongsToAnotherUser() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("usera@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("userb@example.com");

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("500.00"));
        expense.setCategory("FOOD");
        expense.setDescription("Lunch");
        expense.setUser(anotherUser);

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        assertThrows(ForbiddenException.class, () -> {
            expenseService.deleteExpense(10L);
        });

        verify(expenseRepository).findById(10L);
        verify(expenseRepository, never()).delete(any(Expense.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteExpenseWhenExpenseDoesNotExist() {
        when(expenseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.deleteExpense(999L);
        });

        verify(expenseRepository).findById(999L);
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void getExpenseSummarySuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(expenseRepository.calculateTotalExpenses(1L))
                .thenReturn(new BigDecimal("2500.00"));
        when(expenseRepository.countByUserId(1L))
                .thenReturn(5L);

        ExpenseSummaryResponse result =
                expenseService.totalExpensesAndexpenseCount();

        assertNotNull(result);
        assertEquals(new BigDecimal("2500.00"), result.getTotalExpenses());
        assertEquals(5L, result.getExpenseCount());

        verify(expenseRepository).calculateTotalExpenses(1L);
        verify(expenseRepository).countByUserId(1L);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getExpenseSummaryWhenNoExpensesExist() {
        User user = new User();
        user.setId(1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(expenseRepository.calculateTotalExpenses(1L))
                .thenReturn(null);
        when(expenseRepository.countByUserId(1L))
                .thenReturn(0L);

        ExpenseSummaryResponse result =
                expenseService.totalExpensesAndexpenseCount();

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(0L, result.getExpenseCount());

        verify(expenseRepository).calculateTotalExpenses(1L);
        verify(expenseRepository).countByUserId(1L);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getCategoryWiseSummarySuccessfully() {
        User user = new User();
        user.setId(1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<CategoryExpenseSummaryResponse> summaries = List.of(
                new CategoryExpenseSummaryResponse(
                        "FOOD", new BigDecimal("1500.00")),
                new CategoryExpenseSummaryResponse(
                        "TRAVEL", new BigDecimal("800.00"))
        );

        when(expenseRepository.getCategoryWiseSummary(1L))
                .thenReturn(summaries);

        List<CategoryExpenseSummaryResponse> result =
                expenseService.categoryExpenseSummaryResponseList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("FOOD", result.get(0).getCategory());
        assertEquals(new BigDecimal("1500.00"),
                result.get(0).getTotalAmount());
        assertEquals("TRAVEL", result.get(1).getCategory());
        assertEquals(new BigDecimal("800.00"),
                result.get(1).getTotalAmount());

        verify(expenseRepository).getCategoryWiseSummary(1L);

        SecurityContextHolder.clearContext();
    }

    @Test
    void getMonthlyExpenseSummarySuccessfully() {
        User user = new User();
        user.setId(1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MonthlyExpenseSummaryResponse.MonthlyExpenseSummary summary1 = new MonthlyExpenseSummaryResponse.MonthlyExpenseSummary() {
            public String getMonth() {
                return "2026-09";
            }

            public BigDecimal getTotalAmount() {
                return new BigDecimal("2500.00");
            }
        };

        MonthlyExpenseSummaryResponse.MonthlyExpenseSummary summary2 = new MonthlyExpenseSummaryResponse.MonthlyExpenseSummary() {
            public String getMonth() {
                return "2026-10";
            }

            public BigDecimal getTotalAmount() {
                return new BigDecimal("1800.00");
            }
        };

        when(expenseRepository.getMonthWiseSummary(1L))
                .thenReturn(List.of(summary1, summary2));

        List<MonthlyExpenseSummaryResponse> result =
                expenseService.monthlyExpenseSummaryResponseList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("2026-09", result.get(0).getMonth());
        assertEquals(new BigDecimal("2500.00"), result.get(0).getTotalAmount());
        assertEquals("2026-10", result.get(1).getMonth());
        assertEquals(new BigDecimal("1800.00"), result.get(1).getTotalAmount());

        verify(expenseRepository).getMonthWiseSummary(1L);

        SecurityContextHolder.clearContext();
    }
}
