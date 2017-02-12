package com.github._1element.sc.controller;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.dto.ImagesSummaryResult;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.properties.NotifierProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.SurveillanceService;
import com.github._1element.sc.utils.RequestUtil;
import com.github._1element.sc.utils.URIConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.format.annotation.DateTimeFormat.*;

@Controller
public class SurveillanceWebController {

  private SurveillanceService surveillanceService;

  private SurveillanceImageRepository imageRepository;

  private CameraRepository cameraRepository;

  @Autowired
  private NotifierProperties notifierProperties;

  @Autowired
  private MessageSource messageSource;

  @Value("${sc.view.images-per-page:50}")
  private Integer pageSize;

  private static final String PATH_SEPARATOR = "/";

  private static final String SORT_FIELD = "receivedAt";

  private static final String MESSAGE_PROPERTIES_CAMERAS_ALL = "cameras.all";

  @Autowired
  public SurveillanceWebController(SurveillanceService surveillanceService, SurveillanceImageRepository imageRepository,
                                   CameraRepository cameraRepository) {
    this.surveillanceService = surveillanceService;
    this.imageRepository = imageRepository;
    this.cameraRepository = cameraRepository;
  }

  @RequestMapping(value = URIConstants.ROOT, method = RequestMethod.GET)
  public String home() throws Exception {
    return "redirect:" + URIConstants.LIVEVIEW;
  }

  @RequestMapping(value = {URIConstants.RECORDINGS, URIConstants.RECORDINGS + "/{date}"}, method = RequestMethod.GET)
  public String recordingsList(@PathVariable @DateTimeFormat(iso = ISO.DATE) Optional<LocalDate> date,
                               @RequestParam Optional<String> camera, @RequestParam Optional<Boolean> archive,
                               @RequestParam Optional<Integer> page, Model model) throws Exception {

    Integer zeroBasedPageNumber = 0;
    if (page.isPresent()) {
      zeroBasedPageNumber = page.get() - 1;
    }

    boolean isArchive = false;
    if (archive.isPresent() && Boolean.TRUE.equals(archive.get())) {
      isArchive = true;
    }

    PageRequest pageRequest = new PageRequest(
      zeroBasedPageNumber,
      pageSize,
      new Sort(new Sort.Order(Sort.Direction.DESC, SORT_FIELD))
    );

    String currentCameraId = null;
    String currentCameraName = messageSource.getMessage(MESSAGE_PROPERTIES_CAMERAS_ALL, null, LocaleContextHolder.getLocale());
    Camera currentCamera = surveillanceService.getCamera(camera);
    if (currentCamera != null) {
      currentCameraName = currentCamera.getName();
      currentCameraId = currentCamera.getId();
    }

    Page<SurveillanceImage> images = surveillanceService.getImagesPage(camera, date, isArchive, pageRequest);
    LocalDateTime mostRecentImageDate = surveillanceService.getMostRecentImageDate(images, date);
    String visibleImageIds = images.getContent().stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(","));

    List<Camera> cameras = cameraRepository.findAll();

    String baseUrl = URIConstants.RECORDINGS;
    if (date.isPresent()) {
      baseUrl = baseUrl + PATH_SEPARATOR + date.get().toString();
    }

    // recordings
    model.addAttribute("images", images);
    model.addAttribute("cameras", cameras);
    model.addAttribute("mostRecentImageDate", mostRecentImageDate);
    model.addAttribute("baseUrl", baseUrl);
    model.addAttribute("isArchive", isArchive);
    model.addAttribute("currentCameraId", currentCameraId);
    model.addAttribute("currentCameraName", currentCameraName);
    model.addAttribute("visibleImageIds", visibleImageIds);
    model.addAttribute("showArchiveAction", StringUtils.isNotBlank(visibleImageIds) && !isArchive);

    return "recordings";
  }

  @RequestMapping(value = URIConstants.RECORDINGS, method = RequestMethod.POST)
  public String recordingsArchive(@RequestParam List<Long> imageIds) throws Exception {
    imageRepository.archiveByIds(imageIds);

    return "redirect:" + URIConstants.RECORDINGS;
  }

  @RequestMapping(value = {URIConstants.LIVEVIEW}, method = RequestMethod.GET)
  public String liveview(Model model, HttpServletRequest request) throws Exception {
    List<Camera> cameras = cameraRepository.findAll();

    model.addAttribute("cameras", cameras);
    model.addAttribute("liveviewUrl", URIConstants.LIVEVIEW);

    if (RequestUtil.isAjax(request)) {
      return "fragments/liveview-grid";
    }

    return "liveview";
  }

  @RequestMapping(value = {URIConstants.LIVEVIEW + "/{cameraId}"}, method = RequestMethod.GET)
  public String liveviewSingleCamera(@PathVariable Optional<String> cameraId, Model model, HttpServletRequest request) throws Exception {
    Camera camera = surveillanceService.getCamera(cameraId);
    if (camera == null) {
      throw new CameraNotFoundException();
    }

    model.addAttribute("camera", camera);

    if (RequestUtil.isAjax(request)) {
      return "fragments/liveview-camera";
    }

    model.addAttribute("liveviewAjaxUrl", URIConstants.LIVEVIEW);

    return "liveview-single";
  }

  @RequestMapping(value = {URIConstants.LIVESTREAM}, method = RequestMethod.GET)
  public String livestream(Model model) throws Exception {
    List<Camera> cameras = cameraRepository.findAll();

    model.addAttribute("cameras", cameras);
    model.addAttribute("livestreamUrl", URIConstants.LIVESTREAM);

    return "livestream";
  }

  @RequestMapping(value = {URIConstants.LIVESTREAM + "/{cameraId}"}, method = RequestMethod.GET)
  public String livestreamSingleCamera(@PathVariable Optional<String> cameraId, Model model) throws Exception {
    Camera camera = surveillanceService.getCamera(cameraId);
    if (camera == null) {
      throw new CameraNotFoundException();
    }

    model.addAttribute("camera", camera);

    return "livestream-single";
  }

  @ModelAttribute
  public void populateNavigationModel(Model model) {
    List<ImagesSummaryResult> imagesSummary = imageRepository.getImagesSummary();
    Long countAllImages = imageRepository.countAllImages();

    model.addAttribute("countRecordings", countAllImages);
    model.addAttribute("recordingsNavigation", imagesSummary);
    model.addAttribute("notifierProperties", notifierProperties);
  }

}
