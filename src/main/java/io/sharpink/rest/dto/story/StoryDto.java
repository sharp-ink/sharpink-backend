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

	@NotEmpty
	@Size(min = 3, max = 100)
	private String title;

	@NotNull
	private String type;

	@NotNull
	private boolean originalStory;

	@NotNull
	private String status;

	@NotNull
	private String summary;

	private boolean published;

	@NotNull
	private Long authorId;

	@NotNull
	private Long chaptersNumber;

	private MemberDto author; // peut être null

	private List<ChapterDto> chapters; // peut être null

	@NotNull
	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
	private LocalDateTime creationDate;

	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
	private LocalDateTime lastModificationDate;

	private LocalDateTime finalReleaseDate;
	
}
