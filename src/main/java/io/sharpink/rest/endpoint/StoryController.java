package io.sharpink.rest.endpoint;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import io.sharpink.rest.dto.story.StoryPatchDto;
import io.sharpink.rest.exception.CustomApiError;
import io.sharpink.rest.exception.UnprocessableEntity422Exception;
import io.sharpink.rest.exception.UnprocessableEntity422ReasonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.sharpink.rest.dto.story.StoryDto;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.StoryService;

import static io.sharpink.rest.exception.UnprocessableEntity422ReasonEnum.TITLE_ALREADY_USED;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/stories")
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
	public List<StoryDto> getStories(@RequestParam Boolean published) {
		return storyService.getAllStories(published);
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
		try {
		  Long storyId = storyService.createStory(storyDto);
      return new ResponseEntity<>(storyId, CREATED);
		} catch (UnprocessableEntity422Exception e) {
        return new ResponseEntity<>(new CustomApiError(e.getReason().name()), UNPROCESSABLE_ENTITY);
    }
	}

	@PatchMapping("/{id}")
  public ResponseEntity<?> updateStory(@PathVariable Long id, @RequestBody @Valid StoryPatchDto storyPatchDto) {
	  try {
	    StoryDto storyDto = storyService.updateStory(id, storyPatchDto);
      return new ResponseEntity<>(storyDto, OK);
    } catch (UnprocessableEntity422Exception e) {
      return new ResponseEntity<>(new CustomApiError(e.getReason().name()), UNPROCESSABLE_ENTITY);
    } catch (Exception e) {
	    return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR);
    }
  }

}
