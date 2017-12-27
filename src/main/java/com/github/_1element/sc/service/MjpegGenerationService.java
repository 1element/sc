package com.github._1element.sc.service; //NOSONAR

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import com.github._1element.sc.properties.StreamGenerationProperties;
import com.github._1element.sc.utils.RestTemplateUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Generation service to create MJPEG streams.
 */
@Service
public class MjpegGenerationService {

  private StreamGenerationProperties streamGenerationProperties;

  private final RestTemplateBuilder restTemplateBuilder;

  private static final String NL = "\r\n";

  private static final String MJPEG_BOUNDARY = "--BoundaryString";

  private static final String MJPEG_HEAD = MJPEG_BOUNDARY + NL + "Content-type: image/jpeg" + NL + "Content-Length: ";

  public MjpegGenerationService(StreamGenerationProperties streamGenerationProperties,
                                RestTemplateBuilder restTemplateBuilder) {
    this.streamGenerationProperties = streamGenerationProperties;
    this.restTemplateBuilder = restTemplateBuilder;
  }

  /**
   * Continuously retrieve JPEG image from given camera snapshotUrl and
   * output result as MJPEG stream to the provided HttpServlet response.
   *
   * @param snapshotUrl the camera snapshot URL to retrieve image from
   * @param response the HTTP response to write to
   * @throws IOException if output stream could not be written (e.g. client disconnects)
   * @throws RestClientException if an HTTP error occurred while accessing the snapshot URL
   */
  public void writeSnapshotToOutputStream(String snapshotUrl, HttpServletResponse response) throws IOException {
    RestTemplate restTemplate = RestTemplateUtils.buildWithAuth(restTemplateBuilder, snapshotUrl);

    OutputStream outputStream = response.getOutputStream();

    while (!Thread.currentThread().isInterrupted()) {
      byte[] imageData = restTemplate.getForObject(snapshotUrl, byte[].class);

      outputStream.write((MJPEG_HEAD + imageData.length + NL + NL).getBytes());
      outputStream.write(imageData);
      outputStream.write((NL + NL).getBytes());
      outputStream.flush();

      try {
        TimeUnit.MILLISECONDS.sleep(streamGenerationProperties.getMjpegDelay());
      } catch (InterruptedException exception) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Set response content type.
   *
   * @param response HTTP response to set header
   */
  public void setContentType(HttpServletResponse response) {
    response.setContentType("multipart/x-mixed-replace; boundary=" + MJPEG_BOUNDARY);
  }

  /**
   * Set response cache control header.
   *
   * @param response HTTP response to set header
   */
  public void setCacheControlHeader(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache, private");
  }

}
