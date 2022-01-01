package io.sharpink.api.resource.forumThread;

import io.sharpink.api.resource.forumThread.dto.ForumMessageRequest;
import io.sharpink.api.resource.forumThread.dto.ForumThreadRequest;
import io.sharpink.api.resource.forumThread.dto.search.ForumThreadSearch;
import io.sharpink.api.resource.forumThread.dto.ForumThreadResponse;
import io.sharpink.api.shared.exception.CustomApiError;
import io.sharpink.api.shared.exception.MissingEntity;
import io.sharpink.api.shared.exception.NotFound404Exception;
import io.sharpink.api.resource.forumThread.service.ForumThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/threads")
public class ForumThreadController {

    private final ForumThreadService forumThreadService;

    @Autowired
    public ForumThreadController(ForumThreadService forumThreadService) {
        this.forumThreadService = forumThreadService;
    }

    @GetMapping(value = "")
    public List<ForumThreadResponse> getThreads() {
        return forumThreadService.getAllThreads();
    }

    @PostMapping("/search")
    public List<ForumThreadResponse> search(@RequestBody ForumThreadSearch threadSearch) {
        return forumThreadService.searchThreads(threadSearch);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createThread(@RequestBody @Valid ForumThreadRequest forumThreadRequest) {
        try {
            long threadId = forumThreadService.createThread(forumThreadRequest);
            return new ResponseEntity<>(threadId, CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ForumThreadResponse getThread(@PathVariable Long id) {
        Optional<ForumThreadResponse> optionalThreadResponse = forumThreadService.getThread(id);
        if (optionalThreadResponse.isPresent()) {
            return optionalThreadResponse.get();
        } else {
            throw new NotFound404Exception(MissingEntity.FORUM_THREAD);
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<?> createMessage(@PathVariable Long id, @RequestBody @Valid ForumMessageRequest forumMessageRequest) {
        try {
            long messageId = forumThreadService.createMessage(id, forumMessageRequest);
            return new ResponseEntity<>(messageId, CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeThread(@PathVariable Long id) {
        try {
            forumThreadService.removeThread(id);
            return new ResponseEntity<>(NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomApiError(null, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}/messages/{messageNumber}")
    public ResponseEntity<?> removeMessage(@PathVariable Long id, @PathVariable int messageNumber) {
        forumThreadService.removeMessage(id, messageNumber);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
