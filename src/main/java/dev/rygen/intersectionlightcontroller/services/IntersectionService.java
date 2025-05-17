package dev.rygen.intersectionlightcontroller.services;

import dev.rygen.intersectionlightcontroller.entities.Colors;
import dev.rygen.intersectionlightcontroller.entities.Intersection;
import dev.rygen.intersectionlightcontroller.entities.TrafficLight;
import dev.rygen.intersectionlightcontroller.repositories.IntersectionRepository;
import dev.rygen.intersectionlightcontroller.repositories.LightRepository;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;


@Service
public class IntersectionService {

    private final IntersectionRepository intersectionRepository;
    private final LightRepository lightRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public IntersectionService(IntersectionRepository intersectionRepository, LightRepository lightRepository) {
        this.intersectionRepository = intersectionRepository;
        this.lightRepository = lightRepository;
    }

    public Intersection createIntersection(Intersection intersection) {
        Intersection int1 = intersectionRepository.save(intersection);

        List<TrafficLight> initLights = List.of(
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(3)
            .rTime(4)
            .isActive(false)
            .road("N Main St")
            .direction("North")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(3)
            .rTime(4)
            .isActive(false)
            .road("N Main St")
            .direction("South")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(3)
            .rTime(4)
            .isActive(false)
            .road("College St")
            .direction("East")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(3)
            .rTime(4)
            .isActive(false)
            .road("College St")
            .direction("West")
            .intersection(int1)
            .build()
        );

        lightRepository.saveAll(initLights);
        return int1;
    }

    public void activateLights(int intersectionId) {
        List<TrafficLight> lights = lightRepository.findByIntersection_IntersectionId(intersectionId);

        for (TrafficLight light : lights) {
            if (!light.isActive()) {
                light.setIsActive(true);
                lightRepository.save(light);
                startLightCycle(light);
            }
        }
    }

    private void startLightCycle(TrafficLight light) {
        Runnable cycleTask = new Runnable() {
        private Colors current = light.getCurrColor();

        @Override
        public void run() {
            current = current.next();
            light.setCurrColor(current);
            lightRepository.save(light);

            long delay;
            if (current == Colors.GREEN) {
                delay = light.getGTime();
            } else if (current == Colors.YELLOW) {
                delay = light.getYTime();
            } else {
                delay = light.getRTime();
            }

            scheduler.schedule(this, delay, TimeUnit.SECONDS);
            }
        };

        scheduler.schedule(cycleTask, 0, TimeUnit.SECONDS); // Start immediately
    }

}
