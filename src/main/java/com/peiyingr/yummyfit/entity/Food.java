package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Getter
@Setter
@ToString(exclude = "userId")
@Entity
@Table(name = "foods", schema = "yummyfit",
        indexes = {
        @Index(name = "FOOD_INDX_0",  columnList="foodId", unique = false),
        @Index(name = "FOOD_INDX_1",  columnList="name", unique = false)
    }
)
public class Food {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer foodId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)
    private User userId;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name; 

    @Column(nullable = false, columnDefinition = "float")
    private Float kcal; 

    @Column(nullable = false, columnDefinition = "float")
    private Float protein;

    @Column(nullable = false, columnDefinition = "float")
    private Float fat;

    @Column(nullable = false, columnDefinition = "float")
    private Float carbs;
}


