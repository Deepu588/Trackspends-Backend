package com.personal.financialvault.repository;

import com.personal.financialvault.entity.AutomatedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<AutomatedReport,Long> {
}
