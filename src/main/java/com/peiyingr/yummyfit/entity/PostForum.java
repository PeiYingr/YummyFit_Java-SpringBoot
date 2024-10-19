package com.peiyingr.yummyfit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "post_forum", schema = "yummyfit",
        indexes = {@Index(name = "POST_FORUM_INDX_0",  columnList="postForumId", unique = false)}
)
public class PostForum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    private Integer postForumId;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String forum;

    @OneToMany(mappedBy = "postForumId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
