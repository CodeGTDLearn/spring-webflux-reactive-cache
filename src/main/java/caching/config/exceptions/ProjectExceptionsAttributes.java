package caching.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProjectExceptionsAttributes {
  //    private String title;
  private String detail;
  private String classType;
  private int status;
  private long timeStamp;
}