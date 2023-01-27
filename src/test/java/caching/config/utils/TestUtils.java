package caching.config.utils;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Arrays;
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

      fullPanel(21, 5, 3, 3,
                "myTitcccle",
                "myBoxxxdy", "myBvvy2", "myBvvy2"
      );
    }


    public static void simplePanel(
         int scale,
         int margin,
         int upSpace,
         int downSpace,
         String... titleAndOthers) {

      fullPanel(
           21,
           5,
           1,
           1,
           titleAndOthers
      );
    }

    public static void simplePanelWithSize(
         int scale,
         int margin,
         int upSpace,
         int downSpace,
         String... titleAndOthers) {

      fullPanel(
           scale,
           5,
           1,
           1,
           titleAndOthers
      );
    }

    public static void fullPanel(
         int scale,
         int margin,
         int upSpace,
         int downSpace,
         String... titleAndOthers) {

           Stream.of(titleAndOthers)
                .filter(title -> title.equals(titleAndOthers[0]))
                .map(String::toUpperCase)
                .map(tile -> {
                  return Arrays.array(tile, Arrays.toString(titleAndOthers));
                })
                .forEach(System.out::println);

      var marginLimitedBySize = Math.min(margin, scale);

      // scale + margin discrepacies eliminated
      if (marginLimitedBySize % 2 != 0) -- marginLimitedBySize;
      if (scale % 2 != 0) ++ scale;

      int fullSize = (scale * 2) - marginLimitedBySize;
      if (fullSize % 2 == 0) ++ fullSize;
      else -- fullSize;

      var internalTextSpace = String.valueOf(fullSize);

      var marginAsString = " ".repeat(marginLimitedBySize);
      var upperSpace = "\n".repeat(upSpace);
      var lowerSpace = "\n".repeat(downSpace);
      var baseline =
           "_".repeat(scale)
              .replace('_', mixedStyle.BASE_LINE.code);

      var divider =
           "_".repeat(scale)
              .replace('_', mixedStyle.BASE_LINE_BOLD.code);

      var upperLine = upperLineGenerator(baseline, borderStyle.MIXED);
      var middleLine = middleLineGenerator(divider, borderStyle.MIXED);
      var bottomLine = bottomLineGenerator(baseline, borderStyle.MIXED);

      var builder = new StringBuilder();
      builder
           .append(upperSpace)
           .append(upperLine)

           .append(mixedStyle.MIDDLE_FACE.code)
           .append("%s%%-%ss".formatted(marginAsString, internalTextSpace))
           .append(mixedStyle.MIDDLE_FACE.code)
           .append("\n")
           .append(middleLine)
      ;

      // "-1" Because the first element in the Array was used as title
      for (int i = titleAndOthers.length - 1; i > 0; i--)
        builder
             .append(mixedStyle.MIDDLE_FACE.code)
             .append("%s%%-%ss".formatted(marginAsString, internalTextSpace))
             .append(mixedStyle.MIDDLE_FACE.code)
             .append("\n");

      builder
           .append(bottomLine)
           .append(lowerSpace);
      System.out.printf(builder.toString(), (Object[]) titleAndOthers);
    }

    @NotNull
    private static String bottomLineGenerator(String baseline, borderStyle style) {

      return
           mixedStyle.LOWER_LEFT_CORNER.code + baseline +
           mixedStyle.MIDDLE_CENTER.code + baseline +
           mixedStyle.LOWER_RIGHT_CORNER.code + "\n";
    }

    @NotNull
    private static String middleLineGenerator(String divider, borderStyle style) {

      return
           mixedStyle.MIDDLE_LEFT.code + divider +
           mixedStyle.MIDDLE_CENTER.code + divider +
           mixedStyle.MIDDLE_RIGHT.code + "\n";
    }

    @NotNull
    private static String upperLineGenerator(String baseline, borderStyle style) {

//      var xx = switch (style) {
//        BOLD -> boldStyle;
//        SLIM -> simpleStyle;
//        DOUBLE -> doubleStyle
//        default -> mixedStyle;
//      };

      return
           mixedStyle.UPPER_LEFT_CORNER.code + baseline +
           mixedStyle.MIDDLE_CENTER.code + baseline +
           mixedStyle.UPPER_RIGHT_CORNER.code + "\n";
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

    private enum mixedStyle {

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

      mixedStyle(char code) {

        this.code = code;
      }
    }

    private enum boldStyle {

      FACE_LINE('\u2503'),
      BASE_LINE('\u2501'),
      UPPER_LEFT_CORNER('\u250F'),
      UPPER_RIGHT_CORNER('\u2513'),
      MIDDLE_LEFT('\u2523'),
      MIDDLE_RIGHT('\u252B'),
      LOWER_LEFT_CORNER('\u2517'),
      LOWER_RIGHT_CORNER('\u251B');

      private final char code;

      boldStyle(char code) {

        this.code = code;
      }
    }

    private enum simpleStyle {

      FACE_LINE('\u2502'),
      BASE_LINE('\u2500'),
      UPPER_LEFT_CORNER('\u250C'),
      UPPER_RIGHT_CORNER('\u2510'),
      MIDDLE_LEFT('\u252C'),
      MIDDLE_RIGHT('\u2524'),
      LOWER_LEFT_CORNER('\u2514'),
      LOWER_RIGHT_CORNER('\u2518');

      private final char code;

      simpleStyle(char code) {

        this.code = code;
      }
    }

    private enum doubleStyle {

      FACE_LINE('\u2551'),
      BASE_LINE('\u2550'),
      UPPER_LEFT_CORNER('\u2554'),
      UPPER_RIGHT_CORNER('\u2557'),
      MIDDLE_LEFT('\u2560'),
      MIDDLE_RIGHT('\u2563'),
      LOWER_LEFT_CORNER('\u255A'),
      LOWER_RIGHT_CORNER('\u255D');

      private final char code;

      doubleStyle(char code) {

        this.code = code;
      }
    }

    private enum borderStyle {
      BOLD,
      MIXED,
      SLIM,
      DOUBLE;
    }


  }


}