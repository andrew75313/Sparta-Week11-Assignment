package com.sparta.realestatefeed.repository.apart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.QApart;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.sparta.realestatefeed.entity.QUser.user;

@RequiredArgsConstructor
public class ApartRepositoryImpl implements ApartJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QApart qApart = QApart.apart;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<Apart> findByApartId(Long apartId) {

        Apart apart = jpaQueryFactory.selectFrom(qApart)
                .where(qApart.id.eq(apartId))
                .fetchOne();

        return Optional.ofNullable(apart);
    }

    @Override
    public User findWriter(Long apartId) {

        User user = jpaQueryFactory.selectFrom(qApart)
                .innerJoin(qApart.user, qUser).fetchJoin()
                .where(qApart.id.eq(apartId))
                .fetchOne().getUser();

        return user;
    }
}
