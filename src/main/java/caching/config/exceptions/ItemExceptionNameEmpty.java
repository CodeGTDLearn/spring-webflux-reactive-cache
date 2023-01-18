package caching.config.exceptions;

public class ItemExceptionNameEmpty extends RuntimeException {

  public ItemExceptionNameEmpty(final String message) {
    super(String.format("Item[%s].notFound", message));
  }

}