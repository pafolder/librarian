package com.pafolder.cbr.to;

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
    private int amount;
}
