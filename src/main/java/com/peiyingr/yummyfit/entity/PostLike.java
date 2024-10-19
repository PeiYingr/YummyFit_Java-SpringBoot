package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "post_like", schema = "yummyfit",
        indexes = {@Index(name = "POST_LIKE_INDX_0",  columnList="postLikeId", unique = false)}
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer postLikeId;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;
}
