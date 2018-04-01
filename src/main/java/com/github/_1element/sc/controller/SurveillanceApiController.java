package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.domain.SurveillanceProperties;
import com.github._1element.sc.dto.CameraResource;
import com.github._1element.sc.dto.PushNotificationSettingResource;
import com.github._1element.sc.dto.PushNotificationSettingUpdateResource;
import com.github._1element.sc.dto.SurveillanceImageBulkUpdateResource;
import com.github._1element.sc.dto.SurveillanceImageResource;
import com.github._1element.sc.dto.SurveillanceImageUpdateResource;
import com.github._1element.sc.exception.CameraNotFoundException;
import com.github._1element.sc.exception.ResourceNotFoundException;
import com.github._1element.sc.properties.ImageProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.PushNotificationSettingRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.ModelMappingService;
import com.github._1element.sc.service.PushNotificationService;
import com.github._1element.sc.service.SurveillanceService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RESTful API controller.
 */
@RestController
@RequestMapping(URIConstants.API_ROOT)
public class SurveillanceApiController {

  private SurveillanceService surveillanceService;

  private SurveillanceImageRepository imageRepository;

  private CameraRepository cameraRepository;

  private PushNotificationSettingRepository pushNotificationSettingRepository;

  private PushNotificationService pushNotificationService;

  private SurveillanceProperties surveillanceProperties;

  private ImageProperties imageProperties;

  private ModelMappingService modelMappingService;

  private static final String SORT_FIELD = "receivedAt";

  /**
   * Constructor.
   *
   * @param imageRepository the image repository
   * @param pushNotificationSettingRepository the push notification setting repository
   * @param pushNotificationService the push notification service
   * @param surveillanceService the surveillance service
   * @param cameraRepository the camera repository
   * @param surveillanceProperties the surveillance configuration properties
   * @param imageProperties the image configuration properties
   * @param modelMappingService the model mapping service to use for entity to dto projection
   */
  @Autowired
  public SurveillanceApiController(SurveillanceImageRepository imageRepository,
                                   PushNotificationSettingRepository pushNotificationSettingRepository,
                                   PushNotificationService pushNotificationService,
                                   SurveillanceService surveillanceService,
                                   CameraRepository cameraRepository,
                                   SurveillanceProperties surveillanceProperties,
                                   ImageProperties imageProperties,
                                   ModelMappingService modelMappingService) {
    this.imageRepository = imageRepository;
    this.pushNotificationSettingRepository = pushNotificationSettingRepository;
    this.pushNotificationService = pushNotificationService;
    this.surveillanceService = surveillanceService;
    this.cameraRepository = cameraRepository;
    this.surveillanceProperties = surveillanceProperties;
    this.imageProperties = imageProperties;
    this.modelMappingService = modelMappingService;
  }

  /**
   * Paged list of SurveillanceImage resources ("recordings").
   *
   * @param cameraId the optional camera id to filter for
   * @param pageParam the page to retrieve results for
   * @param sizeParam the size of each page (limit), default will be used if not provided
   * @param isArchive set to true if archived images shall be included in the result
   * @param date the optional date to filter for
   * @param assembler the assembler used to convert Page results to HATEOAS PagedResources
   *
   * @return paged SurveillanceImage resources
   */
  @GetMapping(value = URIConstants.API_RECORDINGS, produces = MediaType.APPLICATION_JSON_VALUE)
  public PagedResources<Resource<SurveillanceImageResource>> recordingsList(
                                      @RequestParam(required = false, value = "camera") String cameraId,
                                      @RequestParam(required = false, value = "page") Integer pageParam,
                                      @RequestParam(required = false, value = "size") Integer sizeParam,
                                      @RequestParam(required = false, value = "archive") boolean isArchive,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                      PagedResourcesAssembler<SurveillanceImageResource> assembler) {

    int page = (pageParam != null) ? pageParam : 0;
    int size = (sizeParam != null) ? sizeParam : imageProperties.getPageSize();

    PageRequest pageRequest = new PageRequest(page, size, new Sort(new Sort.Order(Sort.Direction.DESC, SORT_FIELD)));

    Optional<LocalDate> localDate = Optional.empty();
    if (date != null) {
      localDate = Optional.of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    Optional<String> camera = Optional.ofNullable(cameraId);

    Page<SurveillanceImage> surveillanceImages =
        surveillanceService.getImagesPage(camera, localDate, isArchive, pageRequest);
    Page<SurveillanceImageResource> surveillanceImageResources = surveillanceImages.map(
        modelMappingService::convertSurveillanceImageToResource);

    Link selfLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(SurveillanceApiController.class)
        .recordingsList(cameraId, page, size, isArchive, date, assembler)).withSelfRel().expand();

    return assembler.toResource(surveillanceImageResources, selfLink);
  }

  /**
   * Return SurveillanceImage ("recording") for given id.
   *
   * @param id the id to retrieve result for
   * @return SurveillanceImage resource
   * @throws ResourceNotFoundException exception in case of invalid id
   */
  @GetMapping(value = URIConstants.API_RECORDINGS + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public SurveillanceImageResource recording(@PathVariable long id) throws ResourceNotFoundException {
    SurveillanceImage image = imageRepository.findOne(id);

    if (image == null) {
      throw new ResourceNotFoundException(String.format("Recording %s was not found.", id));
    }

    return modelMappingService.convertSurveillanceImageToResource(image);
  }

  /**
   * Update a list of SurveillanceImages ("recordings").
   * Currently the only supported action is to archive images. Thus all provided IDs are collected
   * and {@link SurveillanceImage#archived} is set to true using one single JPA query.
   *
   * @param surveillanceImageUpdateList the list of surveillance images to update
   */
  @PatchMapping(URIConstants.API_RECORDINGS)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void recordingsUpdate(@RequestBody List<SurveillanceImageUpdateResource> surveillanceImageUpdateList) {
    List<Long> imageIds = surveillanceImageUpdateList.stream().map(SurveillanceImageUpdateResource::getId)
        .collect(Collectors.toList());
    imageRepository.updateSetArchived(imageIds);
  }

  /**
   * Bulk update all recordings before a provided timestamp (non REST compliant action).
   * Currently the only supported action is to set the archived state.
   *
   * @param updateResource the bulk update command
   */
  @PostMapping(URIConstants.API_RECORDINGS)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void recordingsBulkUpdate(@RequestBody SurveillanceImageBulkUpdateResource updateResource) {
    imageRepository.updateArchiveState(updateResource.isArchived(), updateResource.getDateBefore());
  }

  /**
   * Returns a list of all cameras.
   *
   * @return list of camera resources
   */
  @GetMapping(value = URIConstants.API_CAMERAS, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CameraResource> camerasList() {
    List<Camera> cameras = cameraRepository.findAll();
    return cameras.stream().map(modelMappingService::convertCameraToResource).collect(Collectors.toList());
  }

  /**
   * Return camera resource for given id.
   *
   * @param id the camera id to retrieve
   * @return camera resource
   * @throws CameraNotFoundException exception in case of invalid id
   */
  @GetMapping(value = URIConstants.API_CAMERAS + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CameraResource camera(@PathVariable String id) throws CameraNotFoundException {
    Camera camera = cameraRepository.findById(id);

    if (camera == null) {
      throw new CameraNotFoundException();
    }

    return modelMappingService.convertCameraToResource(camera);
  }

  /**
   * Returns a list of all push notification settings for each camera.
   *
   * @return list of push notification settings
   */
  @GetMapping(value = URIConstants.API_PUSH_NOTIFICATION_SETTINGS, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PushNotificationSettingResource> pushNotificationSettingsList() {
    return pushNotificationService.getAllSettings();
  }

  /**
   * Update push notification settings (enable/disable).
   *
   * @param cameraId the camera id to modify the setting for
   * @param pushNotificationSettingUpdateResource the push notification setting update resource
   */
  @PatchMapping(URIConstants.API_PUSH_NOTIFICATION_SETTINGS + "/{cameraId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pushNotificationSettingsUpdate(@PathVariable String cameraId,
                                             @RequestBody PushNotificationSettingUpdateResource
                                               pushNotificationSettingUpdateResource)
      throws CameraNotFoundException {

    Camera camera = cameraRepository.findById(cameraId);
    if (camera == null) {
      throw new CameraNotFoundException();
    }

    PushNotificationSetting pushNotificationSetting = pushNotificationSettingRepository.findByCameraId(cameraId);
    if (pushNotificationSetting == null) {
      pushNotificationSetting = new PushNotificationSetting(cameraId,
          pushNotificationSettingUpdateResource.isEnabled());
    } else {
      pushNotificationSetting.setEnabled(pushNotificationSettingUpdateResource.isEnabled());
    }

    pushNotificationSettingRepository.save(pushNotificationSetting);
  }

  /**
   * Return some configuration properties.
   *
   * @return configuration properties
   */
  @GetMapping(value = URIConstants.API_PROPERTIES, produces = MediaType.APPLICATION_JSON_VALUE)
  public SurveillanceProperties properties() {
    return surveillanceProperties;
  }

}
