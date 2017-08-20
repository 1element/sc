package com.github._1element.sc.domain.remotecopy; //NOSONAR

import org.springframework.beans.factory.annotation.Autowired;

import com.github._1element.sc.exception.SFTPRemoteCopyException;
import com.github._1element.sc.properties.SFTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Abstract SFTP remote copy class.
 */
public class AbstractSFTPRemoteCopy {

  protected SFTPRemoteCopyProperties sftpRemoteCopyProperties;
  
  protected FileService fileService;

  private JSch jsch;
  
  private Session session;

  private static final String SFTP_CHANNEL_NAME = "sftp";
  
  private static final String CONFIG_STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
  
  private static final String CONFIG_DISABLED = "no";

  @Autowired
  public AbstractSFTPRemoteCopy(SFTPRemoteCopyProperties sftpRemoteCopyProperties, JSch jsch, FileService fileService) {
    this.sftpRemoteCopyProperties = sftpRemoteCopyProperties;
    this.jsch = jsch;
    this.fileService = fileService;
  }

  /**
   * Creates a SFTP channel.
   * 
   * @return SFTP channel
   * @throws SFTPRemoteCopyException
   */
  protected ChannelSftp createSFTPChannel() throws SFTPRemoteCopyException {
    Channel channel;
    try {
      session = jsch.getSession(sftpRemoteCopyProperties.getUsername(), sftpRemoteCopyProperties.getHost());
      session.setConfig(CONFIG_STRICT_HOST_KEY_CHECKING, CONFIG_DISABLED);
      session.setPassword(sftpRemoteCopyProperties.getPassword());
      session.connect();
      channel = session.openChannel(SFTP_CHANNEL_NAME);
    } catch (JSchException e) {
      session.disconnect();
      throw new SFTPRemoteCopyException("Could not establish SSH connection: " + e.getMessage(), e);
    }

    if (channel == null) {
      session.disconnect();
      throw new SFTPRemoteCopyException("No channel was found.");
    }

    try {
      channel.connect();
    } catch (JSchException e) {
      session.disconnect();
      throw new SFTPRemoteCopyException("Could not establish SFTP channel: " + e.getMessage(), e);
    }

    if (!(channel instanceof ChannelSftp)) {
      channel.disconnect();
      session.disconnect();
      throw new SFTPRemoteCopyException("No SFTP channel was found.");
    }

    return (ChannelSftp) channel;
  }

  /**
   * Disconnect SSH session if existing.
   */
  protected void disconnectSession() {
    if (session != null) {
      session.disconnect();
    }
  }

}
