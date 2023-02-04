package com.pafolder.librarian;

import com.pafolder.librarian.model.Book;
import com.pafolder.librarian.model.User;

public class TestData {
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    public static final String BOOK_ID_FOR_UPDATE = "1";
    public static final String BOOK_ID_TO_DELETE = "7";
    public static final String UPDATED_AUTHOR = "Updated Author";
    public static final int UPDATED_BOOK_ID = 1;
    public static final String NONEXISTENT_ID_STRING = "0";
    public static final String ADMIN_MAIL = "admin@mail.com";
    public static final String USER_MAIL = "user@mail.com";
    public static final User admin = new User(1, "Administrator", ADMIN_MAIL,
            DEFAULT_ADMIN_PASSWORD, true, 1, User.Role.ADMIN);
    public static final User user = new User(2, "User", USER_MAIL,
            DEFAULT_PASSWORD, true, 0, User.Role.USER);
    public static Book updatedBook = new Book(UPDATED_BOOK_ID, "Updated Author", "Updated Book",
            "Shelf 1", 0);

    private TestData() {
    }
}

