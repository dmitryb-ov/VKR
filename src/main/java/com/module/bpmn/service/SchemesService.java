package com.module.bpmn.service;

import com.module.bpmn.model.Schemes;
import com.module.bpmn.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public interface SchemesService {
    Schemes store(MultipartFile file, User user) throws IOException;
    Optional<Schemes> getFile(Integer id);
    Stream<Schemes> getAllFiles();

    void delete(Schemes schemes);
}
