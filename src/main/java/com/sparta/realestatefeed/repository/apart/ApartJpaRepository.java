package com.sparta.realestatefeed.repository.apart;

import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.User;

import java.util.Optional;

public interface ApartJpaRepository {

    Optional<Apart> findByApartId(Long apartId);
    User findWriter(Long apartId);
}
