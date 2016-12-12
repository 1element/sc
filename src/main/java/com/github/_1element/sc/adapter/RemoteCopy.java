package com.github._1element.sc.adapter;

import com.github._1element.sc.events.RemoteCopyEvent;
import org.springframework.context.event.EventListener;

/**
 * Remote copy interface.
 */
public interface RemoteCopy {

  /**
   * Listen to remote copy events and handle copy action.
   *
   * @param remoteCopyEvent remote copy event
   */
  @EventListener
  void handle(RemoteCopyEvent remoteCopyEvent);

  /**
   * Remove old files. Should be scheduled.
   */
  void cleanup();

}
