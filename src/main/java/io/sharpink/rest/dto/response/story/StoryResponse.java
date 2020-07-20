package io.sharpink.rest.dto.response.story;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.sharpink.rest.dto.response.member.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryResponse {

  private Long id;
  private String title;
  private String type;
  private boolean originalStory;
  private String status;
  private String summary;
  private boolean published;
  private Long authorId;
  private Long chaptersNumber;
  private MemberResponse author; // peut être null
  private List<ChapterResponse> chapters; // peut être null

  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime creationDate;

  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime lastModificationDate;

  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime finalReleaseDate;

}
