package com.sparta.realestatefeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "QNA")
@NoArgsConstructor
public class QnA extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id", nullable = false)
    private Long qnaId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "apart_id")
    private Apart apart;

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likeList = new ArrayList<>();

    public QnA(String content, User user, Apart apart) {

        this.content = content;
        this.user = user;
        this.apart = apart;
    }

    public void changeContent(String content){
        this.content = content;
    }
}
