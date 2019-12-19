package io.sharpink.service.picture;

import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.sharpink.util.picture.PictureUtil.extractBase64Content;

@Service
public class PictureManagementService {

  /**
   * Store an image on the file system, at specified location.
   * @param formImageData the image obtained from a html form, in base64 format and with html metadata
   * @param destinationPath the location on the file system where the image should be stored (folder + name + extension)
   */
  public void storePictureOnFileSystem(String formImageData, String destinationPath) throws IOException {
    String base64Content = extractBase64Content(formImageData);
    Files.createDirectories(Paths.get(destinationPath).getParent()); // to avoid non existent folders
    Files.write(
      Paths.get(destinationPath),
      DatatypeConverter.parseBase64Binary(base64Content)
    );
  }

}
