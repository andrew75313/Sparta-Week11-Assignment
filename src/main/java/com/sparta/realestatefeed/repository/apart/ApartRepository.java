package com.sparta.realestatefeed.repository.apart;

import com.sparta.realestatefeed.entity.Apart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApartRepository extends JpaRepository<Apart, Long>, ApartJpaRepository{

    Page<Apart> findByArea(String area, Pageable pageable);
}
