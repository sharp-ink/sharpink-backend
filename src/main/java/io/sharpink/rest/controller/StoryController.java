package io.sharpink.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import io.sharpink.rest.dto.request.story.StoryPatchRequest;
import io.sharpink.rest.dto.request.story.StoryRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.sharpink.service.StoryService;

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
	public List<StoryResponse> getStories(@RequestParam Boolean published) {
		return storyService.getAllStories(published);
	}

	/**
	 * Renvoie la {@code Story} ayant l'id passé en paramètre.
	 */
	@GetMapping("/{id}")
	public StoryResponse getStory(@PathVariable Long id) {
		Optional<StoryResponse> optionalStoryResponse = storyService.getStory(id);
		if (optionalStoryResponse.isPresent()) {
			return optionalStoryResponse.get();
		} else {
			throw new NotFound404Exception();
		}
	}

	/**
	 * Crée la {@code Story} avec les informations fournies. Renvoie l'id généré
	 * lors de l'insertion en base de données.
	 */
	@PostMapping("")
	public ResponseEntity<?> createStory(@RequestBody @Valid StoryRequest storyRequest) {
		try {
		  Long storyId = storyService.createStory(storyRequest);
      return new ResponseEntity<>(storyId, CREATED);
		} catch (UnprocessableEntity422Exception e) {
        return new ResponseEntity<>(new CustomApiError(e.getReason().name()), UNPROCESSABLE_ENTITY);
    }
	}

	@PatchMapping("/{id}")
  public ResponseEntity<?> updateStory(@PathVariable Long id, @RequestBody @Valid StoryPatchRequest storyPatchRequest) {
	  try {
	    StoryResponse storyResponse = storyService.updateStory(id, storyPatchRequest);
      return new ResponseEntity<>(storyResponse, OK);
    } catch (Exception e) {
	    return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR);
    }
  }

  /*@PostMapping("/{id}/chapters")
  public ResponseEntity<?> addChapter(@PathVariable Long id, @RequestBody @Valid ChapterRequest chapterRequest) {
	  try {
      ChapterDto chapterDto storyService.addChapter(storyId, chapterRequest);
    } catch(Exception e) {
	    return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR>);
    }
  }*/
}
