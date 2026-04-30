package com.personal.financialvault.service;


import com.personal.financialvault.dto.request.MonthlySavingsRequest;
import com.personal.financialvault.dto.response.*;
import com.personal.financialvault.dto.request.ExpensePatchRequest;
import com.personal.financialvault.dto.request.ExpenseRequest;
import com.personal.financialvault.entity.Expense;
import com.personal.financialvault.entity.User;
import com.personal.financialvault.exceptions.ResourceNotFoundException;
import com.personal.financialvault.repository.ExpenseRepository;
import com.personal.financialvault.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
        private final UserRepository userRepository;

    public ApiResponse<?> saveTheExpense(ExpenseRequest expense, UserDetails userDetails){


        User user=    userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(()->new ResourceNotFoundException("User Not Found"));

        Expense e=Expense
                .builder()
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .date(expense.getDate())
                .user(user)
                .build();

     Expense savedExpense=   expenseRepository.save(e);


        return ApiResponse.builder()
                .success(true)
                .message("Expense Added Successfully !! ")
                .data(new ExpenseResponse(savedExpense))
                .build();
    }




    public ApiResponse<?> getAllExpensesOfUser(UserDetails userDetails){

   User user=   userRepository.findByEmail(  userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User not Found"));

      List<Expense> e=  expenseRepository.findByUser(user);
        List<ExpenseResponse> allExpensesOfUser =
                e.stream()
                        .map(ExpenseResponse::new)
                        .toList();

        return  ApiResponse.builder()
                .success(true)
                .message("Loaded All Expenses of the User Successfully !!")
                .data(allExpensesOfUser)
                .build();



    }


    public ExpenseResponseWithPagination<?> getAllTheExpensesOfUser(int page, int size, UserDetails userDetails){


       User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User not Found"));

         Pageable pageable=   PageRequest.of(page,size);
         Page<Expense> expenses=   expenseRepository.findByUser(user,pageable);

         ExpenseResponseWithPagination<ExpenseResponse>   customizedResponse=       new ExpenseResponseWithPagination<>();

                List<ExpenseResponse>   expenseResponseList=    expenses.getContent().stream().map(ExpenseResponse::new).toList();
        System.out.println(expenseResponseList+" -----------");
        customizedResponse.setData(expenseResponseList);
        customizedResponse.setTotalPages(expenses.getTotalPages());
        customizedResponse.setTotalElements(expenses.getTotalElements());

        String baseUrl="http://localhost:8080/api/expense/getAllExpense";
        String nextPage=expenses.hasNext()?baseUrl+"?page="+(page+1)+"&size="+size:null;
        String previousPage=expenses.hasPrevious()?baseUrl+"?page="+(page-1)+"&size="+size:null;

        customizedResponse.setPreviousPage(previousPage);
        customizedResponse.setNextPage(nextPage);


       // expenses.getTotalElements();
        //xpenses.getTotalPages();

       // System.out.println(l+" -----------");
       //     System.out.println(expenses.getTotalElements());
        //System.out.println(expenses.getTotalPages());
        //System.out.println(expenses.hasNext());


             return customizedResponse;

    }


    public ExpenseResponseWithPagination<?> getExpensesWithinRange(LocalDate startDate, LocalDate endDate,int page,int size,UserDetails user){

            User userinfo=            userRepository.findByEmail(user.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
              Pageable pageable   =   PageRequest.of(page,size);
               Page<Expense>   expensesWithinRange=      expenseRepository.findByUserAndDateBetween(userinfo,startDate,endDate,pageable);
                  ExpenseResponseWithPagination<ExpenseResponse>      customizedResponse =    new ExpenseResponseWithPagination<>();


        List<  ExpenseResponse  >      expensesRange=expensesWithinRange.getContent().stream().map(ExpenseResponse::new).toList();
       customizedResponse.setData(expensesRange);
       customizedResponse.setTotalElements(expensesWithinRange.getTotalElements());
       customizedResponse.setTotalPages(expensesWithinRange.getTotalPages());
        String baseUrl="http://localhost:8080/api/expense/getExpensesInRange?startDate="+startDate+"&endDate="+endDate;
        String nextPage=expensesWithinRange.hasNext()?baseUrl+"&page="+(page+1)+"&size="+size:null;
        String previousPage=expensesWithinRange.hasPrevious()?baseUrl+"&page="+(page-1)+"&size="+size:null;
        customizedResponse.setPreviousPage(previousPage);
        customizedResponse.setNextPage(nextPage);

        System.out.println(customizedResponse);


        return customizedResponse;
                //ApiResponse.builder().success(true).message("Extracted expense details in given range").data(expensesRange).build();
    }




    public ApiResponse<?> getExpenseById(Long id,UserDetails userDetails){

        User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not found"));

        Expense expense=  expenseRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Expense Not Found"));

        if(! expense.getUser().getUserId().equals(user.getUserId())){
            throw  new RuntimeException("Unauthorized ");
        }

        return ApiResponse
                .builder()
                .success(true)
                .message("Loaded the Expense Details Successfully !! ")
                .data(new ExpenseResponse(expense))
                .build();
    }


        @Transactional
            public ApiResponse<?> updateExpenseById(Long id, UserDetails userDetails, ExpensePatchRequest request){

                 User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not found"));

                  Expense expense=  expenseRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Expense Not Found"));

                 if(! expense.getUser().getUserId().equals(user.getUserId())){
                   throw  new RuntimeException("Unauthorized ");
                 }

                 System.out.println(request);
                if(request.getAmount()!=null){
                    expense.setAmount(request.getAmount());
                    System.out.println("---Done 1");
                }
                if(request.getDescription()!=null){
                    expense.setDescription(request.getDescription());
                    System.out.println("---Done 2");

                }
                if(request.getDate()!=null){
                    expense.setDate(request.getDate());
                    expense.setExpenseMonth(request.getDate().getMonthValue());
                    expense.setExpenseYear(request.getDate().getYear());
                    System.out.println("---Done 3");

                }

                if(request.getCategory()!=null){
                    expense.setCategory(request.getCategory());
                    System.out.println("---Done 4");

                }

             Expense updatedExpense=   expenseRepository.save(expense);
             System.out.println(updatedExpense);
                return  ApiResponse.builder()
                        .success(true)
                        .message("Expense Details Updated Successfully !!")
                        .data(new ExpenseResponse(updatedExpense))
                        .build();

        }




        public ApiResponse<?> deleteExpenseById(Long id,UserDetails userDetails){

            User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not found"));

            Expense expense=  expenseRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Expense Not Found"));

            if(! expense.getUser().getUserId().equals(user.getUserId())){
                new RuntimeException("Unauthorized ");
            }

            expenseRepository.delete(expense);

            return ApiResponse.builder()
                    .success(true)
                    .message("Expense Details deleted Successfully !! ")
                    .build();


        }



        public ApiResponse<?> getSavingsInMonth(MonthlySavingsRequest request,UserDetails userDetails){

    User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not Found"));

    Double salaryInMonth=  request.getSalary();

    Double  sumOfAllExpensesInGivenMonth= expenseRepository.getUserExpensesInGivenMonth(user, request.getExpenseMonth(), request.getExpenseYear());
            if (sumOfAllExpensesInGivenMonth == null) {
                sumOfAllExpensesInGivenMonth = 0.0;
            }
                       HashMap<String,Double> hashMapForResponse=    new HashMap<>();
                       hashMapForResponse.put("salaryInMonth",salaryInMonth);
                       hashMapForResponse.put("sumOfAllExpensesInGivenMonth",sumOfAllExpensesInGivenMonth);
                       hashMapForResponse.put("calculatedSavingsInMonth",(salaryInMonth-sumOfAllExpensesInGivenMonth));

        return  ApiResponse
                .builder()
                .success(true)
                .message("Calculated the savings of user in given month and year based on salary successfully !!")
                .data(hashMapForResponse)
                .build();

        }


        public ApiResponse<?> getCurrentWeekExpenses(UserDetails userDetails){

           User user=     userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not Found"));

           LocalDate currentDate=     LocalDate.now();
           LocalDate startDate=    currentDate.minusDays(7);

                        List<DailyExpenseResponse>    result=    expenseRepository.getTotalExpensePerDayInCurrentWeek(user,startDate,currentDate);
                System.out.println(result);

           return ApiResponse
                   .builder()
                   .success(true)
                   .message("Extracted the amount spent on each day in this current week successfully ")
                   .data(result)
                   .build();


        }


        public ApiResponse<?> getCategoryWiseExpense(UserDetails userDetails){

            User user=    userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not found"));

                    List<CategoryExpenseResponse>  result=  expenseRepository.getTotalExpensePerCategory(user);


                        return ApiResponse
                                .builder()
                                .success(true)
                                .message("Calculated the total expenses per each available category successfully !")
                                .data(result)
                                .build();




        }



        public ApiResponse<?> getTodayTotalExpenses(UserDetails userDetails)
        {

            User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User not Found"));

          LocalDate today=  LocalDate.now();
        DailyExpenseResponse  todayTotalExpenses=  expenseRepository.getTodayTotalExpense(user,today);


      return   ApiResponse
                .builder()
                .success(true)
                .message("Calculated the today's total Expenses successfully ")
                .data(todayTotalExpenses)
                .build();

        }






}
