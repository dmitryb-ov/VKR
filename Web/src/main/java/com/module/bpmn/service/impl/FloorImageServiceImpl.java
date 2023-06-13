package com.module.bpmn.service.impl;

import com.module.bpmn.model.FloorImage;
import com.module.bpmn.model.User;
import com.module.bpmn.repositories.FloorImageRepository;
import com.module.bpmn.service.FloorImageService;
import com.module.bpmn.util.SchemaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FloorImageServiceImpl implements FloorImageService {

    private final FloorImageRepository floorImageRepository;

    public FloorImageServiceImpl(FloorImageRepository floorImageRepository) {
        this.floorImageRepository = floorImageRepository;
    }

    @Override
    public FloorImage store(MultipartFile file, User user) throws IOException {
        String fileName = file.getOriginalFilename();
//        String fileName = SchemaUtil.createSpecifiedFileName(file);
        FloorImage schemes = new FloorImage(fileName, file.getContentType(), file.getBytes(), user, new Date(), file.getSize());
        return floorImageRepository.save(schemes);
    }

    @Override
    public Optional<FloorImage> getFile(Integer id) {
        return floorImageRepository.findById(id);
    }

    @Override
    public Stream<FloorImage> getAllFiles() {
        return floorImageRepository.findAll().stream();
    }

    @Override
    public Page<FloorImage> paginationFindAll(String fileName, Pageable pageable){
        return floorImageRepository.findByFileName(fileName, pageable);
    }

    @Override
    public void delete(FloorImage floorImage) {
        floorImageRepository.delete(floorImage);
    }
}
