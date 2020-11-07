package io.sharpink.rest.dto.request.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

  @NotNull
  private Long authorId;

  @NotNull
  private String content;

}
