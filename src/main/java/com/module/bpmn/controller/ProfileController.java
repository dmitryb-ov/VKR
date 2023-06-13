package com.module.bpmn.controller;

import com.module.bpmn.model.Settings;
import com.module.bpmn.model.SettingsConstants;
import com.module.bpmn.model.User;
import com.module.bpmn.service.SettingsService;
import com.module.bpmn.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping
@Controller
public class ProfileController {

    private final UserService userService;

    private final SettingsService settingsService;

    public ProfileController(UserService userService, SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    @GetMapping(value = {"/profile"})
    public String getGeneralPage(Authentication authentication, Model model) {
        String currentUser = authentication.getName();
        Optional<User> user = userService.getUserByUsername(currentUser);
        if (user.isPresent()) {
            model.addAttribute("user", user);
        }
        return "profile";
    }

    @GetMapping("/profile-links")
    public String getProfileLinksPage(Authentication authentication, Model model) {
        String currentUser = authentication.getName();
        Optional<User> user = userService.getUserByUsername(currentUser);
        if (user.isPresent()) {
            User currUser = user.get();
//            List<Settings> settingsList = settingsService.getSettingsByUser(currUser);
            Optional<Settings> bpmnPath = settingsService.getSettingsByUserAndKey(currUser, SettingsConstants.BLENDER_PATH);
            Optional<Settings> bpmnSavePath = settingsService.getSettingsByUserAndKey(currUser, SettingsConstants.BLENDER_SAVE_PATH);

            if(bpmnPath.isPresent()){
                model.addAttribute("bpmnPath", bpmnPath.get());
            } else {
                model.addAttribute("bpmnPath", new Settings());
            }
            if(bpmnSavePath.isPresent()){
                model.addAttribute("bpmnSavePath", bpmnSavePath.get());
            } else {
                model.addAttribute("bpmnSavePath", new Settings());
            }

        }
        return "profile-links";
    }

    @PostMapping("/profile-links")
    public String setPathToBPMN(@ModelAttribute("bpmnPath") String bpmnPath,@ModelAttribute("bpmnSavePath") String bpmnSavePath, BindingResult result, Model model, Authentication authentication) {
        String currentUser = authentication.getName();
        Optional<User> user = userService.getUserByUsername(currentUser);
        if (user.isPresent()) {
            User currUser = user.get();
            Optional<Settings> optionalBpmnPathFromDB = settingsService.getSettingsByUserAndKey(currUser, SettingsConstants.BLENDER_PATH);
            Optional<Settings> optionalBpmnSavePathFromDB = settingsService.getSettingsByUserAndKey(currUser, SettingsConstants.BLENDER_SAVE_PATH);

            if(optionalBpmnPathFromDB.isPresent()){
                Settings bpmnPathFromDB = optionalBpmnPathFromDB.get();
                bpmnPathFromDB.setSettingsValue(bpmnPath);
                settingsService.addSetting(bpmnPathFromDB);
            } else {
                Settings newBpmnPath = new Settings();
                newBpmnPath.setSettingsKey(SettingsConstants.BLENDER_PATH.toString());
                newBpmnPath.setSettingsValue(bpmnPath);
                newBpmnPath.setUser(currUser);
                settingsService.addSetting(newBpmnPath);
            }

            if(optionalBpmnSavePathFromDB.isPresent()){
                Settings bpmnSavePathFromDB = optionalBpmnSavePathFromDB.get();
                bpmnSavePathFromDB.setSettingsValue(bpmnSavePath);
                settingsService.addSetting(bpmnSavePathFromDB);
            } else {
                Settings newBpmnSavePath = new Settings();
                newBpmnSavePath.setSettingsKey(SettingsConstants.BLENDER_SAVE_PATH.toString());
                newBpmnSavePath.setSettingsValue(bpmnSavePath);
                newBpmnSavePath.setUser(currUser);
                settingsService.addSetting(newBpmnSavePath);
            }
        }
        return "redirect:/profile-links";
    }
}
