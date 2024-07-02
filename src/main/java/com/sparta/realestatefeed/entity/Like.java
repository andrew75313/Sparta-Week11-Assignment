package com.sparta.realestatefeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "likes")
@NoArgsConstructor
public class Like extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "apart_id", nullable = true)
    private Apart apart;

    @ManyToOne
    @JoinColumn(name = "qna_id", nullable = true)
    private QnA qna;

    public Like(User user, Apart apart, QnA qna) {

        this.user = user;
        this.apart = apart;
        this.qna = qna;
    }
}
