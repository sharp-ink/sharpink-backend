package io.sharpink.util;

public interface PictureUtil {

  /**
   * Extract the base64 representation of the image contained in the form data
   * @param formImageData the image obtained from a html form, in base64 format and with html metadata
   * @return the base64 image content
   */
  static String extractBase64Content(String formImageData) {
    return formImageData.split(",")[1];
  }

  /**
   * Return the extension (png, jpg,...) of the base64 form image data
   * @param formImageData the image obtained from a html form, in base64 format and with html metadata.
   * @return the extension of the image
   */
  static String extractExtension(String formImageData) {
    return formImageData.split(",")[0] // get "data:image/XXX;base64" part
      .split(";")[0] // get "data:image/XXX" part
      .split("/")[1]; // get "XXX" part (this is the extension!)
  }

}
