package caching.config.utils;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.MongoDBContainer;

import java.util.ArrayList;
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
           + "                      %s                        ║\n" + "║ --> Service: %s\n" + "║ " + "--> Host: %s\n" + "║ --> Port: %s\n" + "║ --> Created: %s\n" + "║ --> Running:" + " " + "%s\n" + "╚═══════════════════════════════════════════════════════════════════════\n\n",
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

      mainPanel(21, 5, 3, 3, true, true, "myTitcc cccc   cccc ccle dddddd", "myBoxxxdy", "myBvvy2",
                "myBvvy2"
      );
    }

    public static void simplePanel(String... texts) {

      mainPanel(21, 5, 1, 1, true, true, texts);
    }

    public static void simplePanelWithScale(int scale, String... texts) {

      mainPanel(scale, 5, 1, 1, true, true, texts);
    }

    public static void mainPanel(int scale, int margin, int upSpace, int downSpace,
                                 boolean uppercaseTitle, boolean centralizeTitle,
                                 String... titleAndOthers) {

      var estimatedAdjustmentFactor = 3;
      var title = titleAndOthers[0];
      var marginTitle = scale - (title.length() / 2) - estimatedAdjustmentFactor;
      var formattedTexts = Stream.of(titleAndOthers)
                                 .map(item -> item.equals(title) && centralizeTitle ? " ".repeat(
                                      marginTitle) + title : item)
                                 .map(item -> item.equals(
                                      title) && uppercaseTitle ? item.toUpperCase() : item)
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
      var baseline = "_".repeat(scale)
                        .replace('_', ThinFont.BASE_LINE.code);

      var divider = "_".repeat(scale)
                       .replace('_', BoldFont.BASE_LINE.code);

      var upperBorder = upperLine(baseline, BorderStyle.DOUBLE, BorderStyle.BOLD);
      var dividerBorder = middleLine(divider, BorderStyle.DOUBLE, BorderStyle.DOUBLE);
      var bottomBorder = bottomLine(baseline, BorderStyle.DOUBLE, BorderStyle.BOLD);

      var builder = new StringBuilder();
      builder.append(upperExternalSpaces)
             .append(upperBorder)
             .append(ThinFont.FACE_LINE.code)
             .append("%s%%-%ss".formatted(marginAsWhitespaces, internalTextArea))
             .append(ThinFont.FACE_LINE.code)
             .append("\n")
             .append(dividerBorder);

      // "-1" Because the first element in the Array was used as title
      for (int i = formattedTexts.length - 1; i > 0; i--)
        builder.append(ThinFont.FACE_LINE.code)
               .append("%s%%-%ss".formatted(marginAsWhitespaces, internalTextArea))
               .append(ThinFont.FACE_LINE.code)
               .append("\n");

      builder.append(bottomBorder)
             .append(bottomExternalSpaces);
      System.out.printf(builder.toString(), formattedTexts);
    }


    @NotNull
    private static String upperLine(
         String baseline,
         BorderStyle corner,
         BorderStyle centerMark) {

      ArrayList<Character> myChars = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          myChars.add(BoldFont.UPPER_LEFT_CORNER.code);
          myChars.add(BoldFont.UPPER_RIGHT_CORNER.code);
        }
        case THIN -> {
          myChars.add(ThinFont.UPPER_LEFT_CORNER.code);
          myChars.add(ThinFont.UPPER_RIGHT_CORNER.code);
        }
        case DOUBLE -> {
          myChars.add(DoubleFont.UPPER_LEFT_CORNER.code);
          myChars.add(DoubleFont.UPPER_RIGHT_CORNER.code);
        }
      }

      switch (centerMark) {
        case BOLD -> myChars.add(BoldFont.BASE_LINE.code);
        case THIN -> myChars.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> myChars.add(DoubleFont.BASE_LINE.code);
      }

      return myChars.get(0) + baseline +
             myChars.get(2) + baseline +
             myChars.get(1) + "\n";
    }

    @NotNull
    private static String middleLine(
         String baseline,
         BorderStyle corner,
         BorderStyle centerMark) {

      ArrayList<Character> myChars = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          myChars.add(BoldFont.MIDDLE_LEFT.code);
          myChars.add(BoldFont.MIDDLE_RIGHT.code);
        }
        case THIN -> {
          myChars.add(ThinFont.MIDDLE_LEFT.code);
          myChars.add(ThinFont.MIDDLE_RIGHT.code);
        }
        case DOUBLE -> {
          myChars.add(DoubleFont.MIDDLE_LEFT.code);
          myChars.add(DoubleFont.MIDDLE_RIGHT.code);
        }
      }

      switch (centerMark) {
        case BOLD -> myChars.add(BoldFont.BASE_LINE.code);
        case THIN -> myChars.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> myChars.add(DoubleFont.BASE_LINE.code);
      }

      return myChars.get(0) + baseline +
             myChars.get(2) + baseline +
             myChars.get(1) + "\n";
    }

    @NotNull
    private static String bottomLine(
         String baseline,
         BorderStyle corner,
         BorderStyle centerMark) {

      ArrayList<Character> myChars = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          myChars.add(BoldFont.LOWER_LEFT_CORNER.code);
          myChars.add(BoldFont.LOWER_RIGHT_CORNER.code);
        }
        case THIN -> {
          myChars.add(ThinFont.LOWER_LEFT_CORNER.code);
          myChars.add(ThinFont.LOWER_RIGHT_CORNER.code);
        }
        case DOUBLE -> {
          myChars.add(DoubleFont.LOWER_LEFT_CORNER.code);
          myChars.add(DoubleFont.LOWER_RIGHT_CORNER.code);
        }
      }

      switch (centerMark) {
        case BOLD -> myChars.add(BoldFont.BASE_LINE.code);
        case THIN -> myChars.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> myChars.add(DoubleFont.BASE_LINE.code);
      }

      return myChars.get(0) + baseline +
             myChars.get(2) + baseline +
             myChars.get(1) + "\n";
    }

    private enum BoldFont {

      FACE_LINE('\u2503'),
      BASE_LINE('\u2501'),
      UPPER_LEFT_CORNER('\u250F'),
      UPPER_RIGHT_CORNER('\u2513'),
      MIDDLE_LEFT('\u2523'),
      MIDDLE_RIGHT('\u252B'),
      LOWER_LEFT_CORNER('\u2517'),
      LOWER_RIGHT_CORNER('\u251B');

      private final char code;

      BoldFont(char code) {

        this.code = code;
      }
    }

    private enum ThinFont {

      FACE_LINE('\u2502'),
      BASE_LINE('\u2500'),
      UPPER_LEFT_CORNER('\u250C'),
      UPPER_RIGHT_CORNER('\u2510'),
      MIDDLE_LEFT('\u252C'),
      MIDDLE_RIGHT('\u2524'),
      LOWER_LEFT_CORNER('\u2514'),
      LOWER_RIGHT_CORNER('\u2518');

      private final char code;

      ThinFont(char code) {

        this.code = code;
      }
    }

    private enum DoubleFont {

      FACE_LINE('\u2551'),
      BASE_LINE('\u2550'),
      UPPER_LEFT_CORNER('\u2554'),
      UPPER_RIGHT_CORNER('\u2557'),
      MIDDLE_LEFT('\u2560'),
      MIDDLE_RIGHT('\u2563'),
      LOWER_LEFT_CORNER('\u255A'),
      LOWER_RIGHT_CORNER('\u255D');

      private final char code;

      DoubleFont(char code) {

        this.code = code;
      }
    }

    private enum BorderStyle {
      BOLD, THIN, DOUBLE
    }
  }


}
//    private static String simpleLineStyle(String str) {
//
//      return str.replace('a', '\u250c')
//                .replace('b', '\u252c')
//                .replace('c', '\u2510')
//                .replace('d', '\u251c')
//                .replace('e', '\u253c')
//                .replace('f', '\u2524')
//                .replace('g', '\u2514')
//                .replace('h', '\u2534')
//                .replace('i', '\u2518')
//                .replace('_', '\u2500')
//                .replace('|', '\u2502');
//    }
//
//    private static String mixedLineStyle(String str) {
//      //source: https://en.wikipedia.org/wiki/Box-drawing_character
//      return str.replace('a', '\u250F')
//                .replace('b', '\u252c')
//                .replace('c', '\u2513')
//                .replace('d', '\u2523')
//                .replace('e', '\u253c')
//                .replace('f', '\u252B')
//                .replace('g', '\u2517')
//                .replace('h', '\u2534')
//                .replace('i', '\u251B')
//                .replace('-', '\u2501')
//                .replace('_', '\u2500')
//                .replace('*', '\u2501')
//                .replace('|', '\u2502');
//    }