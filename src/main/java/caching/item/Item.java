package caching.item;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item {

    @Id
    private String _id;
    private String name;
    private double price;
}