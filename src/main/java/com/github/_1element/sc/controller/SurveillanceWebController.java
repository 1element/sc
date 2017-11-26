package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.dto.CameraPushNotificationSettingResult;
import com.github._1element.sc.dto.ImagesDateSummaryResult;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.properties.NotifierProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.PushNotificationService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  private PushNotificationService pushNotificationService;

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
                                   CameraRepository cameraRepository, PushNotificationService pushNotificationService) {
    this.surveillanceService = surveillanceService;
    this.imageRepository = imageRepository;
    this.cameraRepository = cameraRepository;
    this.pushNotificationService = pushNotificationService;
  }

  @GetMapping(URIConstants.ROOT)
  public String home() {
    return "redirect:" + URIConstants.LIVEVIEW;
  }

  @GetMapping(value = {URIConstants.RECORDINGS, URIConstants.RECORDINGS + "/{date}"})
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
    final LocalDateTime mostRecentImageDate = surveillanceService.getMostRecentImageDate(images, date);
    final String visibleImageIds = images.getContent().stream().map(image -> String.valueOf(image.getId())).collect(Collectors.joining(","));

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

  @PostMapping(URIConstants.RECORDINGS)
  public String recordingsArchive(@RequestParam List<Long> imageIds) {
    imageRepository.archiveByIds(imageIds);

    return "redirect:" + URIConstants.RECORDINGS;
  }

  @GetMapping(URIConstants.LIVEVIEW)
  public String liveview(Model model, HttpServletRequest request) {
    List<Camera> cameras = cameraRepository.findAllWithSnapshotUrl();

    model.addAttribute("cameras", cameras);
    model.addAttribute("liveviewUrl", URIConstants.LIVEVIEW);

    if (RequestUtil.isAjax(request)) {
      return "fragments/liveview-grid";
    }

    return "liveview";
  }

  @GetMapping(URIConstants.LIVEVIEW + "/{cameraId}")
  public String liveviewSingleCamera(@PathVariable Optional<String> cameraId, Model model, HttpServletRequest request) throws CameraNotFoundException {
    Camera camera = surveillanceService.getCamera(cameraId);
    if (camera == null || camera.getSnapshotUrl() == null) {
      throw new CameraNotFoundException();
    }

    model.addAttribute("camera", camera);

    if (RequestUtil.isAjax(request)) {
      return "fragments/liveview-camera";
    }

    model.addAttribute("liveviewAjaxUrl", URIConstants.LIVEVIEW);

    return "liveview-single";
  }

  @GetMapping(URIConstants.LIVESTREAM)
  public String livestream(Model model) {
    List<Camera> cameras = cameraRepository.findAllWithStreamUrl();

    model.addAttribute("cameras", cameras);
    model.addAttribute("livestreamUrl", URIConstants.LIVESTREAM);

    return "livestream";
  }

  @GetMapping(URIConstants.LIVESTREAM + "/{cameraId}")
  public String livestreamSingleCamera(@PathVariable Optional<String> cameraId, Model model) throws CameraNotFoundException {
    Camera camera = surveillanceService.getCamera(cameraId);
    if (camera == null || camera.getStreamUrl() == null) {
      throw new CameraNotFoundException();
    }

    model.addAttribute("camera", camera);

    return "livestream-single";
  }

  @GetMapping(URIConstants.SETTINGS)
  public String settings(Model model) {
    List<CameraPushNotificationSettingResult> cameraPushNotificationSettings = pushNotificationService.getAllSettings();

    model.addAttribute("cameraPushNotificationSettings", cameraPushNotificationSettings);

    return "settings";
  }

  @ModelAttribute
  public void populateNavigationModel(Model model) {
    List<ImagesDateSummaryResult> imagesSummary = imageRepository.getImagesSummary();
    Long countAllImages = imageRepository.countAllImages();

    model.addAttribute("countRecordings", countAllImages);
    model.addAttribute("recordingsNavigation", imagesSummary);
    model.addAttribute("notifierProperties", notifierProperties);
  }

}
