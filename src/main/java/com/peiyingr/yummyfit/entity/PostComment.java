package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "post_comment", schema = "yummyfit",
        indexes = {@Index(name = "POST_COMMENT_INDX_0",  columnList="postCommentId", unique = false)}
)
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer postCommentId;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long dateTime;


    @Column(nullable = false, columnDefinition = "text")
    private String commentText;
}
