package com.personal.financialvault.controller;

import com.personal.financialvault.dto.request.MonthlySavingsRequest;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.dto.request.ExpensePatchRequest;
import com.personal.financialvault.dto.request.ExpenseRequest;
import com.personal.financialvault.dto.response.ExpenseResponse;
import com.personal.financialvault.dto.response.ExpenseResponseWithPagination;
import com.personal.financialvault.entity.Expense;
import com.personal.financialvault.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/expense")
public class ExpenseController {

private final ExpenseService expenseService;

    @PostMapping("/saveExpense")
    public ResponseEntity<ApiResponse<?>> addExpense(@Valid @RequestBody ExpenseRequest expenseRequest,
                                                     @AuthenticationPrincipal UserDetails userDetails){

    return ResponseEntity.status(201).body(expenseService.saveTheExpense(expenseRequest,userDetails));

    }

    @GetMapping("/getAllExpenses")
    public ResponseEntity<ApiResponse<?>> getAllExpensesOfUser(@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.status(200).body(expenseService.getAllExpensesOfUser(userDetails));


    }

    @GetMapping("/getExpense/{id}")
    public ResponseEntity<ApiResponse<?>> getExpenseById( @PathVariable("id") Long expenseId,
                                                          @AuthenticationPrincipal UserDetails userDetails
                                                          ){

        return ResponseEntity.status(200).body(expenseService.getExpenseById(expenseId,userDetails));
    }


    @GetMapping("/getAllExpense")
    public ResponseEntity<ExpenseResponseWithPagination<?>> getAllExpensesOfUserWithPages(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "3") int size,
                                                                                          @AuthenticationPrincipal UserDetails userDetails
                                                            ){


        return ResponseEntity.status(200).body(expenseService.getAllTheExpensesOfUser(page,size,userDetails));


    }

    @GetMapping("/getExpensesInRange")
    public ResponseEntity<ExpenseResponseWithPagination<?>> getExpenseWithInRange(@RequestParam("startDate")LocalDate startDate,
                                                                 @RequestParam("endDate")LocalDate endDate,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @AuthenticationPrincipal UserDetails userDetails

                                                                 ){
        return ResponseEntity.status(200).body(expenseService.getExpensesWithinRange(startDate,endDate,page,size,userDetails));
    }


    @PostMapping("/getMonthlySavings")
    public ResponseEntity<ApiResponse<?>> getSavingsInDefinedMonth(@Valid @RequestBody MonthlySavingsRequest request,
                                                                   @AuthenticationPrincipal UserDetails userDetails
                                                                   ){
        return ResponseEntity.status(200).body(expenseService.getSavingsInMonth(request,userDetails));
    }



    @PatchMapping("/updateExpense/{id}")
    public ResponseEntity<ApiResponse<?>> updateExpenseOfUser(@PathVariable("id") Long expenseId  ,
                                                              @AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody ExpensePatchRequest request){

        return ResponseEntity
                .status(200)
                .body(expenseService.updateExpenseById(expenseId,userDetails,request));
    }


    @DeleteMapping("/deleteExpense/{id}")
    public ResponseEntity<ApiResponse<?>> deleteExpenseOfUser(@PathVariable("id") Long expenseId,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(204).body(expenseService.deleteExpenseById(expenseId,userDetails));
    }


    @GetMapping("/amountSpentInCurrentWeek")
    public ResponseEntity<ApiResponse<?>> getTotalAmountSpentInEachDayInCurrentWeek(@AuthenticationPrincipal UserDetails userDetails ){

        return ResponseEntity.status(200).body(expenseService.getCurrentWeekExpenses(userDetails));
    }


    @GetMapping("/totalAmountSpentPerCategory")
    public ResponseEntity<ApiResponse<?>> getTotalExpensesPerAvailableCategories(@AuthenticationPrincipal UserDetails userDetails){


        return ResponseEntity.status(200).body(expenseService.getCategoryWiseExpense(userDetails));
    }

    @GetMapping("/todayTotalExpenses")
    public ResponseEntity<ApiResponse<?>> getTodayTotalExpenses(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(200).body(expenseService.getTodayTotalExpenses(userDetails));
    }

}
