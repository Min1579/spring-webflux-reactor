package me.min.reactiveweb.document;

import lombok.*;
import me.min.reactiveweb.payload.ItemUpdateDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Document
public class Item {
    @Id
    private String id;
    private String description;
    private Double price;

    @Builder
    public Item(String id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

    public Item update(final ItemUpdateDTO payload) {
        return Item.builder()
                .id(payload.getId())
                .description(payload.getDescription())
                .price(payload.getPrice())
                .build();
    }
}
