package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;
import static org.mockito.Mockito.*;
import org.springframework.ui.ConcurrentModel;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UserControllerTest {
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void init() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenGetRegistrationPageThenReturnRegistrationPage() {
        var model = new ConcurrentModel();
        var view = userController.getRegistationPage(model);

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenRegisterNewUserThenRedirectToVacancies() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.save(any())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenRegisterExistingUserThenReturnErrorPage() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    void whenLoginWithValidCredentialsThenRedirectToVacancies() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenLoginWithInvalidCredentialsThenReturnLoginViewWithError() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.findByEmailAndPassword(any(), any()))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var view = userController.loginUser(user, model, request);
        var error = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно");
    }
}
