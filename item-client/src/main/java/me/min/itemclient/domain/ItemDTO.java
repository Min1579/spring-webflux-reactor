package me.min.itemclient.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ItemDTO {

    private final String id;
    private final String description;
    private final Double price;

    public ItemDTO(String description, Double price) {
        this.id = null;
        this.description = description;
        this.price = price;
    }
}
