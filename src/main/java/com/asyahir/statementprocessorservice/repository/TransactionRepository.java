package com.asyahir.statementprocessorservice.repository;

import com.asyahir.statementprocessorservice.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

@Deprecated
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
