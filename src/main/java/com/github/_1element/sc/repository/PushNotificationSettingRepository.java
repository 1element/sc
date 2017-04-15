package com.github._1element.sc.repository;

import com.github._1element.sc.domain.PushNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for push notification settings.
 */
public interface PushNotificationSettingRepository extends JpaRepository<PushNotificationSetting, Long> {

  PushNotificationSetting findByCameraId(String cameraId);

}
