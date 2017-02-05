package com.github._1element.sc.controller;

import com.github._1element.sc.dto.ImagesCountResult;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = URIConstants.API_ROOT)
public class SurveillanceApiController {

  @Autowired
  private SurveillanceImageRepository imageRepository;

  @RequestMapping(value = URIConstants.API_RECORDINGS_COUNT, method = RequestMethod.GET)
  public ImagesCountResult recordingsCount() throws Exception {
    Long imagesCount = imageRepository.countAllImages();

    return new ImagesCountResult(imagesCount);
  }

}
