package me.min.itemclient.domain;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Item {
    private String id;
    private String description;
    private Double price;

    @Builder
    public Item(String id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }
}