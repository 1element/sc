package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.controller.SurveillanceProxyController;
import com.github._1element.sc.controller.SurveillanceStreamGenerationController;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.dto.CameraResource;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.exception.ProxyException;
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

  private static final Logger LOG = LoggerFactory.getLogger(ModelMappingService.class);

  @Autowired
  public ModelMappingService(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
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

}
