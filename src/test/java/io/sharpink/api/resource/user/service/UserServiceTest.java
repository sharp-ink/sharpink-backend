package io.sharpink.api.resource.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.story.enums.ChaptersLoadingStrategy;
import io.sharpink.api.resource.story.persistence.Story;
import io.sharpink.api.resource.story.service.StoryMapper;
import io.sharpink.api.resource.user.dto.UserDetailsResponse;
import io.sharpink.api.resource.user.dto.UserPatchRequest;
import io.sharpink.api.resource.user.dto.UserResponse;
import io.sharpink.api.resource.user.dto.preferences.AppearanceDto;
import io.sharpink.api.resource.user.dto.preferences.UserPreferencesDto;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.user.persistence.user.UserDetails;
import io.sharpink.api.resource.user.persistence.user.UserPreferences;
import io.sharpink.api.resource.user.persistence.user.preferences.Theme;
import io.sharpink.api.shared.enums.StoriesLoadingStrategy;
import io.sharpink.api.shared.exception.MissingEntity;
import io.sharpink.api.shared.exception.NotFound404Exception;
import io.sharpink.api.shared.service.picture.PictureManagementService;
import io.sharpink.config.SharpinkConfiguration;
import io.sharpink.util.JsonUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.util.StoryMockUtil;
import test.util.UserMockUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(SharpinkConfiguration.class)
@TestPropertySource("classpath:application.properties")
class UserServiceTest {

    @MockBean
    UserDao userDaoMock;

    @MockBean
    StoryMapper storyMapperMock;

    @MockBean
    PictureManagementService pictureManagementServiceMock;

    @Spy UserMapper userMapper = new UserMapper(storyMapperMock);

    @Autowired
    SharpinkConfiguration sharpinkConfigurationMock;

    UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userService = new UserService(userDaoMock, userMapper, storyMapperMock, pictureManagementServiceMock, sharpinkConfigurationMock);
    }

    @Test
    void getAllUsers() {
        // given
        // List<User> with an random size of 0 to 4 items
        List<User> userListMock = IntStream.range(0, RandomUtils.nextInt(0, 5))
            .mapToObj(i -> new User())
            .collect(Collectors.toList());
        when(userDaoMock.findAll()).thenReturn(userListMock);
        int usersCount = userListMock.size();

        // when
        List<UserResponse> userResponseList = userService.getAllUsers();

        // then
        verify(userDaoMock).findAll();
        verify(userMapper).toUserResponseList(anyList(), eq(StoriesLoadingStrategy.DISABLED));
        assertThat(userResponseList).isNotNull();
        assertThat(userResponseList.size()).isEqualTo(usersCount);
    }

    @Test
    void getUser_UserExists() {
        // given
        User userMock = new User();
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long id = RandomUtils.nextLong();
        UserResponse userResponse = userService.getUser(id);

        // then
        verify(userDaoMock).findById(id);
        verify(userMapper).toUserResponse(any(User.class), eq(StoriesLoadingStrategy.DISABLED));
        assertThat(userResponse).isNotNull();
    }

    @Test
    void getUser_UserDoesNotExist() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Long id = RandomUtils.nextLong();
        assertThatThrownBy(() -> userService.getUser(id))
            .isInstanceOf(NotFound404Exception.class)
            .hasMessage("User not found for id=" + id)
            .extracting("reason").isEqualTo(MissingEntity.USER);

        // then
        verify(userDaoMock).findById(id);
        verify(userMapper, never()).toUserResponse(any(User.class), any(StoriesLoadingStrategy.class));
    }

    @Test
    void getStories_UserExists() {
        // given
        // A user with a random number of stories (up to 4 stories), each having a different last modification date
        User userMock = User.builder()
            .stories(IntStream.range(0, RandomUtils.nextInt(0, 5))
                .mapToObj(i -> {
                    Story story = StoryMockUtil.getStoryMock();
                    story.setTitle("Story " + i);
                    story.setLastModificationDate(LocalDateTime.now().minusDays(RandomUtils.nextInt(1, 1000)));
                    return story;
                })
                .collect(Collectors.toList()))
            .build();
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));
        when(storyMapperMock.toStoryResponseList(anyList(), any(ChaptersLoadingStrategy.class))).thenCallRealMethod();
        if (userMock.getStories().size() > 0) {
            when(storyMapperMock.toStoryResponse(any(Story.class), any(ChaptersLoadingStrategy.class))).thenCallRealMethod();
        }

        // when
        Long userId = RandomUtils.nextLong();
        List<StoryResponse> storyResponses = userService.getStories(userId);

        // then
        verify(userDaoMock).findById(userId);
        verify(storyMapperMock).toStoryResponseList(userMock.getStories()
            .stream()
            .sorted(reverseOrder())
            .collect(toList()), ChaptersLoadingStrategy.NONE);
        assertThat(storyResponses.size()).isEqualTo(userMock.getStories().size());
        assertThat(storyResponses)
            .isSortedAccordingTo(Comparator.comparing(StoryResponse::getLastModificationDate).reversed());
    }

    @Test
    void getStories_UserDoesNotExist() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Long userId = RandomUtils.nextLong();
        assertThatThrownBy(() -> userService.getStories(userId))
            .isInstanceOf(NotFound404Exception.class)
            .hasMessage("User not found for id=" + userId)
            .extracting("reason").isEqualTo(MissingEntity.USER);

        // then
        verify(userDaoMock).findById(userId);
        verify(storyMapperMock, never()).toStoryResponseList(anyList(), any(ChaptersLoadingStrategy.class));
    }

    @Test
    void updateUserProfile_UserDoesNotExist() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Long id = RandomUtils.nextLong();
        assertThatThrownBy(() -> userService.updateUser(id, any(UserPatchRequest.class)))
            .isInstanceOf(NotFound404Exception.class)
            .hasMessage("User not found for id=" + id)
            .extracting("reason").isEqualTo(MissingEntity.USER);

        // then
        verify(userDaoMock).findById(id);
        verifyNoMoreInteractions(userDaoMock, userMapper);
    }

    @Test
    void updateUserProfile_UserExist_UpdateBasicInfoButNotProfilePicture() {
        // given
        UserPatchRequest userPatchRequest = UserPatchRequest.builder()
            .nickname("Mithrandir")
            .email("gandalf@middle-earth.com")
            .firstName("Gandalf")
            .lastName("The Grey")
            .profilePicture(null) // assume that we don't want to update the profile picture
            .build();

        // assume arbitrary existing profile picture (a one-in-two chance to be empty)
        String profilePictureMock = RandomUtils.nextBoolean() ? null : "http://sharpink.io/sharpink/users/gandalf/gandalf.png";
        User userMock = User.builder()
            .userDetails(UserDetails.builder().profilePicture(profilePictureMock).build())
            .build();
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long id = RandomUtils.nextLong();
        UserResponse updatedUserResponse = userService.updateUser(id, userPatchRequest);

        // then
        verify(userDaoMock).findById(id);
        verify(userMapper).toUserDetails(userPatchRequest);
        verify(userDaoMock).save(userMock);
        verify(userMapper).toUserResponse(userMock, StoriesLoadingStrategy.DISABLED);

        assertThat(updatedUserResponse.getNickname()).isEqualTo("Mithrandir");
        assertThat(updatedUserResponse.getEmail()).isEqualTo("gandalf@middle-earth.com");
        UserDetailsResponse userDetailsResponse = updatedUserResponse.getUserDetails();
        assertThat(userDetailsResponse.getFirstName()).isEqualTo("Gandalf");
        assertThat(userDetailsResponse.getLastName()).isEqualTo("The Grey");
        // the new profile picture should not have been updated
        assertThat(userDetailsResponse.getProfilePicture()).isEqualTo(profilePictureMock);

        verifyNoInteractions(pictureManagementServiceMock);
    }

    @Test
    void updateUserProfile_UserExist_UpdateProfilePicture() throws IOException {
        // given
        UserPatchRequest userPatchRequest = UserPatchRequest.builder()
            .profilePicture("data:image/jpg;base64,/9j/4AAQSkZJRgABAQETCETCETC")
            .build();

        User userMock = User.builder().nickname("Superman").build();
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long id = RandomUtils.nextLong();
        UserResponse updatedUserResponse = userService.updateUser(id, userPatchRequest);

        // then
        verify(userDaoMock).findById(id);

        String expectedProfilePictureFSPath = sharpinkConfigurationMock.getResources().getFileSystemPath() + "/users/Superman/Superman.jpg";
        verify(pictureManagementServiceMock).storePictureOnFileSystem("/9j/4AAQSkZJRgABAQETCETCETC", expectedProfilePictureFSPath);

        String expectedProfilePicture = sharpinkConfigurationMock.getResources().getWebUrl() + "/users/Superman/Superman.jpg";
        assertThat(updatedUserResponse.getUserDetails()).isNotNull();
        assertThat(updatedUserResponse.getUserDetails().getProfilePicture()).isEqualTo(expectedProfilePicture);
    }

    @Test
    void getPreferences_UserDoesNotExist() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Long id = RandomUtils.nextLong();
        assertThatThrownBy(() -> userService.getPreferences(id)).isInstanceOf(NotFound404Exception.class)
            .hasMessage("User not found for id=" + id)
            .extracting("reason")
            .isEqualTo(MissingEntity.USER);

        // then
        verify(userDaoMock).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getPreferences_UserExistsButHasNoPreferences() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(new User()));

        // when
        Long userId = RandomUtils.nextLong();
        UserPreferencesDto userPreferencesDto = userService.getPreferences(userId);

        // then
        verify(userDaoMock).findById(userId);
        assertThat(userPreferencesDto).isEqualTo(new UserPreferencesDto());
    }

    @Test
    void getPreferences_UserExistsAndHasSomePreferences() {
        // given
        User userMock = UserMockUtil.USER_MOCK;
        userMock.setUserPreferences(UserMockUtil.USER_PREFERENCES_MOCK);
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long userId = RandomUtils.nextLong();
        UserPreferencesDto userPreferencesDto = userService.getPreferences(userId);

        // then
        verify(userDaoMock).findById(userId);
        verify(userMapper).toUserPreferencesDto(userMock.getUserPreferences().orElseThrow());
        assertThat(userPreferencesDto).isNotNull();
        assertThat(userPreferencesDto.getAppearance()).isNotNull();
        assertThat(userPreferencesDto.getAppearance().getTheme()).isEqualTo(Theme.CANDY);
    }

    @Test
    void updateUserPreferences_UserDoesNotExist() {
        // given
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Long userId = RandomUtils.nextLong();
        assertThatThrownBy(() -> userService.updateUserPreferences(userId, new JsonPatch(emptyList())))
            .hasMessage("User not found for id=" + userId)
            .extracting("reason").isEqualTo(MissingEntity.USER);

        // then
        verify(userDaoMock).findById(userId);
        verifyNoMoreInteractions(userDaoMock, userMapper);
    }

    @Test
    void updateUserPreferences_UserHasNoPreferences() throws IOException {
        // given
        User userMock = UserMockUtil.USER_MOCK;
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long userId = RandomUtils.nextLong();
        var jsonNode = objectMapper.readTree("[{" +
            "    \"op\" : \"replace\"," +
            "    \"path\" : \"/appearance/theme\"," +
            "    \"value\" : \"" + Theme.MARINE.value() + "\"" +
            "}]"
        );
        var jsonPatch = JsonPatch.fromJson(jsonNode);
        UserPreferencesDto updatedUserPreferencesDto = userService.updateUserPreferences(userId, jsonPatch);

        // then
        verify(userDaoMock).findById(userId);
        verify(userDaoMock).save(userMock);
        verify(userMapper).toUserPreferencesDto(any(UserPreferences.class));
        assertThat(updatedUserPreferencesDto).isNotNull();
        assertThat(updatedUserPreferencesDto.getAppearance()).isNotNull();
        assertThat(updatedUserPreferencesDto.getAppearance().getTheme()).isEqualTo(Theme.MARINE);
    }

    @Test
    void updateUserPreferences_UserHasAlreadySomePreferences() throws IOException {
        // given
        User userMock = UserMockUtil.USER_MOCK;
        Theme originalPreferredTheme = pickRandomTheme();
        userMock.setUserPreferences(UserPreferences.builder()
            .user(userMock)
            .preferences(JsonUtil.toJson(UserPreferencesDto.builder()
                .appearance(AppearanceDto.builder().theme(originalPreferredTheme).build())
                .build()))
            .build());
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        Long userId = RandomUtils.nextLong();
        Theme newTheme = pickRandomThemeDifferentFrom(originalPreferredTheme);
        var jsonNode = objectMapper.readTree("[{" +
            "    \"op\" : \"replace\"," +
            "    \"path\" : \"/appearance/theme\"," +
            "    \"value\" : \"" + newTheme.value() + "\"" +
            "}]"
        );
        var jsonPatch = JsonPatch.fromJson(jsonNode);
        UserPreferencesDto updatedUserPreferencesDto = userService.updateUserPreferences(userId, jsonPatch);

        // then
        verify(userDaoMock).findById(userId);
        verify(userDaoMock).save(userMock);
        verify(userMapper).toUserPreferencesDto(any(UserPreferences.class));
        assertThat(updatedUserPreferencesDto).isNotNull();
        assertThat(updatedUserPreferencesDto.getAppearance()).isNotNull();
        assertThat(updatedUserPreferencesDto.getAppearance()
            .getTheme()).as("Preferred theme should be changed from %s to %s", originalPreferredTheme, newTheme)
            .isEqualTo(newTheme);
    }

    private Theme pickRandomTheme() {
        List<Theme> themeValues = asList(Theme.values());
        Collections.shuffle(themeValues);
        return themeValues.stream().findFirst().orElseThrow();
    }

    private Theme pickRandomThemeDifferentFrom(Theme currentTheme) {
        List<Theme> themeValues = asList(Theme.values());
        Collections.shuffle(themeValues);
        return themeValues.stream().filter(t -> t != currentTheme).findFirst().orElseThrow();
    }
}
