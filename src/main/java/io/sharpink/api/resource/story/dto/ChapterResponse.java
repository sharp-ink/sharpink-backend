package io.sharpink.api.resource.story.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterResponse implements Comparable<ChapterResponse> {

	private Long id;
	private Integer position;
	private String title;
	private String content;

	@Override
	public int compareTo(ChapterResponse o) {
		return position.compareTo(o.position);
	}

}
