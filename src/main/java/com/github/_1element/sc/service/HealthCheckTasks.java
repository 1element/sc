package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.repository.CameraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Scheduler component to perform health checks.
 */
@ConditionalOnProperty(name = "sc.healthcheck.enabled", havingValue = "true")
@Component
public class HealthCheckTasks {

  private final CameraRepository cameraRepository;

  private final PushNotificationService pushNotificationService;

  private final MessageSource messageSource;

  private static final Map<Camera, Status> statusTrackingMap = new HashMap<>();

  @Value("${sc.healthcheck.timeout:10000}")
  private int timeout;

  private enum Status {
    UP, DOWN
  }

  private static final String MESSAGE_PROPERTIES_HEALTHCHECK_TITLE = "healthcheck.title";

  private static final String MESSAGE_PROPERTIES_HEALTHCHECK_MESSAGE = "healthcheck.message";

  private static final Logger LOG = LoggerFactory.getLogger(HealthCheckTasks.class);

  /**
   * Constructs the health check component.
   *
   * @param cameraRepository the camera repository
   * @param pushNotificationService the push notification service
   * @param messageSource the message source used for localization
   */
  @Autowired
  public HealthCheckTasks(final CameraRepository cameraRepository,
                          final PushNotificationService pushNotificationService,
                          final MessageSource messageSource) {
    this.cameraRepository = cameraRepository;
    this.pushNotificationService = pushNotificationService;
    this.messageSource = messageSource;
  }

  /**
   * Perform camera health checks.
   * This will be a simple check if all configured camera hosts are reachable.
   * When a host changes its state (e.g. going from UP to DOWN or vice versa) a push notification will be sent.
   */
  @Scheduled(fixedDelayString = "${sc.healthcheck.interval}")
  public void performCameraHealthChecks() {
    LOG.debug("Health check started.");

    for (final Camera camera : cameraRepository.findAll()) {
      Status currentStatus;
      try {
        final InetAddress inetAddress = InetAddress.getByName(camera.getHost());
        currentStatus = inetAddress.isReachable(timeout) ? Status.UP : Status.DOWN;
        LOG.debug("Health status for host '{}' is: {}", camera.getHost(), currentStatus);
      } catch (final Exception exception) {
        LOG.debug("Could not determine health status for host '{}', exception was: '{}'",
            camera.getHost(), exception.getMessage());
        currentStatus = Status.DOWN;
      }

      final Status previousStatus = statusTrackingMap.get(camera);
      if ((previousStatus != null) && (previousStatus != currentStatus)) {
        LOG.info("Camera health check. Host {} has new status: {}", camera.getHost(), currentStatus);
        final String title = messageSource.getMessage(MESSAGE_PROPERTIES_HEALTHCHECK_TITLE, null,
            LocaleContextHolder.getLocale());
        final String message = messageSource.getMessage(MESSAGE_PROPERTIES_HEALTHCHECK_MESSAGE,
            new Object[]{camera.getName(), camera.getHost(), currentStatus}, LocaleContextHolder.getLocale());
        pushNotificationService.sendMessage(title, message);
      }

      statusTrackingMap.put(camera, currentStatus);
    }

  }

}
