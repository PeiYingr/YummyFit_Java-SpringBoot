package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.List;


@Getter
@Setter
@ToString(exclude = "foods")
@Entity
@Table(name = "users", schema = "yummyfit",
        indexes = {@Index(name = "USER_INDX_0",  columnList="userId", unique = false)}
)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer userId; // to be sequence number

    @Column(nullable = false, columnDefinition = "varchar(30)")
    private String name; 

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String email; 

    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String password; 

    @Column(nullable = true, columnDefinition = "varchar(255)")
    private String avatar; 

    @Column(nullable = true, columnDefinition = "int")
    private Integer targetKcal;

    @Column(nullable = true , columnDefinition="int")
    private Integer targetProtein;

    @Column(nullable = true ,  columnDefinition="int")
    private Integer targetFat;

    @Column(nullable = true ,columnDefinition = "int")
    private Integer targetCarbs;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foods;
}