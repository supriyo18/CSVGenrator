package com.fetchCSV.Tiger.Repository;

import com.fetchCSV.Tiger.dto.CustomDto;
import com.fetchCSV.Tiger.Entity.CustomEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Slice;

import java.util.List;

@Repository
public interface CustomRepository extends JpaRepository<CustomEntity, Long> {
    @Query("SELECT new com.fetchCSV.Tiger.dto.CustomDto(a.id, a.column1, a.column2, b.column1, b.column2, c.column1, c.column2) " +
            "FROM TableA a JOIN TableB b ON a.id = b.id JOIN TableC c ON a.id = c.id")
    Slice<CustomDto> findJoinedData(Pageable pageable);
}
