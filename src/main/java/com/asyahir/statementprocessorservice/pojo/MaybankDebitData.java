package com.asyahir.statementprocessorservice.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MaybankDebitData extends StatementData {
    private String statementBalance;
}
