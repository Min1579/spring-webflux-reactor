package me.min.reactiveweb.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Document
public class Item {
    @Id
    private final String id;
    private String description;
    private Double price;

    @Builder
    public Item(String id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }
}
