package io.sharpink.rest.dto.story;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterDto implements Comparable<ChapterDto> {
	
	private Integer id;

	private Integer position;

	private String title;

	private String content;

	@Override
	public int compareTo(ChapterDto o) {
		return position.compareTo(o.position);
	}

}
