package dev.rygen.intersectionlightcontroller.repositories;

import dev.rygen.intersectionlightcontroller.entities.TrafficLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LightRepository extends JpaRepository<TrafficLight, Integer> {
    List<TrafficLight> findByIntersection_IntersectionId(int intersectionId);
}

