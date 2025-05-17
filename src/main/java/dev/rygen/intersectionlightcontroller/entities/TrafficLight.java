package dev.rygen.intersectionlightcontroller.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tlight")
public class TrafficLight {

    // Traffic light ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tlight_id")
    private int id;

    // Creating a link to the intersection and allowing multiple traffic lights to be associated with one intersection
    @ManyToOne
    @JoinColumn(name = "intersection_id")
    @JsonBackReference
    private Intersection intersection;

    // Light attributes
    @Column(name = "color")
    private Colors currColor;

    private int gTime;
    private int yTime;
    private int rTime;

    @Column(name = "is_active")
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    private String road;
    private String direction;

    public int getId() {
        return id;
    }

    public Colors getColor() {
        return currColor;
    }

    

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
