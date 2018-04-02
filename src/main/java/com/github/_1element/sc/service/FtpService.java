package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.UploadFtplet;
import com.github._1element.sc.properties.FtpProperties;
import com.github._1element.sc.repository.CameraRepository;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Autowired;
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
 * FTP service class. Will start FTP server.
 */
@Service
public class FtpService {

  private final CameraRepository cameraRepository;

  private final UploadFtplet uploadFtplet;

  private final FtpProperties properties;

  private FtpServer server;

  private PropertiesUserManagerFactory userManagerFactory;

  /**
   * Constructor.
   *
   * @param cameraRepository the camera repository
   * @param uploadFtplet the upload ftplet
   * @param properties the properties
   */
  @Autowired
  public FtpService(final CameraRepository cameraRepository, final UploadFtplet uploadFtplet,
                    final FtpProperties properties) {
    this.cameraRepository = cameraRepository;
    this.uploadFtplet = uploadFtplet;
    this.properties = properties;
  }

  /**
   * Start FTP server.
   *
   * @throws FtpException exception in case of an error
   */
  @PostConstruct
  public void start() throws FtpException {
    if (!properties.isEnabled()) {
      return;
    }

    final FtpServerFactory ftpServerFactory = new FtpServerFactory();
    final ListenerFactory listenerFactory = new ListenerFactory();

    listenerFactory.setPort(properties.getPort());
    ftpServerFactory.addListener("default", listenerFactory.createListener());

    // user manager
    userManagerFactory = new PropertiesUserManagerFactory();
    ftpServerFactory.setUserManager(populateUserManager());

    // ftplet
    final Map<String, Ftplet> ftpletMap = new HashMap<>();
    ftpletMap.put("uploadFtplet", uploadFtplet);
    ftpServerFactory.setFtplets(ftpletMap);

    // start server
    server = ftpServerFactory.createServer();
    server.start();
  }

  /**
   * Stop FTP server.
   */
  @PreDestroy
  public void stop() {
    if (server != null) {
      server.stop();
    }
  }

  /**
   * Populate FTP credentials and home directories.
   *
   * @return user manager
   * @throws FtpException exception in case of an error
   */
  private UserManager populateUserManager() throws FtpException {
    final UserManager userManager = userManagerFactory.createUserManager();

    final List<Authority> authorities = new ArrayList<>();
    authorities.add(new WritePermission());

    for (final Camera camera : cameraRepository.findAll()) {
      final BaseUser user = new BaseUser();
      user.setName(camera.getFtp().getUsername());
      user.setPassword(camera.getFtp().getPassword());
      user.setHomeDirectory(camera.getFtp().getIncomingDirectory());
      user.setAuthorities(authorities);

      userManager.save(user);
    }

    return userManager;
  }

}
