package com.pafolder.librarian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class LibrarianRestApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(LibrarianRestApplication.class, args);
  }
}
