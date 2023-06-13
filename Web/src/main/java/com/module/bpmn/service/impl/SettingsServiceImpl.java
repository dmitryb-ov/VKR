package com.module.bpmn.service.impl;

import com.module.bpmn.model.Settings;
import com.module.bpmn.model.SettingsConstants;
import com.module.bpmn.model.User;
import com.module.bpmn.repositories.SettingsRepository;
import com.module.bpmn.service.SettingsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public List<Settings> getSettingsByUser(User user) {
        return settingsRepository.findSettingsByUser(user);
    }

    @Override
    public Optional<Settings> getSettingsByUserAndKey(User user, SettingsConstants constants) {
        return settingsRepository.findSettingsByUserAndSettingsKey(user, constants.toString());
    }

    @Override
    public List<Settings> getAllUserSettings(Integer userId) {
        return null;
    }

    @Override
    public Settings addSetting(Settings settings) {
        return settingsRepository.save(settings);
    }

    @Override
    public Settings updateSettings(Settings settings) {
        return settingsRepository.save(settings);
    }
}
