package com.pafolder.librarian.infrastructure.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.pafolder.librarian.domain.repository.BookRepository;
import com.pafolder.librarian.domain.repository.CheckoutRepository;
import com.pafolder.librarian.application.service.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public abstract class BaseTestController {
  protected static final Locale RU_LOCALE = new Locale("ru");

  protected static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER =
      new CharacterEncodingFilter();

  static {
    CHARACTER_ENCODING_FILTER.setEncoding("UTF-8");
    CHARACTER_ENCODING_FILTER.setForceEncoding(true);
  }

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired BookRepository bookRepository;
  @Autowired UserServiceImpl userService;
  @Autowired CheckoutRepository checkoutRepository;

  @PostConstruct
  private void postConstruct() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }
}
