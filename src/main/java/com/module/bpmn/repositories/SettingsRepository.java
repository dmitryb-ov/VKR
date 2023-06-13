package com.module.bpmn.repositories;

import com.module.bpmn.model.Settings;
import com.module.bpmn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    Optional<Settings> findSettingsByUserAndSettingsKey(User user, String constants);

    List<Settings> findSettingsByUser(User user);
}
