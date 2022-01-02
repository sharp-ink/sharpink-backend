package io.sharpink.api.resource.user.persistence.user.preferences;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
  BLUE(0), MARINE(1), CANDY(2), BRICK(3), GREY(4), PUMPKIN(5), TURQUOISE(6), BARLEY_SUGAR(7);

  private final Integer value;

  Theme(Integer value) {
    this.value = value;
  }

  @JsonValue
  public Integer value() {
    return this.value;
  }
}
