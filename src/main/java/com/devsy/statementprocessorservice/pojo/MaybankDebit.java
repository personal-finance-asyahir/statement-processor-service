package com.devsy.statementprocessorservice.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MaybankDebit extends Statement {
    private String statementBalance;
}
