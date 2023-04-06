package com.pafolder.librarian.infrastructure.controller;

import static com.pafolder.librarian.TestData.*;
import static com.pafolder.librarian.infrastructure.controller.profile.CheckoutController.BORROWING_PROHIBITED;
import static com.pafolder.librarian.infrastructure.controller.profile.CheckoutController.BORROWING_PROHIBITED_LIMIT_REACHED;
import static com.pafolder.librarian.infrastructure.controller.profile.CheckoutController.CHECKOUT_OF_ANOTHER_USER;
import static com.pafolder.librarian.infrastructure.controller.profile.CheckoutController.MAX_VIOLATIONS;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pafolder.librarian.infrastructure.controller.admin.AdminCheckoutController;
import com.pafolder.librarian.infrastructure.controller.profile.CheckoutController;
import com.pafolder.librarian.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class CheckoutControllersTest extends BaseTestController {

  @Test
  @WithUserDetails(value = ADMIN_MAIL)
  void getOwnCheckouts() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CheckoutController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("toId", String.valueOf(MAX_ID)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void getUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CheckoutController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("toId", String.valueOf(MAX_ID)))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = USER_MAIL)
  void create() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(CheckoutController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", BOOK_ID_FOR_TEST_CHECKOUT))
        .andDo(print())
        .andExpect(status().isCreated());
    Assertions.assertTrue(checkoutRepository.findById(CREATED_CHECKOUT_ID).isPresent());
  }

  @Test
  @WithUserDetails(value = ADMIN_MAIL)
  void createWithLimitReached() throws Exception {
    Assertions.assertTrue(
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(CheckoutController.REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("id", BOOK_ID_FOR_TEST_CHECKOUT))
            .andExpect(status().isUnprocessableEntity())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .matches(".*" + BORROWING_PROHIBITED_LIMIT_REACHED + ".*"));
  }

  @Test
  @WithUserDetails(value = USER_MAIL)
  void createWithViolationsExceeded() throws Exception {
    User user = userService.getByEmail(USER_MAIL);
    user.setViolations(MAX_VIOLATIONS + 1);
    userService.save(user);
    Assertions.assertTrue(
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(CheckoutController.REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("id", BOOK_ID_FOR_TEST_CHECKOUT))
            .andExpect(status().isUnprocessableEntity())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .matches(".*" + BORROWING_PROHIBITED + ".*"));
  }

  @Test
  @WithUserDetails(value = ADMIN_MAIL)
  void checkin() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CheckoutController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(CHECKOUT_ID_FOR_CHECKIN)))
        .andDo(print())
        .andExpect(status().isNoContent());
    Assertions.assertNotNull(
        checkoutRepository.findById(CHECKOUT_ID_FOR_CHECKIN).orElseThrow().getCheckinDateTime());
  }

  @Test
  @WithUserDetails(value = USER_MAIL)
  void checkinNotOwn() throws Exception {
    Assertions.assertTrue(
        mockMvc
            .perform(
                MockMvcRequestBuilders.put(CheckoutController.REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("id", String.valueOf(CHECKOUT_ID_FOR_CHECKIN)))
            .andExpect(status().isConflict())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .matches(".*" + CHECKOUT_OF_ANOTHER_USER + ".*"));
  }

  @Test
  @WithUserDetails(value = ADMIN_MAIL)
  void getAllFromIdToId() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(AdminCheckoutController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("toId", String.valueOf(MAX_ID)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = ADMIN_MAIL)
  void delete() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    AdminCheckoutController.REST_URL + "/" + CHECKOUT_ID_TO_DELETE)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());
    Assertions.assertTrue(checkoutRepository.findById(CHECKOUT_ID_TO_DELETE).isEmpty());
  }

  @Test
  void deleteUnauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    AdminCheckoutController.REST_URL + "/" + CHECKOUT_ID_TO_DELETE)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = USER_MAIL)
  void deleteForbidden() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    AdminCheckoutController.REST_URL + "/" + CHECKOUT_ID_TO_DELETE)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden());
  }
}
