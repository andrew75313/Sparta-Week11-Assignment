package com.sparta.realestatefeed.repository.apart;

import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.User;

import java.util.List;
import java.util.Optional;

public interface ApartJpaRepository {

    Optional<Apart> findByApartId(Long apartId);
    User findWriter(Long apartId);
    List<Apart> findByAreaDateDescendingPaginated(String area, int page);
    List<Apart> findByFollowingUsers(List<Long> followingIdList, int page);
}
