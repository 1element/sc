package com.github._1element.sc.events; //NOSONAR

import com.github._1element.sc.domain.Camera;

/**
 * Event for received image.
 */
public class ImageReceivedEvent {

  private byte[] image;

  private Camera source;

  public ImageReceivedEvent(byte[] image, Camera source) {
    this.image = image;
    this.source = source;
  }

  public byte[] getImage() {
    return image;
  }

  public Camera getSource() {
    return source;
  }

}
