package com.pafolder.librarian.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;

public class JsonFilter {
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";

    private JsonFilter() {
    }

    public static <T> MappingJacksonValue getFilteredCheckoutsJson(boolean isFullInfoNeeded, T object) {
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("checkoutJsonFilter", isFullInfoNeeded ? SimpleBeanPropertyFilter.filterOutAllExcept(
                        "id", "user", "checkoutDateTime", "checkinDateTime", "book") :
                        SimpleBeanPropertyFilter.filterOutAllExcept("id", "checkoutDateTime", "book"))
                .addFilter("bookJsonFilter", isFullInfoNeeded ? SimpleBeanPropertyFilter.filterOutAllExcept(
                        "id", AUTHOR, TITLE, "location", "amount") :
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
                                "id", AUTHOR, TITLE, "location", "amount"));
        new ObjectMapper().setFilterProvider(filterProvider);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }
}
