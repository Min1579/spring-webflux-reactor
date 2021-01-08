package me.min.reactiveweb.payload;

import lombok.*;
import me.min.reactiveweb.document.Item;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class ItemUpdateDTO {
    private final String id;
    private final String description;
    private final Double price;

    public Item toItemDocument() {
        return Item.builder()
                .description(description)
                .price(price)
                .build();
    }
}
