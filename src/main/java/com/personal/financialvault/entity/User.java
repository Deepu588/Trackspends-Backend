package com.personal.financialvault.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private Integer age;

    private String maritalStatus;

    private Double monthlySalary;

    private String employmentDomain;

    private String refreshToken;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerified = false;
}