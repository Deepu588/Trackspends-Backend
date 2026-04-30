package com.personal.financialvault.repository;

import com.personal.financialvault.dto.response.CategoryExpenseResponse;
import com.personal.financialvault.dto.response.DailyExpenseResponse;
import com.personal.financialvault.entity.Expense;
import com.personal.financialvault.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {


    List<Expense> findByUser(User user);


    Page<Expense> findByUser(User user, Pageable pageable);
   // Expense save(Expense expense);

    Page<Expense> findByUserAndDateBetween(User user,LocalDate startDate,LocalDate endDate,Pageable pageable);

    @Query("select sum(e.amount) from Expense e where e.user=:user and e.expenseMonth=:expenseMonth and e.expenseYear=:expenseYear")
    Double getUserExpensesInGivenMonth(@Param("user") User user,
                                       @Param("expenseMonth") Integer expenseMonth,
                                       @Param("expenseYear") Integer expenseYear
                                       );

    @Query("select  new com.personal.financialvault.dto.response.DailyExpenseResponse(e.date,(sum(e.amount))*1.0) from Expense e where e.user=:user and e.date between :startDate and :endDate group by e.date order by e.date asc")
    List<DailyExpenseResponse> getTotalExpensePerDayInCurrentWeek(@Param("user") User user,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate")  LocalDate endDate);





    @Query("select new com.personal.financialvault.dto.response.CategoryExpenseResponse(e.category,(sum(e.amount))*1.0) from Expense e where e.user=:user group by e.category ")
    List<CategoryExpenseResponse> getTotalExpensePerCategory(@Param("user") User user);



    @Query("select new com.personal.financialvault.dto.response.DailyExpenseResponse(e.date,(sum(e.amount))*1.0) from Expense e where e.user=:user and e.date=:date")
    DailyExpenseResponse getTodayTotalExpense(@Param("user") User user,@Param("date") LocalDate date);




    }
