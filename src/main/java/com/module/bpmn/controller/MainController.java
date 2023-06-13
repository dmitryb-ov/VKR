package com.module.bpmn.controller;

import com.module.bpmn.model.FloorImage;
import com.module.bpmn.model.Settings;
import com.module.bpmn.model.SettingsConstants;
import com.module.bpmn.model.User;
import com.module.bpmn.repositories.FloorImageRepository;
import com.module.bpmn.service.FloorImageService;
import com.module.bpmn.service.SettingsService;
import com.module.bpmn.service.UserService;
import com.module.bpmn.socket.Client;
import com.module.bpmn.socket.SocketConstants;
import com.module.bpmn.util.ImageUtil;
import com.module.bpmn.util.UserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping
@Controller
public class MainController {

    private final FloorImageRepository floorImageRepository;
    private final UserService userService;
    private final SettingsService settingsService;

    private final FloorImageService floorImageService;

    public MainController(SettingsService settingsService, UserService userService, FloorImageService floorImageService, FloorImageRepository floorImageRepository) {
        this.settingsService = settingsService;
        this.userService = userService;
        this.floorImageService = floorImageService;
        this.floorImageRepository = floorImageRepository;
    }

    @GetMapping("/")
    public String homePage(Model model,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "3") int size) {
        try {
            List<FloorImage> floorImageList = new ArrayList<>();
            Pageable paging = PageRequest.of(page - 1, size);
            Page<FloorImage> pageFloorImage;
            if (keyword == null) {
                pageFloorImage = floorImageRepository.findAll(paging);
            } else {
                pageFloorImage = floorImageService.paginationFindAll(keyword, paging);
                model.addAttribute("keyword", keyword);
            }

            floorImageList = pageFloorImage.getContent();
            model.addAttribute("floorImage", floorImageList);
            model.addAttribute("currentPage", pageFloorImage.getNumber() + 1);
            model.addAttribute("totalFloorImages", pageFloorImage.getTotalElements());
            model.addAttribute("totalPages", pageFloorImage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("imgUtil", new ImageUtil());
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "home";
    }

    @GetMapping("/socketconnect")
    public String socketConnect() {
        Client client = new Client();
        client.sendMessage(SocketConstants.CONNECTED, "CONNECTED");
        return "redirect:/";
    }

    @GetMapping("/openeditor")
    public String openEditor(Authentication authentication) throws IOException, InterruptedException {
        User user = UserUtil.getUserFromContext(authentication, userService);
        Optional<Settings> settings = settingsService.getSettingsByUserAndKey(user, SettingsConstants.BLENDER_PATH);
        if (settings.isPresent()) {
            String path = settings.get().getSettingsValue();
            Client client = new Client();
            client.sendMessage(SocketConstants.COMMAND_BLENDER_PATH, path);
//            ProcessBuilder builder = new ProcessBuilder(path);
//            Process process = builder.start();
//            try ( BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream())) ) {
//                String line;
//                while ((line = br.readLine()) != null) {
//                    System.out.println(line);
//                }
//            }
//
//            // ждем завершения процесса
//            process.waitFor();
        }
        return "redirect:/";
    }

    @GetMapping("/floorimage/convert")
    public String convert(@Param("id") Integer id, Authentication authentication) {
        User user = UserUtil.getUserFromContext(authentication, userService);
        Optional<FloorImage> floorImageOpt = floorImageService.getFile(id);
        Optional<Settings> settings = settingsService.getSettingsByUserAndKey(user, SettingsConstants.BLENDER_SAVE_PATH);
        if (settings.isPresent() & floorImageOpt.isPresent()) {
            FloorImage floorImage = floorImageOpt.get();
            String path = settings.get().getSettingsValue();
            path = path + "\\" + floorImage.getFileName();
            Client client = new Client();
            client.sendMessage(SocketConstants.CONVERT, path);
        }
//        Optional<FloorImage> floorImageOpt = floorImageService.getFile(id);
//        if (floorImageOpt.isPresent()) {
//
//            FloorImage floorImage = floorImageOpt.get();

//            String s = null;
//            Process process = null;
//            try {
//                process = Runtime.getRuntime().exec("python C:\\Users\\dimon\\Downloads\\FloorplanToBlender3d-master\\main.py");
//                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                while ((s = in.readLine()) != null) {
//                    System.out.println(s);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            StringWriter output = new StringWriter();
//            ScriptContext context = new SimpleScriptContext();
//            context.setWriter(output);
//            ScriptEngineManager manager = new ScriptEngineManager();
//            ScriptEngine engine = manager.getEngineByName("python");
//            try {
//                engine.eval(new FileReader("C:\\Users\\dimon\\Downloads\\FloorplanToBlender3d-master\\main.py"), context);
//            } catch (ScriptException e) {
//                throw new RuntimeException(e);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return "redirect:/";
    }

    @PostMapping("/uploadfloor")
    public String uploadScheme(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Authentication authentication) {
        User user = UserUtil.getUserFromContext(authentication, userService);
        try {
            floorImageService.store(file, user);
            attributes.addAttribute("message", "Файл успешно загружен");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

    private String resolvePythonScriptPath(String filename) {
        File file = new File("D:\\BPMN Module/bpmn/src/main/resources/pythonblenderconverter/" + filename);
        return file.getAbsolutePath();
    }
}
