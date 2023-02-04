package com.pafolder.librarian.controller.admin;

import com.pafolder.librarian.MatcherFactory;
import com.pafolder.librarian.controller.profile.BookController;
import com.pafolder.librarian.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pafolder.librarian.TestData.*;
import static com.pafolder.librarian.controller.admin.AdminBookController.NO_BOOK_FOR_DELETION_FOUND;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllersTest extends AbstractTestController {
    public static final MatcherFactory.Matcher<Book> BOOK_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(
            Book.class, "amount");

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllFromIdToId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BookController.REST_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "%")
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllFromIdToIdWithUnauthorizedException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BookController.REST_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "%")
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void create() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(AdminBookController.REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Created Author\",\"title\": \"Created Book\"," +
                                "\"location\": \"Created Shelf\",\"amount\": 1}"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void createByUserWithForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(AdminBookController.REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Created Author\",\"title\": \"Created Book\"," +
                                "\"location\": \"Created Shelf\",\"amount\": 1}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void createWithValidationException() throws Exception {
        Assertions.assertTrue(mockMvc.perform(MockMvcRequestBuilders.post(AdminBookController.REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Created Author\",\"title\": \"Created Book\"," +
                                "\"location\": \"Created Shelf\",\"amount\": 2}"))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .matches(".*" + "amount: must be less than or equal to" + ".*"));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(AdminBookController.REST_URL + "/" + BOOK_ID_FOR_UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"" + UPDATED_AUTHOR + "\",\"title\": \"Updated Book\"}"))
                .andDo(print())
                .andExpect(status().isNoContent());
        List<Book> updated = bookRepository.findAllByAuthor(UPDATED_AUTHOR);
        Assertions.assertEquals(1, updated.size());
        BOOK_MATCHER.assertMatch(updated.get(0), updatedBook);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void updateNonexistent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(
                                AdminBookController.REST_URL + "/" + NONEXISTENT_ID_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"" + UPDATED_AUTHOR + "\",\"title\": \"Updated Book\"}"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    @Transactional(propagation = Propagation.NEVER)
    void updateWithValidationException() throws Exception {
        Assertions.assertTrue(mockMvc.perform(MockMvcRequestBuilders.put(
                                AdminBookController.REST_URL + "/" + BOOK_ID_FOR_UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\": \"Third Author\",\"title\": \"Third Book\"}"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .matches(".*constraint.*BOOK_UNIQUE_TITLE_AUTHOR_IDX.*"));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                                AdminBookController.REST_URL + "/" + BOOK_ID_TO_DELETE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        Assertions.assertTrue(bookRepository.findById(Integer.valueOf(BOOK_ID_TO_DELETE)).isEmpty());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNonexistent() throws Exception {
        Assertions.assertTrue(mockMvc.perform(MockMvcRequestBuilders.delete(
                                AdminBookController.REST_URL + "/" + NONEXISTENT_ID_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .matches(".*" + NO_BOOK_FOR_DELETION_FOUND + ".*"));
    }
}