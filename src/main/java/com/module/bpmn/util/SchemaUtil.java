package com.module.bpmn.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class SchemaUtil {

    public static String createSpecifiedFileName(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String[] fileNameArr = fileName.split("\\.");
        return fileNameArr[0] + DateUtil.getCurrDate() + "." + fileNameArr[1];
    }
}
