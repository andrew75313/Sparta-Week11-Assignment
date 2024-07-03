package com.sparta.realestatefeed.repository.qna;

import com.sparta.realestatefeed.entity.QnA;
import com.sparta.realestatefeed.entity.User;

import java.util.List;
import java.util.Optional;

public interface QnAJpaRepository {

    Optional<QnA> findByQnaId(Long qnaId);
    User findWriter(Long qnaId);
    List<QnA> findByApartIdDescendingPaginated(Long apartId, int page);
}
