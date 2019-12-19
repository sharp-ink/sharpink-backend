package io.sharpink.rest.dto.story;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.sharpink.rest.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDto {

	private Long id;

	private String title;

	private String type;

	private boolean originalStory;

	private String status;

	private String summary;

	private boolean published;

	private Long authorId;

	private Long chaptersNumber;

	private MemberDto author; // peut être null

	private List<ChapterDto> chapters; // peut être null

	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
	private LocalDateTime creationDate;

	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
	private LocalDateTime lastModificationDate;

  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
	private LocalDateTime finalReleaseDate;

}
