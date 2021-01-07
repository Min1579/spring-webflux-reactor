package me.min.reactiveweb.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.min.reactiveweb.document.Item;

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

    public Item toItemDocument() {
        return Item.builder()
                .description(description)
                .price(price)
                .build();
    }

}
