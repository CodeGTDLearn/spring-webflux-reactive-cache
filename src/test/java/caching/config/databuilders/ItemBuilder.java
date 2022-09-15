package caching.config.databuilders;

import caching.item.Item;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;

@Builder
@Getter
public class ItemBuilder {

  private static final Faker faker = new Faker(new Locale("en-CA.yml"));
  private final Item item;


  public static String createFakeUniqueRandomId() {

    return faker.regexify("PP[a-z0-9]{24}");
  }


  public static ItemBuilder itemNoID() {

    Item item = new Item();

    item.setName(faker.name()
                      .fullName());

    return ItemBuilder.builder()
                      .item(item)
                      .build();
  }

  public static ItemBuilder itemWithID() {

    Item item = new Item();

    item.set_id(createFakeUniqueRandomId());
    item.setName(faker.name()
                      .fullName());

    return ItemBuilder.builder()
                      .item(item)
                      .build();
  }


  public Item create() {

    return this.item;
  }
}