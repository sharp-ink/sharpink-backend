package io.sharpink.api.shared.service.picture;

import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PictureManagementService {

  /**
   * Store an image on the file system, at specified location.
   * @param base64ImageContent the image obtained from a html form, in base64 format, with html metadata stripped
   * @param destinationPath the location on the file system where the image should be stored (folder + name + extension)
   */
  public void storePictureOnFileSystem(String base64ImageContent, String destinationPath) throws IOException {
    Files.createDirectories(Paths.get(destinationPath).getParent()); // to avoid non existent folders
    Files.write(
      Paths.get(destinationPath),
      DatatypeConverter.parseBase64Binary(base64ImageContent)
    );
  }

}
