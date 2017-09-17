package com.github._1element.sc.controller; //NOSONAR

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;

import com.github._1element.sc.exception.ForbiddenException;
import com.github._1element.sc.exception.ResourceNotFoundException;
import com.github._1element.sc.properties.MJPEGTransformProperties;
import com.github._1element.sc.service.MJPEGTransformationService;
import com.github._1element.sc.utils.URIConstants;

@Controller
public class SurveillanceTransformationController {

  private MJPEGTransformationService mjpegTransformationService;

  private MJPEGTransformProperties mjpegProperties;

  private static final Logger LOG = LoggerFactory.getLogger(SurveillanceTransformationController.class);
  
  @Autowired
  public SurveillanceTransformationController(MJPEGTransformationService mjpegTransformationService, MJPEGTransformProperties mjpegProperties) {
    this.mjpegTransformationService = mjpegTransformationService;
    this.mjpegProperties = mjpegProperties;
  }

  @GetMapping(URIConstants.TRANSFORM_MJPEG)
  public void transformMJPEG(@PathVariable int id, HttpServletResponse response) throws ForbiddenException, ResourceNotFoundException {
    if (!mjpegProperties.isEnabled()) {
      throw new ForbiddenException("MJPEG transformation is disabled. Check your configuration.");
    }

    String snapshotUrl = mjpegTransformationService.getSnapshotUrl(id);
    if (snapshotUrl == null) {
      throw new ResourceNotFoundException("Invalid ID provided.");
    }

    mjpegTransformationService.setContentType(response);
    mjpegTransformationService.setCacheControlHeader(response);

    try {
      mjpegTransformationService.writeSnapshotToOutputStream(snapshotUrl, response);
    } catch (IOException | RestClientException e) {
      LOG.debug("MJPEG streaming terminated: {}", e.getMessage());
    }
  }

}
