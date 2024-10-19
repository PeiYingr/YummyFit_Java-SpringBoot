package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "post_photo", schema = "yummyfit",
        indexes = {@Index(name = "POST_PHOTO_INDX_0",  columnList="postPhotoId", unique = false)}
)
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer postPhotoId;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post postId;
    
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String photo;
}
