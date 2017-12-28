package com.github._1element.sc.repository; //NOSONAR

import com.github._1element.sc.domain.SurveillanceImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Surveillance image repository.
 */
public interface SurveillanceImageRepository extends JpaRepository<SurveillanceImage, Long> {

  Page<SurveillanceImage> findAllByArchived(boolean archived, Pageable pageable);

  Page<SurveillanceImage> findAllByCameraIdAndArchived(String cameraId, boolean archived, Pageable pageable);

  @Query("select s from SurveillanceImage s where (s.receivedAt between :start and :end) and s.archived = :archived")
  Page<SurveillanceImage> findAllForDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                              @Param("archived") boolean archived, Pageable pageable);

  @Query("select s from SurveillanceImage s where s.cameraId = :cameraId and (s.receivedAt between :start and :end) "
      + "and s.archived = :archived")
  Page<SurveillanceImage> findAllForDateRangeAndCameraId(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         @Param("cameraId") String cameraId,
                                                         @Param("archived") boolean archived, Pageable pageable);

  @Query("select count(*) from SurveillanceImage s where s.cameraId = :cameraId and s.archived = false")
  Long countImagesForCamera(@Param("cameraId") String cameraId);

  @Query("select s.receivedAt from SurveillanceImage s where s.cameraId = :cameraId and s.archived = false "
      + "order by s.receivedAt desc")
  List<LocalDateTime> getMostRecentImageDateForCamera(@Param("cameraId") String cameraId, Pageable pageable);

  @Query("select s from SurveillanceImage s where s.archived = true and s.receivedAt <= :dateBefore")
  List<SurveillanceImage> getArchivedImagesToCleanup(@Param("dateBefore") LocalDateTime before);

  @Modifying
  @Transactional
  @Query("update SurveillanceImage s set s.archived = true where s.id in :ids")
  void updateSetArchived(@Param("ids") List<Long> ids);

  @Modifying
  @Transactional
  @Query("update SurveillanceImage s set s.archived = :value where s.receivedAt <= :dateBefore")
  void updateArchiveState(@Param("value") boolean value, @Param("dateBefore") LocalDateTime before);

}
