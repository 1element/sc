package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;

import com.github._1element.sc.exception.FtpRemoteCopyException;
import com.github._1element.sc.properties.FtpRemoteCopyProperties;
import com.github._1element.sc.service.FileService;

/**
 * Abstract ftp remote copy class.
 */
public abstract class AbstractFtpRemoteCopy {

  protected FtpRemoteCopyProperties ftpRemoteCopyProperties;

  protected FTPClient ftp;
  
  protected FileService fileService;

  @Autowired
  public AbstractFtpRemoteCopy(FtpRemoteCopyProperties ftpRemoteCopyProperties, FTPClient ftp, FileService fileService) {
    this.ftpRemoteCopyProperties = ftpRemoteCopyProperties;
    this.ftp = ftp;
    this.fileService = fileService;
  }

  /**
   * Connect to ftp server.
   *
   * @throws FtpRemoteCopyException
   * @throws IOException
   */
  protected void connect() throws FtpRemoteCopyException, IOException {
    ftp.connect(ftpRemoteCopyProperties.getHost());

    if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
      throw new FtpRemoteCopyException("Could not connect to remote ftp server '" + ftpRemoteCopyProperties.getHost() + "'. Response was: " + ftp.getReplyString());
    }

    if (!ftp.login(ftpRemoteCopyProperties.getUsername(), ftpRemoteCopyProperties.getPassword())) {
      throw new FtpRemoteCopyException("Could not login to remote ftp server. Invalid username or password.");
    }

    ftp.setFileType(FTP.BINARY_FILE_TYPE);
    ftp.enterLocalPassiveMode();
  }

  /**
   * Disconnect from ftp server.
   */
  protected void disconnect() {
    if (ftp != null && ftp.isConnected()) {
      try {
        ftp.logout();
        ftp.disconnect();
      } catch (IOException e) {
        // silently ignore disconnect exceptions
      }
    }
  }

}
