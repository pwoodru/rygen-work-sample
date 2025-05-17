package dev.rygen.intersectionlightcontroller.services;

import dev.rygen.intersectionlightcontroller.entities.Colors;
import dev.rygen.intersectionlightcontroller.entities.Intersection;
import dev.rygen.intersectionlightcontroller.entities.TrafficLight;
import dev.rygen.intersectionlightcontroller.repositories.IntersectionRepository;
import dev.rygen.intersectionlightcontroller.repositories.LightRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;


@Service
public class IntersectionService {

    private final IntersectionRepository intersectionRepository;
    private final LightRepository lightRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

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
            .yTime(2)
            .rTime(6)
            .isActive(false)
            .road("N Main St")
            .direction("North")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(2)
            .rTime(6)
            .isActive(false)
            .road("N Main St")
            .direction("South")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(2)
            .rTime(6)
            .isActive(false)
            .road("College St")
            .direction("East")
            .intersection(int1)
            .build(),
        TrafficLight.builder()
            .currColor(Colors.OFF)
            .gTime(4)
            .yTime(2)
            .rTime(6)
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
                light.setCurrColor(Colors.OFF);
                light.setIsActive(true);
                
            }
        }
        lightRepository.saveAll(lights);
        startLightCycle(intersectionId, lights);
    }

    public void deactivateLights(int intersectionId) {
        List<TrafficLight> lights = lightRepository.findByIntersection_IntersectionId(intersectionId);

        for (TrafficLight light : lights) {
            // Cancel all scheduled tasks to reset colors and timers
            ScheduledFuture<?> task = scheduledTasks.remove(light.getId());
            if (task != null) {
                task.cancel(true);
            }

            light.setCurrColor(Colors.OFF);
            light.setIsActive(false);
        }
        lightRepository.saveAll(lights);
    }

    private void startLightCycle(int intersectionId, List<TrafficLight> lights) {
    Runnable cycleTask = new Runnable() {
        private boolean nsActive = true;

        @Override
        public void run() {
            List<TrafficLight> northSouth = new ArrayList<>();
            List<TrafficLight> eastWest = new ArrayList<>();

            for (TrafficLight light : lights) {
                switch (light.getDirection().toLowerCase()) {
                    case "north":
                    case "south":
                        northSouth.add(light);
                        break;
                    case "east":
                    case "west":
                        eastWest.add(light);
                        break;
                }
            }

            if (nsActive) {
                updateLights(northSouth, Colors.GREEN);
                updateLights(eastWest, Colors.RED);
                lightRepository.saveAll(lights);

                ScheduledFuture<?> yellowTask = scheduler.schedule(() -> {
                    updateLights(northSouth, Colors.YELLOW);
                    lightRepository.saveAll(lights);
                }, northSouth.get(0).getGTime(), TimeUnit.SECONDS);

                ScheduledFuture<?> nextCycle = scheduler.schedule(this,
                    northSouth.get(0).getGTime() + northSouth.get(0).getYTime(),
                    TimeUnit.SECONDS);

                for (TrafficLight light : northSouth) {
                    scheduledTasks.put(light.getId(), yellowTask);
                    scheduledTasks.put(light.getId(), nextCycle);
                }
            } else {
                updateLights(eastWest, Colors.GREEN);
                updateLights(northSouth, Colors.RED);
                lightRepository.saveAll(lights);

                ScheduledFuture<?> yellowTask = scheduler.schedule(() -> {
                    updateLights(eastWest, Colors.YELLOW);
                    lightRepository.saveAll(lights);
                }, eastWest.get(0).getGTime(), TimeUnit.SECONDS);

                ScheduledFuture<?> nextCycle = scheduler.schedule(this,
                    eastWest.get(0).getGTime() + eastWest.get(0).getYTime(),
                    TimeUnit.SECONDS);

                for (TrafficLight light : eastWest) {
                    scheduledTasks.put(light.getId(), yellowTask);
                    scheduledTasks.put(light.getId(), nextCycle);
                }
            }

            nsActive = !nsActive;
        }
    };

        ScheduledFuture<?> initialTask = scheduler.schedule(cycleTask, 0, TimeUnit.SECONDS);
        for (TrafficLight light : lights) {
            scheduledTasks.put(light.getId(), initialTask);
        }
    }

    private void updateLights(List<TrafficLight> lights, Colors color) {
        for (TrafficLight light : lights) {
            light.setCurrColor(color);
        }
    }

}
