export default {

  /**
   * Append hash fragment with current time to the provided url.
   * This will prevent browsers to cache the (image) object.
   */
  appendHashFragment(url) {
    let plainUrl = url;
    const hashIndex = url.indexOf('#');
    if (hashIndex !== -1) {
      plainUrl = url.substring(0, hashIndex);
    }
    return `${plainUrl}#${new Date().getTime()}`;
  },

};
