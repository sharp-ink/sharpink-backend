package io.sharpink.api.resource.user;

import io.sharpink.api.resource.user.dto.UserPatchRequest;
import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.service.UserService;
import io.sharpink.api.shared.exception.CustomApiError;
import io.sharpink.api.shared.exception.NotFound404Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            UserResponse user = userService.getUser(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NotFound404Exception e) {
            return new ResponseEntity<>(new CustomApiError(e.getReason().name(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/stories")
    public ResponseEntity<?> getStories(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userService.getStories(id), HttpStatus.OK);
        } catch (NotFound404Exception e) {
            return new ResponseEntity<>(new CustomApiError(e.getReason().name(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserPatchRequest userPatchRequest) {
        return userService.updateUser(id, userPatchRequest);
    }

    @GetMapping("/{id}/preferences")
    public UserPreferencesDto getUserPreferences(@PathVariable Long id) {
        return userService.getPreferences(id);
    }

    @PutMapping("/{id}/preferences")
    public UserPreferencesDto updateUserPreferences(@PathVariable Long id, @RequestBody UserPreferencesDto userPreferencesDto) {
        return userService.updateUserPreferences(id, userPreferencesDto);
    }
}
