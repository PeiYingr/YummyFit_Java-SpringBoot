package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "meal_photo", schema = "yummyfit",
        indexes = {@Index(name = "MEAL_PHOTO_INDX_0",  columnList="mealPhotoId", unique = false)}
)
public class MealPhoto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer mealPhotoId;

    @ManyToOne
    @JoinColumn(name = "mealRecordId", nullable = false)
    private MealRecord mealRecordId;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String photo;
}
