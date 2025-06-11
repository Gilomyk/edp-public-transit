package com.example.projectedp.model;

public class Line {
    private final String lineNumber;
    private final String direction; // pojedynczy kierunek, nie zbiór

    public Line(String name, String direction) {
        this.lineNumber = name;
        this.direction = direction;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        if (direction == null) {
            return lineNumber;
        }
        return lineNumber + " → " + direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line that)) return false;
        return lineNumber.equals(that.lineNumber);
    }

    @Override
    public int hashCode() {
        return lineNumber.hashCode();
    }
}

