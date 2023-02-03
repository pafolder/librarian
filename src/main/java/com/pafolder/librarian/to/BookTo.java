package com.pafolder.librarian.to;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTo {
    @NotBlank
    private String author;
    @NotBlank
    private String title;
    @NotBlank
    private String location;
    @Min(0)
    @Max(1)
    private int amount = 1;
}
