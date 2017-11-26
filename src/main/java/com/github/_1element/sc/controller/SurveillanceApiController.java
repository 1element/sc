package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.dto.ImagesCountResult;
import com.github._1element.sc.exception.ResourceNotFoundException;
import com.github._1element.sc.exception.UnsupportedOperationException;
import com.github._1element.sc.repository.PushNotificationSettingRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * RESTful API controller.
 */
@RestController
@RequestMapping(URIConstants.API_ROOT)
public class SurveillanceApiController {

  private SurveillanceImageRepository imageRepository;

  private PushNotificationSettingRepository pushNotificationSettingRepository;

  @Autowired
  public SurveillanceApiController(SurveillanceImageRepository imageRepository,
                                   PushNotificationSettingRepository pushNotificationSettingRepository) {
    this.imageRepository = imageRepository;
    this.pushNotificationSettingRepository = pushNotificationSettingRepository;
  }

  /**
   * Bulk update all recordings.
   * Currently only archived flag set to true is supported.
   *
   * @param surveillanceImage surveillance image with archived flag set to true
   * @throws UnsupportedOperationException exception if unsupported operation is requested
   */
  @PostMapping(URIConstants.API_RECORDINGS)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void bulkUpdateRecordings(@RequestBody SurveillanceImage surveillanceImage)
      throws UnsupportedOperationException {
    boolean isArchived = surveillanceImage.isArchived();
    if (!isArchived) {
      throw new UnsupportedOperationException("Archived flag must be true.");
    }

    imageRepository.updateAllArchiveState(isArchived);
  }

  /**
   * Returns the total amount of (non-archived) recordings.
   *
   * @return images count
   */
  @GetMapping(URIConstants.API_RECORDINGS_COUNT)
  public ImagesCountResult recordingsCount() {
    Long imagesCount = imageRepository.countAllImages();

    return new ImagesCountResult(imagesCount);
  }

  /**
   * Set push notification settings (enable/disable) for a given camera.
   *
   * @param cameraId the camera id to modify the setting for
   * @param pushNotificationSetting the push notification setting
   *
   * @return the updated push notification setting
   */
  @PutMapping(URIConstants.API_PUSH_NOTIFICATION_SETTINGS + "/{cameraId}")
  public PushNotificationSetting updatePushNotificationSetting(@PathVariable String cameraId,
                                                               @RequestBody PushNotificationSetting
                                                                 pushNotificationSetting)
      throws ResourceNotFoundException {

    PushNotificationSetting updatePushNotificationSetting = pushNotificationSettingRepository.findByCameraId(cameraId);
    if (updatePushNotificationSetting == null) {
      throw new ResourceNotFoundException("Resource '" + cameraId + "' not found.");
    }

    updatePushNotificationSetting.setEnabled(pushNotificationSetting.isEnabled());

    return pushNotificationSettingRepository.save(updatePushNotificationSetting);
  }

}
