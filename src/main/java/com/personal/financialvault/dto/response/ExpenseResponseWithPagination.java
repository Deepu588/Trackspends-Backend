package com.personal.financialvault.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseWithPagination<T> {


    private Long totalElements;

    private int totalPages;

    private String nextPage;

    private String previousPage;

    private List<T> data;




}
