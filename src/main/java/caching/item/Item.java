package caching.item;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item  implements Serializable {

    @Serial
    private static final long serialVersionUID = - 2490398103295450213L;

    @Id
    private String _id;

    @NotEmpty
    private String name;

    private double price;

    @Version
    private Long version;
}