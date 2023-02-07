package caching.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ItemExceptionAttributes {
  private String detail;
  private String classType;
  private int status;
  private long timeStamp;
}