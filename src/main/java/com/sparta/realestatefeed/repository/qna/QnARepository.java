package com.sparta.realestatefeed.repository.qna;

import com.sparta.realestatefeed.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnARepository extends JpaRepository<QnA, Long>, QnAJpaRepository {

    List<QnA> findByApartId(Long apartId);
}
