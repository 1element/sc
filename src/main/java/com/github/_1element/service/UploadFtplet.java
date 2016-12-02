package com.github._1element.service;

import com.github._1element.domain.Camera;
import com.github._1element.events.ImageReceivedEvent;
import com.github._1element.repository.CameraRepository;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Ftp upload component. Will handle incoming file uploads.
 */
@Component
public class UploadFtplet extends DefaultFtplet {

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private CameraRepository cameraRepository;

  @Override
  public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
    String userRoot = session.getUser().getHomeDirectory();
    String currentDirectory = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();
    String fileArgument = request.getArgument();

    String fileName = userRoot + currentDirectory + File.separator + fileArgument;
    Camera camera = cameraRepository.findByFtpUsername(session.getUser().getName());

    eventPublisher.publishEvent(new ImageReceivedEvent(fileName, camera));

    return super.onUploadEnd(session, request);
  }

}
