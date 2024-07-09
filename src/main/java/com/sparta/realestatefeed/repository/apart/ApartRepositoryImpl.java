package com.sparta.realestatefeed.repository.apart;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.QApart;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.User;
import lombok.RequiredArgsConstructor;

import javax.management.Query;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Apart> findByAreaDateDescendingPaginated(String area, int page) {

        JPAQuery<Apart> query = jpaQueryFactory.selectFrom(qApart)
                .orderBy(qApart.createdAt.desc());

        if (area != null) {
            query.where(qApart.area.eq(area));
        }

        List<Apart> apartList = query.offset(page * 5)
                .limit(5)
                .fetch();

        return apartList;
    }

    @Override
    public List<Apart> findByFollowingUsers(List<Long> followingIdList, int page) {

        List<Apart> apartList = jpaQueryFactory.selectFrom(qApart)
                .where(qApart.user.id.in(followingIdList))
                .orderBy(qApart.createdAt.desc())
                .offset(page * 5)
                .limit(5)
                .fetch();

        return apartList;
    }
}
