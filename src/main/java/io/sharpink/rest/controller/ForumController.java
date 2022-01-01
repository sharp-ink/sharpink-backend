package io.sharpink.rest.controller;

import io.sharpink.rest.dto.request.forum.MessageRequest;
import io.sharpink.rest.dto.request.forum.ThreadRequest;
import io.sharpink.rest.dto.request.forum.search.ThreadSearch;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import io.sharpink.rest.exception.CustomApiError;
import io.sharpink.rest.exception.MissingEntity;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/threads")
public class ForumController {

  private final ForumService forumService;

  @Autowired
  public ForumController(ForumService forumService) {
    this.forumService = forumService;
  }

  @GetMapping(value = "")
  public List<ThreadResponse> getThreads() {
    return forumService.getAllThreads();
  }

  @PostMapping("/search")
  public List<ThreadResponse> search(@RequestBody ThreadSearch threadSearch) {
    return forumService.searchThreads(threadSearch);
  }

  @PostMapping(value = "")
  public ResponseEntity<?> createThread(@RequestBody @Valid ThreadRequest threadRequest) {
    try {
      long threadId = forumService.createThread(threadRequest);
      return new ResponseEntity<>(threadId, CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value = "/{id}")
  public ThreadResponse getThread(@PathVariable Long id) {
    Optional<ThreadResponse> optionalThreadResponse = forumService.getThread(id);
    if (optionalThreadResponse.isPresent()) {
      return optionalThreadResponse.get();
    } else {
      throw new NotFound404Exception(MissingEntity.FORUM_THREAD);
    }
  }

  @PostMapping(value = "/{id}")
  public ResponseEntity<?> createMessage(@PathVariable Long id, @RequestBody @Valid MessageRequest messageRequest) {
    try {
      long messageId = forumService.createMessage(id, messageRequest);
      return new ResponseEntity<>(messageId, CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> removeThread(@PathVariable Long id) {
    try {
      forumService.removeThread(id);
      return new ResponseEntity<>(NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value = "/{id}/messages/{messageNumber}")
  public ResponseEntity<?> removeMessage(@PathVariable Long id, @PathVariable int messageNumber) {
    forumService.removeMessage(id, messageNumber);
    return new ResponseEntity<>(NO_CONTENT);
  }
}
