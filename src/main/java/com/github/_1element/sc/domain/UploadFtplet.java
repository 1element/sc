package com.github._1element.sc.domain; //NOSONAR

import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.service.FileService;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Ftp upload component. Will handle incoming file uploads.
 */
@Component
public class UploadFtplet extends DefaultFtplet {

  private ApplicationEventPublisher eventPublisher;

  private CameraRepository cameraRepository;

  private FileService fileService;

  private static final String SEPARATOR = "/";

  private static final String INVALID_EXTENSION_MESSAGE = "Permission denied. Invalid file extension.";

  private static final String NO_PERMISSION_DELETE_MESSAGE = "No permission to delete.";

  private static final String NO_PERMISSION_MESSAGE = "No permission.";

  private static final Logger LOG = LoggerFactory.getLogger(UploadFtplet.class);

  /**
   * Constructor.
   *
   * @param eventPublisher the event publisher used to distribute events
   * @param cameraRepository the camera repository
   * @param fileService the file service
   */
  @Autowired
  public UploadFtplet(ApplicationEventPublisher eventPublisher, CameraRepository cameraRepository,
                      FileService fileService) {
    this.eventPublisher = eventPublisher;
    this.cameraRepository = cameraRepository;
    this.fileService = fileService;
  }

  /**
   * Check file extension before upload starts. Restrict invalid extensions.
   *
   * @param session ftp session
   * @param request ftp request
   * @return ftplet result to skip or process this request
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onUploadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
    if (!fileService.hasValidExtension(request.getArgument())) {
      session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, INVALID_EXTENSION_MESSAGE));
      return FtpletResult.SKIP;
    }

    return FtpletResult.DEFAULT;
  }

  /**
   * Publish event if file is has been uploaded.
   *
   * @param session ftp session
   * @param request ftp request
   * @return ftplet default result
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
    String userRoot = session.getUser().getHomeDirectory();
    String currentDirectory = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();
    String fileArgument = request.getArgument();

    LOG.debug("File '{}' was uploaded to ftp server by client '{}'", fileArgument,
        session.getClientAddress().getHostString());

    String fileName = userRoot + currentDirectory + SEPARATOR + fileArgument;
    Camera camera = cameraRepository.findByFtpUsername(session.getUser().getName());

    eventPublisher.publishEvent(new ImageReceivedEvent(fileName, camera));

    return super.onUploadEnd(session, request);
  }

  /**
   * Restrict file delete operations.
   *
   * @param session ftp session
   * @param request ftp request
   * @return ftplet result to skip this request
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onDeleteStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
    session.write(new DefaultFtpReply(FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN,
        NO_PERMISSION_DELETE_MESSAGE));
    return FtpletResult.SKIP;
  }

  /**
   * Restrict file download operations.
   *
   * @param session ftp session
   * @param request ftp request
   * @return ftplet result to skip this request
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onDownloadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
    session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, NO_PERMISSION_MESSAGE));
    return FtpletResult.SKIP;
  }

  /**
   * Log connections to ftp server.
   *
   * @param session ftp session
   * @return default ftplet result
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
    LOG.debug("Client '{}' connected to ftp server.", session.getClientAddress().getHostString());
    return super.onConnect(session);
  }

  /**
   * Log disconnections from ftp server.
   *
   * @param session ftp session
   * @return default ftplet result
   * @throws FtpException exception if a FTP error occurs
   * @throws IOException exception if an I/O error occurs
   */
  @Override
  public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
    LOG.debug("Client '{}' disconnected from ftp server.", session.getClientAddress().getHostString());
    return super.onDisconnect(session);
  }


}
