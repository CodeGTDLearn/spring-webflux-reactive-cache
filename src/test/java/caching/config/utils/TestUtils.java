package caching.config.utils;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.MongoDBContainer;

import java.util.stream.Stream;

import static caching.config.utils.RestAssureSpecs.requestSpecs;
import static caching.config.utils.RestAssureSpecs.responseSpecs;

@Slf4j
public class TestUtils {

  @BeforeAll
  public static void globalBeforeAll() {

    requestSpecs();
    responseSpecs();
  }


  @AfterAll
  public static void globalAfterAll() {

    RestAssuredWebTestClient.reset();
  }


  public static void globalTestMessage(String subTitle, String testType) {


    if (subTitle.contains("repetition"))
      subTitle = "Error: Provide TestInfo testInfo.getTestMethod().toString()";

    if (subTitle.contains("()]")) {
      subTitle = subTitle.replace("()]", "");
      subTitle = subTitle.substring(subTitle.lastIndexOf(".") + 1);
      subTitle = subTitle.substring(0, 1)
                         .toUpperCase() + subTitle.substring(1);
    }


    String title = switch (testType.toLowerCase()) {
      case "class-start" -> " STARTING TEST-CLASS...";
      case "class-end" -> "...FINISHED TEST-CLASS ";
      case "method-start" -> "STARTING TEST-METHOD...";
      case "method-end" -> "...FINISHED TEST-METHOD";
      default -> title = "";
    };

    System.out.printf(

         """
              ╔════════════════════════════════════════════════════════════════════╗
              ║    %-30s   |   %-5s                     ║
              ║ --> Name: %s %38s%n
              ╚════════════════════════════════════════════════════════════════════╝
              """, title, "|", subTitle, "║");
  }


  public static void globalContainerMessage(MongoDBContainer container, String typeTestMessage) {

    if (container != null) {
      String title;
      switch (typeTestMessage.toLowerCase()) {
        case "container-start" -> title = "STARTING TEST-CONTAINER...";
        case "container-end" -> title = "...FINISHED TEST-CONTAINER";
        case "container-state" -> title = "  ...TEST'S TC-CONTAINER  ";
        default -> title = "";
      }

      System.out.printf(
           "╔═══════════════════════════════════════════════════════════════════════╗\n" + "║ " + "-->" + " Name: %s\n" + "║ --> Url: %s\n" + "║ --> Running: %s\n" + "╚═══════════════════════════════════════════════════════════════════════╝\n\n",
           title, container.getContainerName(), container.getReplicaSetUrl(), container.isRunning()
      );
    }
  }


  public static void globalComposeServiceContainerMessage(DockerComposeContainer<?> compose,
                                                          String service, Integer port) {

    if (compose != null) {
      System.out.printf(

           "╔═══════════════════════════════════════════════════════════════════════\n" + "║     "
           + "                      %s                        ║\n" + "║ --> Service: %s\n" + "║ " +
           "--> Host: %s\n" + "║ --> Port: %s\n" + "║ --> Created: %s\n" + "║ --> Running:" + " " +
           "%s\n" + "╚═══════════════════════════════════════════════════════════════════════\n\n",
           "TC-CONTAINER-COMPOSE", service, compose.getServiceHost(service, port),
           compose.getServicePort(service, port), compose.getContainerByServiceName(service + "_1")
                                                         .get()
                                                         .isCreated(),
           compose.getContainerByServiceName(service + "_1")
                  .get()
                  .isRunning()
      );
    }
  }

  private static class ConsolePanel {

    public static void main(String[] args) {

      fullPanel(
           21,
           5,
           3,
           3,
           true,
           true,
           "myTitcc cccc   cccc ccle dddddd", "myBoxxxdy", "myBvvy2", "myBvvy2"
      );
    }

    public static void simplePanel(String... texts) {

      fullPanel(
           21,
           5,
           1,
           1,
           true,
           true,
           texts
      );
    }

    public static void simplePanelWithScale(int scale, String... texts) {

      fullPanel(
           scale,
           5,
           1,
           1,
           true,
           true,
           texts
      );
    }

    public static void fullPanel(
         int scale,
         int margin,
         int upSpace,
         int downSpace,
         boolean uppercaseTitle,
         boolean centralizeTitle,
         String... titleAndOthers) {

      var estimatedAdjustmentFactor = 3;
      var title = titleAndOthers[0];
      var marginTitle = scale - (title.length() / 2) - estimatedAdjustmentFactor;
      var formattedTexts =
           Stream
                .of(titleAndOthers)
                .map(item ->
                          item.equals(title) &&
                          centralizeTitle ?
                               " ".repeat(marginTitle) + title :
                               item)
                .map(item ->
                          item.equals(title) &&
                          uppercaseTitle ?
                               item.toUpperCase() :
                               item)
                .toArray();

      var marginLimitedBySize = Math.min(margin, scale);

      // scale + margin discrepacies eliminated
      if (marginLimitedBySize % 2 != 0) -- marginLimitedBySize;
      if (scale % 2 != 0) ++ scale;

      int fullSize = (scale * 2) - marginLimitedBySize;
      if (fullSize % 2 == 0) ++ fullSize;
      else -- fullSize;

      var internalTextArea = String.valueOf(fullSize);

      var marginAsWhitespaces = " ".repeat(marginLimitedBySize);
      var upperExternalSpaces = "\n".repeat(upSpace);
      var bottomExternalSpaces = "\n".repeat(downSpace);
      var drawline =
           "_".repeat(scale)
              .replace('_', MixedStyle.BASE_LINE.code);

      var divider =
           "_".repeat(scale)
              .replace('_', MixedStyle.BASE_LINE_BOLD.code);

      var upperBorderLine = upperLineGenerator(drawline, BorderStyle.MIXED);
      var middleBorderLine = middleLineGenerator(divider, BorderStyle.MIXED);
      var bottomBorderLine = bottomLineGenerator(drawline, BorderStyle.MIXED);

      var builder = new StringBuilder();
      builder
           .append(upperExternalSpaces)
           .append(upperBorderLine)
           .append(MixedStyle.MIDDLE_FACE.code)
           .append("%s%%-%ss".formatted(marginAsWhitespaces, internalTextArea))
           .append(MixedStyle.MIDDLE_FACE.code)
           .append("\n")
           .append(middleBorderLine)
      ;

      // "-1" Because the first element in the Array was used as title
      for (int i = formattedTexts.length - 1; i > 0; i--)
        builder
             .append(MixedStyle.MIDDLE_FACE.code)
             .append("%s%%-%ss".formatted(marginAsWhitespaces, internalTextArea))
             .append(MixedStyle.MIDDLE_FACE.code)
             .append("\n");

      builder
           .append(bottomBorderLine)
           .append(bottomExternalSpaces);
      System.out.printf(builder.toString(), formattedTexts);
      xxxxx(bottomBorderLine,BorderStyle.BOLD);
    }

    @NotNull
    private static String bottomLineGenerator(String baseline, BorderStyle style) {

      return
           MixedStyle.LOWER_LEFT_CORNER.code + baseline +
           MixedStyle.MIDDLE_CENTER.code + baseline +
           MixedStyle.LOWER_RIGHT_CORNER.code + "\n";
    }

    @NotNull
    private static String middleLineGenerator(String divider, BorderStyle style) {

      return
           MixedStyle.MIDDLE_LEFT.code + divider +
           MixedStyle.MIDDLE_CENTER.code + divider +
           MixedStyle.MIDDLE_RIGHT.code + "\n";
    }

    @NotNull
    private static String upperLineGenerator(String baseline, BorderStyle style) {

      //      var xx = switch (style) {
      //        BOLD -> boldStyle;
      //        SLIM -> simpleStyle;
      //        DOUBLE -> doubleStyle
      //        default -> mixedStyle;
      //      };

      return
           MixedStyle.UPPER_LEFT_CORNER.code + baseline +
           MixedStyle.MIDDLE_CENTER.code + baseline +
           MixedStyle.UPPER_RIGHT_CORNER.code + "\n";
    }

    @NotNull
    private static String xxxxx(String baseline, BorderStyle xx) {

      System.out.println(xx);

      String hhh = switch (xx) {
        BOLD -> "BoldStyle";
        SLIM -> "SimpleStyle";
        DOUBLE -> "DoubleStyle";
        MIXED -> "DoubleStyle";
        default -> throw new IllegalStateException("Invalid day: ");
      };

      return
           MixedStyle.UPPER_LEFT_CORNER.code + baseline +
           MixedStyle.MIDDLE_CENTER.code + baseline +
           MixedStyle.UPPER_RIGHT_CORNER.code + "\n";
    }

    private static String simpleLineStyle(String str) {

      return str.replace('a', '\u250c')
                .replace('b', '\u252c')
                .replace('c', '\u2510')
                .replace('d', '\u251c')
                .replace('e', '\u253c')
                .replace('f', '\u2524')
                .replace('g', '\u2514')
                .replace('h', '\u2534')
                .replace('i', '\u2518')
                .replace('_', '\u2500')
                .replace('|', '\u2502');
    }

    private static String mixedLineStyle(String str) {
      //source: https://en.wikipedia.org/wiki/Box-drawing_character
      return str.replace('a', '\u250F')
                .replace('b', '\u252c')
                .replace('c', '\u2513')
                .replace('d', '\u2523')
                .replace('e', '\u253c')
                .replace('f', '\u252B')
                .replace('g', '\u2517')
                .replace('h', '\u2534')
                .replace('i', '\u251B')
                .replace('-', '\u2501')
                .replace('_', '\u2500')
                .replace('*', '\u2501')
                .replace('|', '\u2502');
    }

    private enum MixedStyle {

      MIDDLE_CENTER('\u2501'),
      BASE_LINE('\u2500'),
      BASE_LINE_BOLD('\u2501'),
      UPPER_LEFT_CORNER('\u250F'),
      UPPER_RIGHT_CORNER('\u2513'),
      MIDDLE_LEFT('\u2523'),
      MIDDLE_RIGHT('\u252B'),
      MIDDLE_FACE('\u2502'),
      LOWER_LEFT_CORNER('\u2517'),
      LOWER_RIGHT_CORNER('\u251B');

      private final char code;

      MixedStyle(char code) {

        this.code = code;
      }
    }

    private enum BoldStyle {

      FACE_LINE('\u2503'),
      BASE_LINE('\u2501'),
      UPPER_LEFT_CORNER('\u250F'),
      UPPER_RIGHT_CORNER('\u2513'),
      MIDDLE_LEFT('\u2523'),
      MIDDLE_RIGHT('\u252B'),
      LOWER_LEFT_CORNER('\u2517'),
      LOWER_RIGHT_CORNER('\u251B');

      private final char code;

      BoldStyle(char code) {

        this.code = code;
      }
    }

    private enum SimpleStyle {

      FACE_LINE('\u2502'),
      BASE_LINE('\u2500'),
      UPPER_LEFT_CORNER('\u250C'),
      UPPER_RIGHT_CORNER('\u2510'),
      MIDDLE_LEFT('\u252C'),
      MIDDLE_RIGHT('\u2524'),
      LOWER_LEFT_CORNER('\u2514'),
      LOWER_RIGHT_CORNER('\u2518');

      private final char code;

      SimpleStyle(char code) {

        this.code = code;
      }
    }

    private enum DoubleStyle {

      FACE_LINE('\u2551'),
      BASE_LINE('\u2550'),
      UPPER_LEFT_CORNER('\u2554'),
      UPPER_RIGHT_CORNER('\u2557'),
      MIDDLE_LEFT('\u2560'),
      MIDDLE_RIGHT('\u2563'),
      LOWER_LEFT_CORNER('\u255A'),
      LOWER_RIGHT_CORNER('\u255D');

      private final char code;

      DoubleStyle(char code) {

        this.code = code;
      }
    }

    private enum BorderStyle {
      BOLD,
      MIXED,
      SLIM,
      DOUBLE;
    }
  }


}