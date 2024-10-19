package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@ToString(exclude = {"userId", "intakes"})
@Entity
@Table(name = "meal_record", schema = "yummyfit",
        indexes = {@Index(name = "MEAL_RECORD_INDX_0",  columnList="mealRecordId", unique = false)}
)
public class MealRecord {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer mealRecordId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String date;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String meal;

    @JsonIgnore
    @OneToMany(mappedBy = "mealRecordId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Intake> intakes;

    @OneToMany(mappedBy = "mealRecordId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPhoto> mealPhotos;
}
