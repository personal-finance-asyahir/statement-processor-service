package com.asyahir.statementprocessorservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="maybank_debit")
public class MaybankDebit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "maybank_debit_sequence")
    @SequenceGenerator(name = "maybank_debit_sequence", sequenceName = "maybank_debit_id_seq", allocationSize = 1)
    private Long id;
    private UUID userId;
    private Double amount;
    private Double statementBalance;
    private String description;
    private char operation;
    private LocalDate transactionDate;
    private LocalDateTime insertedDateTime;
}
