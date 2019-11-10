package io.sharpink.rest.endpoint;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import io.sharpink.rest.exception.CustomApiError;
import io.sharpink.rest.exception.UnprocessableEntity422Exception;
import io.sharpink.rest.exception.UnprocessableEntity422ReasonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.sharpink.rest.dto.story.StoryDto;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.StoryService;

import static io.sharpink.rest.exception.UnprocessableEntity422ReasonEnum.TITLE_ALREADY_USED;

@RestController
@RequestMapping("/stories")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 86400)
public class StoryController {

	private StoryService storyService;

	@Autowired
	public StoryController(StoryService storyService) {
		this.storyService = storyService;
	}

	/**
	 * Renvoie toutes les {@code Story}.
	 */
	@GetMapping("")
	public List<StoryDto> getStories() {
		return storyService.getAllStories();
	}

	/**
	 * Renvoie la {@code Story} ayant l'id passé en paramètre.
	 */
	@GetMapping("/{id}")
	public StoryDto getStory(@PathVariable Long id) {
		Optional<StoryDto> optionalStoryDto = storyService.getStory(id);
		if (optionalStoryDto.isPresent()) {
			return optionalStoryDto.get();
		} else {
			throw new NotFound404Exception();
		}
	}

	/**
	 * Crée la {@code Story} avec les informations fournies. Renvoie l'id généré
	 * lors de l'insertion en base de données.
	 */
	@PostMapping("")
	public ResponseEntity<?> createStory(@RequestBody @Valid StoryDto storyDto) {
		System.out.println("POST /stories - storyDto = " + storyDto);
		try {
		  Long storyId = storyService.createStory(storyDto);
      return new ResponseEntity<>(storyId, HttpStatus.CREATED);
		} catch (UnprocessableEntity422Exception e) {
        return new ResponseEntity<>(new CustomApiError(e.getReason().name()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
	}

}
