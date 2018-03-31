package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.controller.SurveillanceProxyController;
import com.github._1element.sc.controller.SurveillanceStreamGenerationController;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.dto.CameraResource;
import com.github._1element.sc.dto.SurveillanceImageResource;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.exception.ProxyException;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.utils.URIConstants;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Service;

/**
 * Service to convert between internal entities and data transfer objects (DTOs).
 * This way we do not expose our internal domain objects/entities.
 */
@Service
public class ModelMappingService {

  private ModelMapper modelMapper;

  private CameraRepository cameraRepository;

  private static final Logger LOG = LoggerFactory.getLogger(ModelMappingService.class);

  @Autowired
  public ModelMappingService(ModelMapper modelMapper, CameraRepository cameraRepository) {
    this.modelMapper = modelMapper;
    this.cameraRepository = cameraRepository;
  }

  /**
   * Converts the provided {@link Camera} to a {@link CameraResource} with additional attributes.
   *
   * @param camera the camera to convert
   * @return converted camera resource
   */
  public CameraResource convertCameraToResource(Camera camera) {
    CameraResource cameraResource = modelMapper.map(camera, CameraResource.class);

    String snapshotProxyUrl = null;
    try {
      snapshotProxyUrl = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
        .methodOn(SurveillanceProxyController.class).retrieveSnapshot(camera.getId())).toString();
    } catch (CameraNotFoundException | ProxyException exception) {
      LOG.debug("Exception occurred during link building: '{}'", exception.getMessage());
    }
    // methodOn() does not work because of void return type
    String streamGeneratorUrl = ControllerLinkBuilder.linkTo(SurveillanceStreamGenerationController.class)
        .slash(URIConstants.GENERATE_MJPEG.replace("{id}", camera.getId())).toString();

    cameraResource.setSnapshotProxyUrl(snapshotProxyUrl);
    cameraResource.setStreamGeneratorUrl(streamGeneratorUrl);

    return cameraResource;
  }

  /**
   * Converts the provided {@link SurveillanceImage} to a {@link SurveillanceImageResource}.
   *
   * @param surveillanceImage the surveillance image to convert
   * @return converted surveillance image resource
   */
  public SurveillanceImageResource convertSurveillanceImageToResource(SurveillanceImage surveillanceImage) {
    SurveillanceImageResource surveillanceImageResource =
        modelMapper.map(surveillanceImage, SurveillanceImageResource.class);

    // add camera name
    Camera camera = cameraRepository.findById(surveillanceImage.getCameraId());
    if (camera != null) {
      surveillanceImageResource.setCameraName(camera.getName());
    }

    return surveillanceImageResource;
  }

}
