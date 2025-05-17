package dev.rygen.intersectionlightcontroller.controllers;

import dev.rygen.intersectionlightcontroller.dtos.IntersectionDTO;
import dev.rygen.intersectionlightcontroller.dtos.TrafficLightDTO;
import dev.rygen.intersectionlightcontroller.entities.Intersection;
import dev.rygen.intersectionlightcontroller.entities.TrafficLight;
import dev.rygen.intersectionlightcontroller.services.IntersectionService;
import dev.rygen.intersectionlightcontroller.repositories.IntersectionRepository;
import dev.rygen.intersectionlightcontroller.repositories.LightRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/intersections")
public class IntersectionController {

    private final IntersectionService intersectionService;
    private final IntersectionRepository intersectionRepository;
    private final LightRepository lightRepository;

    public IntersectionController(IntersectionService intersectionService, IntersectionRepository intersectionRepository, LightRepository lightRepository) {
        this.intersectionService = intersectionService;
        this.intersectionRepository = intersectionRepository;
        this.lightRepository = lightRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createIntersection(@RequestBody IntersectionDTO intersectionDto) {
        Intersection intersection = Intersection.builder()
            .activeLight(intersectionDto.getActiveLight())
            .build();
        Intersection saved = this.intersectionService.createIntersection(intersection);

        return ResponseEntity.ok(Map.of("id", saved.getIntersectionId()));
    }



    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateIntersection(@PathVariable("id") int id) {
        intersectionService.activateLights(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntersectionDTO> getIntersection(@PathVariable("id") int id) {
        Intersection intersection = intersectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Intersection not found"));

        List<TrafficLight> lights = lightRepository.findByIntersection_IntersectionId(id);

        List<TrafficLightDTO> lightDtos = new ArrayList<>();
        for (TrafficLight light : lights) {
            lightDtos.add(
                TrafficLightDTO.builder()
                    .id(light.getId())
                    .color(light.getCurrColor().name())
                    .gTime(light.getGTime())
                    .yTime(light.getYTime())
                    .rTime(light.getRTime())
                    .isActive(light.isActive())
                    .road(light.getRoad())
                    .direction(light.getDirection())
                    .intersectionId(intersection.getIntersectionId())
                    .build()
            );
        }

        IntersectionDTO dto = IntersectionDTO.builder()
            .id(intersection.getIntersectionId())
            .activeLight(intersection.getActiveLight())
            .trafficLights(lightDtos)
            .build();

        return ResponseEntity.ok(dto);
    }

}
