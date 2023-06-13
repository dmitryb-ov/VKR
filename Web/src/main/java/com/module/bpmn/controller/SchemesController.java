package com.module.bpmn.controller;

import com.module.bpmn.model.Schemes;
import com.module.bpmn.model.Settings;
import com.module.bpmn.model.SettingsConstants;
import com.module.bpmn.model.User;
import com.module.bpmn.service.SchemesService;
import com.module.bpmn.service.SettingsService;
import com.module.bpmn.service.UserService;
import com.module.bpmn.socket.Client;
import com.module.bpmn.socket.SocketConstants;
import com.module.bpmn.util.UserUtil;
import org.camunda.bpm.model.bpmn.impl.BpmnModelInstanceImpl;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SchemesController {

    private final SchemesService schemesService;

    private final UserService userService;

    private final SettingsService settingsService;

    public SchemesController(SchemesService schemesService, UserService userService, SettingsService settingsService) {
        this.schemesService = schemesService;
        this.userService = userService;
        this.settingsService = settingsService;
    }

    @GetMapping("/schemes")
    public String getPage(Authentication authentication, Model model) {
        User user = UserUtil.getUserFromContext(authentication, userService);
        List<Schemes> schemesList = schemesService.getAllFiles()
                .filter(schema -> schema.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        if (!schemesList.isEmpty()) {
            model.addAttribute("schemes", schemesList);
        } else {
            model.addAttribute("schemes", new ArrayList<Schemes>());
        }
        return "schemes";
    }

    @PostMapping("/schemes/upload")
    public String uploadScheme(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Authentication authentication) {
        User user = UserUtil.getUserFromContext(authentication, userService);
        try {
            schemesService.store(file, user);
            attributes.addAttribute("message", "Файл успешно загружен");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/schemes";
    }

    @GetMapping("/schemes/download")
    public void downloadScheme(@Param("id") Integer id, HttpServletResponse response) throws Exception {
        Optional<Schemes> schemes = schemesService.getFile(id);
        if (!schemes.isPresent()) {
            throw new Exception("Файл не найден");
        }

        Schemes currSchema = schemes.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + currSchema.getFileName();

        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(currSchema.getData());
        outputStream.close();

//        BpmnParser parser = new BpmnParser();
//        BpmnParser parser1 = parser.parseModelFromStream(currSchema.getData())
    }

    @GetMapping("/schemes/delete")
    public String deleteScheme(@Param("id") Integer id) throws Exception {
        Optional<Schemes> schemes = schemesService.getFile(id);
        if (!schemes.isPresent()) {
            throw new Exception("Файл не найден");
        } else {
            schemesService.delete(schemes.get());
        }

        return "redirect:/schemes";
    }

    @GetMapping("/schemes/open")
    public String openScheme(@Param("id") Integer id, Authentication authentication){
        Optional<Schemes> schemes = schemesService.getFile(id);
        Optional<Settings> settings = settingsService
                .getSettingsByUserAndKey(UserUtil.getUserFromContext(authentication, userService), SettingsConstants.BLENDER_SAVE_PATH);
        Optional<Settings> optionalExePath = settingsService
                .getSettingsByUserAndKey(UserUtil.getUserFromContext(authentication, userService), SettingsConstants.BLENDER_PATH);
        if(settings.isPresent() && optionalExePath.isPresent()){
            Settings currSettings = settings.get();
            Settings exePathSettings = optionalExePath.get();
            String exePath = exePathSettings.getSettingsValue();
            String path = currSettings.getSettingsValue();
            BpmnParser bpmnParser = new BpmnParser();
            BpmnModelInstanceImpl instance = bpmnParser.parseModelFromStream(new ByteArrayInputStream(schemes.get().getData()));
            System.out.println(instance.getDefinitions().getName());
            instance.getDefinitions().getBpmDiagrams().forEach(s -> System.out.println(s.toString()));
            if(schemes.isPresent()) {
                Client client = new Client();
                client.sendMessage(SocketConstants.COMMAND_BLENDER_SAVE_PATH_OPEN, path + "__" + schemes.get().getFileName() + "__" + exePath);
            }
        }
        return "redirect:/schemes";
    }
}
