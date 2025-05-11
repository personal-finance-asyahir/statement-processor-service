package com.asyahir.statementprocessorservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StatementData {
    private String date;
    private String description;
    private String amount;

    @Override
    public String toString() {
        return "StatementData{" +
                "date='" + date + '\'' +
                ", description='" + StringUtils.replace(description, "\n", "") + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
