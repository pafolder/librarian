package com.pafolder.librarian.controller;

import com.pafolder.librarian.MatcherFactory;
import com.pafolder.librarian.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.pafolder.librarian.TestData.newUser;
import static com.pafolder.librarian.TestData.user;
import static com.pafolder.librarian.validator.UserToValidator.DUPLICATING_EMAIL;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegisterControllerTest extends BaseTestController {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(
            User.class, "violations", "enabled", "password");

    @Test
    void register() throws Exception {
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(RegisterController.REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + newUser.getName() + "\", \"email\": \"" +
                                newUser.getEmail() + "\", \"password\": \"" + newUser.getPassword() + "\"}"))
                .andDo(print())
                .andExpect(status().isCreated());
        User created = USER_MATCHER.readFromJson(action);
        USER_MATCHER.assertMatch(created, newUser);
    }

    @Test
    void registerDuplicatedEmail() throws Exception {
        Assertions.assertTrue(mockMvc.perform(MockMvcRequestBuilders.post(RegisterController.REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + newUser.getName() + "\", \"email\": \"" +
                                user.getEmail() + "\", \"password\": \"" + newUser.getPassword() + "\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .matches(".*" + DUPLICATING_EMAIL + ".*"));
    }
}