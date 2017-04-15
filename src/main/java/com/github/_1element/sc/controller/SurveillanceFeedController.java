package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.SurveillanceService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping(URIConstants.FEED_ROOT)
public class SurveillanceFeedController {

  private SurveillanceService surveillanceService;

  private SurveillanceImageRepository imageRepository;

  @Value("${sc.feed.baseurl}")
  private String feedBaseUrl;

  @Autowired
  public SurveillanceFeedController(SurveillanceService surveillanceService, SurveillanceImageRepository imageRepository) {
    this.surveillanceService = surveillanceService;
    this.imageRepository = imageRepository;
  }

  @GetMapping(URIConstants.FEED_STATUS)
  public String statusfeed(Model model) throws Exception {

    Long countAllImages = imageRepository.countAllImages();
    LocalDateTime mostRecentImageDate = surveillanceService.getMostRecentImageDate();

    model.addAttribute("countRecordings", countAllImages);
    model.addAttribute("mostRecentImageDate", mostRecentImageDate);
    model.addAttribute("baseUrl", feedBaseUrl);

    return "feed-status";
  }

}
