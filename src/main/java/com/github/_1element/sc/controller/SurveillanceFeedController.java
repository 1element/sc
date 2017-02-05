package com.github._1element.sc.controller;

import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.SurveillanceService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Controller
@RequestMapping(value = URIConstants.FEED_ROOT)
public class SurveillanceFeedController {

  @Autowired
  private SurveillanceService surveillanceService;

  @Autowired
  private SurveillanceImageRepository imageRepository;

  @Value("${sc.feed.baseurl}")
  private String feedBaseUrl;

  @RequestMapping(value = URIConstants.FEED_STATUS, method = RequestMethod.GET)
  public String statusfeed(Model model) throws Exception {

    Long countAllImages = imageRepository.countAllImages();
    LocalDateTime mostRecentImageDate = surveillanceService.getMostRecentImageDate();

    model.addAttribute("countRecordings", countAllImages);
    model.addAttribute("mostRecentImageDate", mostRecentImageDate);
    model.addAttribute("baseUrl", feedBaseUrl);

    return "feed-status";
  }

}
