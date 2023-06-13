package com.module.bpmn.service.impl;

import com.module.bpmn.model.Schemes;
import com.module.bpmn.model.User;
import com.module.bpmn.repositories.SchemesRepository;
import com.module.bpmn.service.SchemesService;
import com.module.bpmn.util.SchemaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SchemeServiceImpl implements SchemesService {

    private final SchemesRepository schemesRepository;

    public SchemeServiceImpl(SchemesRepository schemesRepository) {
        this.schemesRepository = schemesRepository;
    }

    @Override
    public Schemes store(MultipartFile file, User user) throws IOException {
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = SchemaUtil.createSpecifiedFileName(file);
        Schemes schemes = new Schemes(fileName, file.getContentType(), file.getBytes(), user, new Date(), file.getSize());
        return schemesRepository.save(schemes);
    }

    @Override
    public Optional<Schemes> getFile(Integer id) {
        return schemesRepository.findById(id);
    }

    @Override
    public Stream<Schemes> getAllFiles() {
        return schemesRepository.findAll().stream();
    }

    @Override
    public void delete(Schemes schemes) {
        schemesRepository.delete(schemes);
    }
}
