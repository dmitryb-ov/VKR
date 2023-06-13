package com.module.bpmn.service;

import com.module.bpmn.model.FloorImage;
import com.module.bpmn.model.Schemes;
import com.module.bpmn.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public interface FloorImageService {

    FloorImage store(MultipartFile file, User user) throws IOException;
    Optional<FloorImage> getFile(Integer id);
    Stream<FloorImage> getAllFiles();

    Page<FloorImage> paginationFindAll(String fileName, Pageable pageable);

    void delete(FloorImage floorImage);
}
