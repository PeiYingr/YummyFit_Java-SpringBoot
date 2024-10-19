package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.*;

@Getter
@Setter
@ToString(exclude = {"foodId", "mealRecordId"})
@Entity
@Table(name = "intake", schema = "yummyfit",
        indexes = {@Index(name = "INTAKE_INDX_0",  columnList="intakeId", unique = false)}
)
public class Intake {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer intakeId;

    @ManyToOne
    @JoinColumn(name = "mealRecordId", nullable = false)
    private MealRecord mealRecordId;

    @ManyToOne
    @JoinColumn(name = "foodId", nullable = false)
    private Food foodId;

    @Column(nullable = false, columnDefinition = "float")
    private Float amount;
}
