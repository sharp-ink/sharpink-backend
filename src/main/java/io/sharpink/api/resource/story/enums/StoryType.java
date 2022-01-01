package io.sharpink.api.resource.story.enums;

/**
 * Cette Enum représente le genre de l'histoire (fantasy, science fiction, biographie,...)
 */
public enum StoryType {

  FANTASY("FANTASY"),
  SCI_FI("SCI_FI"),
  BIOGRAPHY("BIOGRAPHY"),
  ADVENTURE("ADVENTURE"),
  ROMANCE("ROMANCE"),
  UNDETERMINED("UNDETERMINED");

  private final String value;

  StoryType(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
