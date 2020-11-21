package io.sharpink.rest.controller;

import io.sharpink.rest.dto.request.user.UserPatchRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.rest.dto.shared.user.preferences.UserPreferencesDto;
import io.sharpink.rest.exception.CustomApiError;
import io.sharpink.rest.exception.MissingEntity;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.UserService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.sharpink.rest.controller.UserMockUtil.USER_RESPONSE_MOCK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  UserService userServiceMock;

  @InjectMocks
  UserController userController = new UserController(userServiceMock);

  @Test
  public void getUsers() {
    // given
    when(userServiceMock.getAllUsers()).thenReturn(Collections.singletonList(USER_RESPONSE_MOCK));

    // when
    List<UserResponse> users = userController.getUsers();

    // then
    verify(userServiceMock).getAllUsers();
    assertEquals(1, users.size());
    assertEquals(1L, users.get(0).getId());
    assertEquals("Batman", users.get(0).getNickname());
    assertEquals("dark-knight@gotham.com", users.get(0).getEmail());
  }

  @Test
  public void getUser_UserExists() {
    // given
    when(userServiceMock.getUser(anyLong())).thenReturn(USER_RESPONSE_MOCK);

    // when
    Long id = RandomUtils.nextLong();
    ResponseEntity<?> responseEntity = userController.getUser(id);

    // then
    ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
    verify(userServiceMock).getUser(idArgumentCaptor.capture());
    assertEquals(id, idArgumentCaptor.getValue());

    assertThat(responseEntity.getBody()).isInstanceOf(UserResponse.class);
    UserResponse userResponse = (UserResponse) responseEntity.getBody();
    AssertableUserResponse assertableUserResponse = buildAssertableUserResponse(userResponse);
    assertThat(assertableUserResponse).isEqualTo(buildAssertableUserResponse(USER_RESPONSE_MOCK));

  }

  @Test
  public void getUser_UserDoesNotExist() {
    // given
    NotFound404Exception notFound404ExceptionMock = new NotFound404Exception(MissingEntity.USER, RandomStringUtils
      .randomAlphabetic(100));
    when(userServiceMock.getUser(anyLong())).thenThrow(notFound404ExceptionMock);

    // when
    Long id = RandomUtils.nextLong();
    ResponseEntity<?> responseEntity = userController.getUser(id);

    // then
    ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
    verify(userServiceMock).getUser(idArgumentCaptor.capture());
    assertEquals(id, idArgumentCaptor.getValue());

    assertThat(responseEntity.getBody()).isInstanceOf(CustomApiError.class);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    CustomApiError customApiError = (CustomApiError) responseEntity.getBody();
    assertThat(customApiError.getCode()).isEqualTo(notFound404ExceptionMock.getReason().toString());
    assertThat(customApiError.getMessage()).isEqualTo(notFound404ExceptionMock.getMessage());
  }

  @Test
  public void getStories() {

    // given
    // a mock list with a random size of 0 to 4 items
    List<StoryResponse> storyListMock = IntStream.range(0, RandomUtils.nextInt(0, 5))
      .mapToObj(i -> new StoryResponse())
      .collect(Collectors.toList());
    when(userServiceMock.getStories(anyLong())).thenReturn(storyListMock);

    // when
    Long id = RandomUtils.nextLong();
    List<StoryResponse> stories = userController.getStories(id);

    // then
    ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
    verify(userServiceMock).getStories(idArgumentCaptor.capture());
    assertEquals(id, idArgumentCaptor.getValue());
    assertThat(stories).isEqualTo(storyListMock);
  }

  @Test
  public void updateUserProfile() {
    // given
    when(userServiceMock.updateUserProfile(anyLong(), any(UserPatchRequest.class))).thenReturn(new UserResponse());

    // when
    Long id = RandomUtils.nextLong();
    UserPatchRequest userPatchRequest = new UserPatchRequest();
    UserResponse userResponse = userController.updateUserProfile(id, userPatchRequest);

    // then
    ArgumentCaptor<Long> idAC = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<UserPatchRequest> userPatchRequestAC = ArgumentCaptor.forClass(UserPatchRequest.class);
    verify(userServiceMock).updateUserProfile(idAC.capture(), userPatchRequestAC.capture());
    assertEquals(id, idAC.getValue());
    assertTrue(userPatchRequest == userPatchRequestAC.getValue());
    assertThat(userResponse).isNotNull();
  }

  @Test
  public void getUserPreferences() {
    // given
    when(userServiceMock.getPreferences(anyLong())).thenReturn(new UserPreferencesDto());

    // when
    Long id = RandomUtils.nextLong();
    UserPreferencesDto userPreferencesDto = userController.getUserPreferences(id);

    // then
    ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
    verify(userServiceMock).getPreferences(idArgumentCaptor.capture());
    assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
    assertThat(userPreferencesDto).isNotNull();
  }

  @Test
  public void updateUserPreferences() {
    // given
    when(userServiceMock.updateUserPreferences(anyLong(), any(UserPreferencesDto.class))).thenReturn(new UserPreferencesDto());

    // when
    Long id = RandomUtils.nextLong();
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto();
    UserPreferencesDto updatedUserPreferencesDto = userController.updateUserPreferences(id, userPreferencesDto);

    // then
    ArgumentCaptor<Long> idAC = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<UserPreferencesDto> userPreferencesDtoAC = ArgumentCaptor.forClass(UserPreferencesDto.class);
    verify(userServiceMock).updateUserPreferences(idAC.capture(), userPreferencesDtoAC.capture());
    assertThat(idAC.getValue()).isEqualTo(id);
    assertTrue(userPreferencesDto == userPreferencesDtoAC.getValue());
    assertThat(updatedUserPreferencesDto).isNotNull();
  }

  private AssertableUserResponse buildAssertableUserResponse(UserResponse userResponse) {
    return AssertableUserResponse.builder()
      .id(userResponse.getId())
      .nickname(userResponse.getNickname())
      .build();
  }

  @Getter
  @Builder
  @EqualsAndHashCode
  private static class AssertableUserResponse {
    Long id;
    String nickname;
  }
}
