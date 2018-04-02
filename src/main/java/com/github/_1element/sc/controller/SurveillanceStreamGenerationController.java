package com.github._1element.sc.controller; //NOSONAR

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.repository.CameraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.github._1element.sc.exception.ForbiddenException;
import com.github._1element.sc.service.MjpegGenerationService;
import com.github._1element.sc.utils.URIConstants;

@Controller
@RequestMapping(URIConstants.GENERATE_ROOT)
public class SurveillanceStreamGenerationController {

  private final CameraRepository cameraRepository;

  private final MjpegGenerationService mjpegGenerationService;

  private static final Logger LOG = LoggerFactory.getLogger(SurveillanceStreamGenerationController.class);

  /**
   * Constructs a SurveillanceStreamGenerationController.
   *
   * @param cameraRepository the camera repository
   * @param mjpegGenerationService the MJPEG generation service
   */
  @Autowired
  public SurveillanceStreamGenerationController(final CameraRepository cameraRepository,
                                                final MjpegGenerationService mjpegGenerationService) {
    this.cameraRepository = cameraRepository;
    this.mjpegGenerationService = mjpegGenerationService;
  }

  /**
   * Creates a simple MJPEG stream by requesting a camera snapshot JPG URL periodically.
   *
   * @param id the camera id to create stream for
   * @param response the streaming HTTP response
   * @throws ForbiddenException exception if MJPEG stream is disabled by configuration
   * @throws CameraNotFoundException exception if provided camera id could not be found
   */
  @GetMapping(URIConstants.GENERATE_MJPEG)
  public void generateMJPEG(@PathVariable final String id, final HttpServletResponse response)
      throws ForbiddenException, CameraNotFoundException {

    final Camera camera = cameraRepository.findById(id);

    if (camera == null) {
      throw new CameraNotFoundException();
    }

    if (!camera.getPicture().isStreamEnabled()) {
      throw new ForbiddenException("MJPEG stream generation is disabled.");
    }

    mjpegGenerationService.setContentType(response);
    mjpegGenerationService.setCacheControlHeader(response);

    try {
      mjpegGenerationService.writeSnapshotToOutputStream(camera.getPicture().getSnapshotUrl(), response);
    } catch (IOException | RestClientException exception) {
      LOG.debug("MJPEG streaming terminated: {}", exception.getMessage());
    }
  }

}
