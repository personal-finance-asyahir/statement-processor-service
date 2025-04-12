package com.devsy.statementprocessorservice.reader;

import java.io.File;
import java.util.List;

public interface StatementReader<T> {
    List<T> read(File file);
}
