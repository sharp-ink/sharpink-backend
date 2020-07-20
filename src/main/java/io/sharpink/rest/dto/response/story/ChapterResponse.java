package io.sharpink.rest.dto.response.story;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterResponse implements Comparable<ChapterResponse> {

	private Integer id;
	private Integer position;
	private String title;
	private String content;

	@Override
	public int compareTo(ChapterResponse o) {
		return position.compareTo(o.position);
	}

}
