package dev.rygen.intersectionlightcontroller.entities;

public enum Colors {
    GREEN, YELLOW, RED, OFF;

    // To ensure that the next color is always correct in the sequence when cycling
    public Colors next() {
        return switch (this) {
            case GREEN -> YELLOW;
            case YELLOW -> RED;
            case RED -> GREEN;
            case OFF -> GREEN;
        };
    }
}
