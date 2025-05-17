package dev.rygen.intersectionlightcontroller.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficLightDTO {
    private int id;
    private String color;
    private int gTime;
    private int yTime;
    private int rTime;
    private boolean isActive;
    private String road;
    private String direction;
    private int intersectionId;
}

