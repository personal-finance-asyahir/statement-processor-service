package com.asyahir.statementprocessorservice.reader;

import java.io.File;
import java.util.List;

abstract class StatementReader<T> {

    final File file;

    public StatementReader(String filepath) {
        try {
            this.file = new File(filepath);
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: implementing better exception catching
        }
    }

    abstract List<T> read();
}
