package dev.rygen.intersectionlightcontroller.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import dev.rygen.intersectionlightcontroller.entities.Colors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntersectionDTO {
    private int id;
    private Colors activeLight;
    private List<TrafficLightDTO> trafficLights;
}

