package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.exception.ProxyException;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.service.SurveillanceProxyService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(URIConstants.PROXY_ROOT)
public class SurveillanceProxyController {

  private SurveillanceProxyService proxyService;

  private CameraRepository cameraRepository;

  @Autowired
  public SurveillanceProxyController(SurveillanceProxyService proxyService, CameraRepository cameraRepository) {
    this.proxyService = proxyService;
    this.cameraRepository = cameraRepository;
  }

  /**
   * Retrieves a new snapshot for the provided camera (proxy).
   *
   * @param id the camera id to retrieve snapshot for
   * @return retrieved snapshot image
   * @throws CameraNotFoundException if camera id was not found
   */
  @GetMapping(URIConstants.PROXY_SNAPSHOT)
  public ResponseEntity<byte[]> retrieveSnapshot(@PathVariable String id)
      throws CameraNotFoundException, ProxyException {

    Camera camera = cameraRepository.findById(id);

    if (camera == null) {
      throw new CameraNotFoundException();
    }

    return proxyService.retrieveImage(camera.getSnapshotUrl());
  }

}
