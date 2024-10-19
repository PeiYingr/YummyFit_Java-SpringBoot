package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "posts", schema = "yummyfit",
        indexes = {@Index(name = "POST_INDX_0",  columnList="postId", unique = false)}
)
public class Post {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "postForumId", nullable = false)
    private PostForum postForumId;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long dateTime;

    @Column(nullable = false, columnDefinition = "text")
    private String postText;

    @Column(nullable = true, columnDefinition = "varchar(255)")
    private String location;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostPhoto> postPhotos;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> postComments;
}
