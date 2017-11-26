package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.dto.ImagesCameraSummaryResult;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main surveillance service class.
 */
@Service
public class SurveillanceService {

  private SurveillanceImageRepository imageRepository;

  private CameraRepository cameraRepository;

  @Autowired
  public SurveillanceService(SurveillanceImageRepository imageRepository, CameraRepository cameraRepository) {
    this.imageRepository = imageRepository;
    this.cameraRepository = cameraRepository;
  }

  /**
   * Returns page of surveillance images.
   *
   * @param camera      optional camera identifier
   * @param date        optional date
   * @param isArchive   archive flag
   * @param pageRequest page request
   * @return the page of surveillance images
   */
  public Page<SurveillanceImage> getImagesPage(Optional<String> camera, Optional<LocalDate> date, boolean isArchive,
                                               PageRequest pageRequest) {
    LocalDateTime startOfDay = null;
    LocalDateTime endOfDay = null;

    if (date.isPresent()) {
      startOfDay = date.get().atStartOfDay();
      endOfDay = LocalDateTime.of(date.get(), LocalTime.MAX);
    }

    if (camera.isPresent() && StringUtils.isNotBlank(camera.get())) {
      if (date.isPresent()) {
        return imageRepository.findAllForDateRangeAndCameraId(startOfDay, endOfDay, camera.get(), isArchive,
            pageRequest);
      }

      return imageRepository.findAllByCameraIdAndArchived(camera.get(), isArchive, pageRequest);
    }

    if (date.isPresent()) {
      return imageRepository.findAllForDateRange(startOfDay, endOfDay, isArchive, pageRequest);
    }

    return imageRepository.findAllByArchived(isArchive, pageRequest);
  }

  /**
   * Returns most recent image date by extracting given page of surveillance images.
   * This is only wanted if no date filter is present.
   *
   * @param images page of surveillance images
   * @param date   optional date filter
   * @return the most recent image date
   */
  public LocalDateTime getMostRecentImageDate(Page<SurveillanceImage> images, Optional<LocalDate> date) {
    if (!date.isPresent()) {
      return images.getContent().stream().map(SurveillanceImage::getReceivedAt).findFirst().orElse(null);
    }

    return null;
  }

  /**
   * Returns most recent image date by querying database.
   *
   * @return most recent image date
   */
  public LocalDateTime getMostRecentImageDate() {
    List<LocalDateTime> resultList = imageRepository.getMostRecentImageDate(new PageRequest(0, 1));

    return resultList.stream().findFirst().orElse(null);
  }

  /**
   * Returns images summary for each camera.
   *
   * @return the images camera summary result
   */
  public List<ImagesCameraSummaryResult> getImagesCameraSummary() {
    List<ImagesCameraSummaryResult> result = new ArrayList<>();
    PageRequest limitPageRequest = new PageRequest(0, 1);

    List<Camera> cameras = cameraRepository.findAll();

    for (Camera camera : cameras) {
      Long count = imageRepository.countImagesForCamera(camera.getId());
      LocalDateTime mostRecentDate = null;
      if (count > 0) {
        List<LocalDateTime> mostRecentDateList = imageRepository.getMostRecentImageDateForCamera(
            camera.getId(), limitPageRequest);
        mostRecentDate = mostRecentDateList.stream().findFirst().orElse(null);
      }
      result.add(new ImagesCameraSummaryResult(camera, count, mostRecentDate));
    }

    return result;
  }

  /**
   * Returns camera for given identifier, if found.
   *
   * @param camera optional camera identifier
   * @return the camera
   */
  public Camera getCamera(Optional<String> camera) {
    if (camera.isPresent() && StringUtils.isNotBlank(camera.get())) {
      return cameraRepository.findById(camera.get());
    }

    return null;
  }

}
