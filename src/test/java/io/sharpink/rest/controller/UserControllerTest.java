package io.sharpink.rest.controller;

import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static io.sharpink.rest.controller.UserMockUtil.USER_RESPONSE_MOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  UserService userServiceMock;

  @InjectMocks
  UserController userController = new UserController(userServiceMock);

  @Test
  public void getUsers() {
    // given
    Mockito.when(userServiceMock.getAllUsers()).thenReturn(Collections.singletonList(USER_RESPONSE_MOCK));

    // when
    List<UserResponse> users = userController.getUsers();

    // then
    Mockito.verify(userServiceMock).getAllUsers();
    assertEquals(1, users.size());
    assertEquals(1L, users.get(0).getId());
    assertEquals("Batman", users.get(0).getNickname());
    assertEquals("dark-knight@gotham.com", users.get(0).getEmail());
  }

}
