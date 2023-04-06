package com.pafolder.librarian.infrastructure.controller;

import static com.pafolder.librarian.infrastructure.controller.profile.CheckoutController.MAX_BORROW_DURATION_IN_DAYS;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.repository.CheckoutRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.converter.json.MappingJacksonValue;

public class ControllerUtil {

  private static final String AUTHOR = "author";
  private static final String TITLE = "title";
  private static final String ID = "id";

  private ControllerUtil() {}

  public static int getFutureViolations(User user) {
    int[] count = new int[] {user.getViolations()};
    List<Checkout> currentCheckouts = user.activeCheckouts();
    if (!currentCheckouts.isEmpty()) {
      currentCheckouts.forEach(
          checkout ->
              count[0] +=
                  Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays()
                          > MAX_BORROW_DURATION_IN_DAYS
                      ? 1
                      : 0);
    }
    return count[0];
  }

  public static <T> MappingJacksonValue getFilteredCheckoutsJson(
      boolean isFullInfoNeeded, T object) {
    SimpleFilterProvider filterProvider =
        new SimpleFilterProvider()
            .addFilter(
                "checkoutJsonFilter",
                isFullInfoNeeded
                    ? SimpleBeanPropertyFilter.filterOutAllExcept(
                        ID, "user", "checkoutDateTime", "checkinDateTime", "book")
                    : SimpleBeanPropertyFilter.filterOutAllExcept(ID, "checkoutDateTime", "book"))
            .addFilter(
                "bookJsonFilter",
                isFullInfoNeeded
                    ? SimpleBeanPropertyFilter.filterOutAllExcept(
                        ID, AUTHOR, TITLE, "location", "amount")
                    : SimpleBeanPropertyFilter.filterOutAllExcept(AUTHOR, TITLE));
    new ObjectMapper()
        .setFilterProvider(filterProvider)
        .setSerializationInclusion(Include.NON_NULL);
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
    mappingJacksonValue.setFilters(filterProvider);
    return mappingJacksonValue;
  }

  public static <T> MappingJacksonValue getFilteredBooksJson(T object) {
    SimpleFilterProvider filterProvider =
        new SimpleFilterProvider()
            .addFilter(
                "bookJsonFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept(
                    ID, AUTHOR, TITLE, "location", "amount"));
    new ObjectMapper().setFilterProvider(filterProvider);
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
    mappingJacksonValue.setFilters(filterProvider);
    return mappingJacksonValue;
  }
}
