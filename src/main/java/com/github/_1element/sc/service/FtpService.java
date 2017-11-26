package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.UploadFtplet;
import com.github._1element.sc.repository.CameraRepository;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ftp service class. Will start ftp server.
 */
@Service
public class FtpService {

  private CameraRepository cameraRepository;

  private UploadFtplet uploadFtplet;

  @Value("${sc.ftp.enabled:false}")
  private boolean enabled;

  @Value("${sc.ftp.port:2221}")
  private int ftpServerPort;

  private FtpServer server;

  private PropertiesUserManagerFactory userManagerFactory;

  @Autowired
  public FtpService(CameraRepository cameraRepository, UploadFtplet uploadFtplet) {
    this.cameraRepository = cameraRepository;
    this.uploadFtplet = uploadFtplet;
  }

  /**
   * Start ftp server.
   *
   * @throws FtpException exception in case of an error
   */
  @PostConstruct
  public void start() throws FtpException {
    if (!enabled) {
      return;
    }

    FtpServerFactory ftpServerFactory = new FtpServerFactory();
    ListenerFactory listenerFactory = new ListenerFactory();

    listenerFactory.setPort(ftpServerPort);
    ftpServerFactory.addListener("default", listenerFactory.createListener());

    // user manager
    userManagerFactory = new PropertiesUserManagerFactory();
    ftpServerFactory.setUserManager(populateUserManager());

    // ftplet
    Map<String, Ftplet> ftpletMap = new HashMap<>();
    ftpletMap.put("uploadFtplet", uploadFtplet);
    ftpServerFactory.setFtplets(ftpletMap);

    // start server
    server = ftpServerFactory.createServer();
    server.start();
  }

  /**
   * Stop ftp server.
   */
  @PreDestroy
  public void stop() {
    if (server != null) {
      server.stop();
    }
  }

  /**
   * Populate ftp credentials and home directories.
   *
   * @return user manager
   * @throws FtpException exception in case of an error
   */
  private UserManager populateUserManager() throws FtpException {
    UserManager userManager = userManagerFactory.createUserManager();

    List<Authority> authorities = new ArrayList<>();
    authorities.add(new WritePermission());

    for (Camera camera : cameraRepository.findAll()) {
      BaseUser user = new BaseUser();
      user.setName(camera.getFtpUsername());
      user.setPassword(camera.getFtpPassword());
      user.setHomeDirectory(camera.getFtpIncomingDirectory());
      user.setAuthorities(authorities);

      userManager.save(user);
    }

    return userManager;
  }

}
