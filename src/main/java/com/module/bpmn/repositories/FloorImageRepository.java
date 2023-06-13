package com.module.bpmn.repositories;

import com.module.bpmn.model.FloorImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloorImageRepository extends JpaRepository<FloorImage, Integer> {
    Page<FloorImage> findByFileName(String fileName, Pageable pageable);
}
