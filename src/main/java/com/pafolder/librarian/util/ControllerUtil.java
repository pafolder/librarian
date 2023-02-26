package com.pafolder.librarian.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.pafolder.librarian.model.Checkout;
import com.pafolder.librarian.model.User;
import com.pafolder.librarian.repository.CheckoutRepository;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.pafolder.librarian.controller.profile.CheckoutController.MAX_BORROW_DURATION_IN_DAYS;

public class ControllerUtil {

  private static final String AUTHOR = "author";
  private static final String TITLE = "title";
  private static final String ID = "id";

  private ControllerUtil() {
  }

  public static int getFutureViolations(User user, CheckoutRepository checkoutRepository) {
    int[] count = new int[]{user.getViolations()};
    List<Checkout> currentCheckouts = checkoutRepository.findAllActiveByUserId(user.getId());
    if (!currentCheckouts.isEmpty()) {
      currentCheckouts.forEach(checkout ->
          count[0] +=
              Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays() >
                  MAX_BORROW_DURATION_IN_DAYS ? 1 : 0);
    }
    return count[0];
  }

  public static <T> MappingJacksonValue getFilteredCheckoutsJson(boolean isFullInfoNeeded,
      T object) {
    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("checkoutJsonFilter",
            isFullInfoNeeded ? SimpleBeanPropertyFilter.filterOutAllExcept(
                ID, "user", "checkoutDateTime", "checkinDateTime", "book") :
                SimpleBeanPropertyFilter.filterOutAllExcept(ID, "checkoutDateTime", "book"))
        .addFilter("bookJsonFilter", isFullInfoNeeded ? SimpleBeanPropertyFilter.filterOutAllExcept(
            ID, AUTHOR, TITLE, "location", "amount") :
            SimpleBeanPropertyFilter.filterOutAllExcept(AUTHOR, TITLE));
    new ObjectMapper().setFilterProvider(filterProvider);
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
    mappingJacksonValue.setFilters(filterProvider);
    return mappingJacksonValue;
  }

  public static <T> MappingJacksonValue getFilteredBooksJson(T object) {
    SimpleFilterProvider filterProvider = new SimpleFilterProvider()
        .addFilter("bookJsonFilter",
            SimpleBeanPropertyFilter.filterOutAllExcept(
                ID, AUTHOR, TITLE, "location", "amount"));
    new ObjectMapper().setFilterProvider(filterProvider);
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
    mappingJacksonValue.setFilters(filterProvider);
    return mappingJacksonValue;
  }
}
