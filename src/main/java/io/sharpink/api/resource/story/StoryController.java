package io.sharpink.api.resource.story;

import io.sharpink.api.resource.story.dto.ChapterRequest;
import io.sharpink.api.resource.story.dto.StoryPatchRequest;
import io.sharpink.api.resource.story.dto.StoryRequest;
import io.sharpink.api.resource.story.dto.search.StorySearch;
import io.sharpink.api.resource.story.dto.ChapterResponse;
import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.story.enums.AuthorLoadingStrategy;
import io.sharpink.api.shared.exception.CustomApiError;
import io.sharpink.api.shared.exception.NotFound404Exception;
import io.sharpink.api.shared.exception.UnprocessableEntity422Exception;
import io.sharpink.api.resource.story.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/stories")
public class StoryController {

  private final StoryService storyService;

  @Autowired
  public StoryController(StoryService storyService) {
    this.storyService = storyService;
  }

  /**
   * Gets all {@code Story} with given publication status.
   */
  @GetMapping("")
  public List<StoryResponse> getAllPublicStories() {
    return storyService.getAllPublicStories(AuthorLoadingStrategy.ENABLED);
  }

  /**
   * Gets the {@code Story} with the given id.
   */
  @GetMapping("/{id}")
  public StoryResponse get(@PathVariable Long id) {
    Optional<StoryResponse> optionalStoryResponse = storyService.getStory(id);
    if (optionalStoryResponse.isPresent()) {
      return optionalStoryResponse.get();
    } else {
      throw new NotFound404Exception();
    }
  }

  /**
   * Creates {@code Story} with given information. Returns the id of the saved {@code Story}.
   */
  @PostMapping("")
  public ResponseEntity<?> create(@RequestBody StoryRequest storyRequest) {
    try {
      Long storyId = storyService.createStory(storyRequest);
      return new ResponseEntity<>(storyId, CREATED);
    } catch (UnprocessableEntity422Exception e) {
      return new ResponseEntity<>(new CustomApiError(e.getReason().name(), e.getMessage()), UNPROCESSABLE_ENTITY);
    }
  }

  @PostMapping("/search")
  public List<StoryResponse> search(@RequestBody StorySearch storySearch) {
    return storyService.searchStories(storySearch, AuthorLoadingStrategy.ENABLED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody StoryPatchRequest storyPatchRequest) {
    try {
      StoryResponse storyResponse = storyService.updateStory(id, storyPatchRequest);
      return new ResponseEntity<>(storyResponse, OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeStory(@PathVariable Long id) {
    storyService.removeStory(id);
    return new ResponseEntity<>(NO_CONTENT);
  }

  /**
   * Creates a new chapter in the given story
   */
  @PostMapping("/{storyId}/chapters")
  public ResponseEntity<?> addChapter(@PathVariable Long storyId, @RequestBody ChapterRequest chapterRequest) {
    try {
      long chapterId = storyService.addChapter(storyId, chapterRequest);
      return new ResponseEntity<>(chapterId, CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Updates an existing chapter
   */
  @PutMapping("/{storyId}/chapters/{chapterPosition}")
  public ResponseEntity<?> updateChapter(@PathVariable Long storyId, @PathVariable Long chapterPosition, @RequestBody ChapterRequest chapterRequest) {
    try {
      ChapterResponse chapterResponse = storyService.updateChapter(storyId, chapterPosition.intValue(), chapterRequest);
      return new ResponseEntity<>(chapterResponse, OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(), INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{storyId}/chapters/{chapterPosition}")
  public ResponseEntity<?> removeChapter(@PathVariable Long storyId, @PathVariable Long chapterPosition) {
    try {
      storyService.removeChapter(storyId, chapterPosition);
      return new ResponseEntity<>(NO_CONTENT);
    } catch (NotFound404Exception e) {
      return new ResponseEntity<>(new CustomApiError(e.getReason().name(), e.getMessage()), INTERNAL_SERVER_ERROR);
    }

  }
}
