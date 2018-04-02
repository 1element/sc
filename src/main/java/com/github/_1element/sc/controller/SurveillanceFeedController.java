package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.dto.ImagesCameraSummaryResult;
import com.github._1element.sc.service.SurveillanceService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(URIConstants.FEED_ROOT)
public class SurveillanceFeedController {

  private final SurveillanceService surveillanceService;

  @Autowired
  public SurveillanceFeedController(final SurveillanceService surveillanceService) {
    this.surveillanceService = surveillanceService;
  }

  /**
   * Renders RSS status feed displaying a summary for each camera.
   *
   * @param model the spring model
   * @return rendered RSS feed
   */
  @GetMapping(URIConstants.FEED_CAMERAS)
  public String camerasfeed(final Model model) {
    final List<ImagesCameraSummaryResult> imagesCameraSummaryResult = surveillanceService.getImagesCameraSummary();

    model.addAttribute("imagesCameraSummaryResult", imagesCameraSummaryResult);

    return "feed-cameras";
  }

}
