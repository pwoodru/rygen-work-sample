package dev.rygen.intersectionlightcontroller.entities;

import java.util.List;

public enum Colors {
    GREEN, YELLOW, RED;

    private static final List<Colors> lightColors = List.of(Colors.values());

    public Colors next() {
        return lightColors.get((this.ordinal() + 1) % lightColors.size());
    }
}
