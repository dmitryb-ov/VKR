package com.module.bpmn.service;

import com.module.bpmn.model.Settings;
import com.module.bpmn.model.SettingsConstants;
import com.module.bpmn.model.User;

import java.util.List;
import java.util.Optional;


public interface SettingsService {

    List<Settings> getSettingsByUser(User user);

    Optional<Settings> getSettingsByUserAndKey(User user, SettingsConstants constants);

    List<Settings> getAllUserSettings(Integer userId);

    Settings addSetting(Settings settings);

    Settings updateSettings(Settings settings);
}
