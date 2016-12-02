package com.github._1element.sc.repository;

import com.github._1element.sc.dto.ImagesSummaryResult;
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

  Page<SurveillanceImage> findAllByCameraIdAndArchived(String cameraId, Boolean archived, Pageable pageable);

  Page<SurveillanceImage> findAllByCameraIdAndReceivedAtBetweenAndArchived(String cameraId, LocalDateTime start, LocalDateTime end, Boolean archive, Pageable pageable);

  Page<SurveillanceImage> findAllByArchived(Boolean archived, Pageable pageable);

  Page<SurveillanceImage> findAllByReceivedAtBetweenAndArchived(LocalDateTime start, LocalDateTime end, Boolean archived, Pageable pageable);

  @Query("select new com.github._1element.sc.dto.ImagesSummaryResult(cast(receivedAt as date), count(*)) from SurveillanceImage s where s.archived = false group by cast(receivedAt as date) order by cast(receivedAt as date) desc")
  List<ImagesSummaryResult> getImagesSummary();

  @Query("select count(*) from SurveillanceImage s where s.archived = false")
  Long countAllImages();

  @Query("select s.receivedAt from SurveillanceImage s where s.archived = false order by s.receivedAt desc")
  List<LocalDateTime> getMostRecentImageDate(Pageable pageable);

  @Query("select s from SurveillanceImage s where s.archived = true and s.receivedAt <= :dateBefore")
  List<SurveillanceImage> getArchivedImagesToCleanup(@Param("dateBefore") LocalDateTime before);

  @Modifying
  @Transactional
  @Query("update SurveillanceImage s set s.archived = true where s.id in :ids")
  void archiveByIds(@Param("ids") List<Long> ids);

}
