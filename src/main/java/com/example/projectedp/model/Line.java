package com.example.projectedp.model;

public class Line {
    private String lineNumber;

    public Line(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return lineNumber;
    }
}

