package caching.config.utils;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import lombok.extern.slf4j.Slf4j;
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

    ConsolePanel.simplePanel(title, subTitle);
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

      ConsolePanel.simplePanel(
           title,
           container.getContainerName(),
           container.getReplicaSetUrl(),
           "" + container.isRunning()
      );
    }
  }


  public static void globalComposeServiceContainerMessage(
       DockerComposeContainer<?> compose, String service, Integer port) {

    if (compose != null) {
      ConsolePanel.simplePanel(
           "TC-CONTAINER-COMPOSE",
           service,
           compose.getServiceHost(service, port),
           compose.getServicePort(service, port)
                  .toString(),
           "" + compose.getContainerByServiceName(service + "_1")
                       .get()
                       .isCreated(),
           "" + compose.getContainerByServiceName(service + "_1")
                       .get()
                       .isRunning()
      );
    }
  }

  private static class ConsolePanel {

    public static void simplePanel(String... texts) {

      panel(
           45,
           5,
           1,
           1,
           Border.DOUBLE,
           Border.DOUBLE,
           Border.THIN,
           Border.THIN,
           true,
           true,
           texts
      );
    }

    public static void simplePanelScalable(int scale, String... texts) {

      panel(
           scale,
           5,
           1,
           1,
           Border.DOUBLE,
           Border.DOUBLE,
           Border.THIN,
           Border.THIN,
           true,
           true,
           texts
      );
    }

    public static void panel(
         int scale,
         int margin,
         int upSpace,
         int downSpace,
         Border cornersFormat,
         Border centerMarksFormat,
         Border horizontalLinesFormat,
         Border verticalLinesFormat,
         boolean uppercaseTitle,
         boolean centralizeTitle,
         String... titleAndOthers) {

      var estimatedAdjustmentFactor = 3;
      var title = titleAndOthers[0];
      var marginTitle = scale - (title.length() / 2) - estimatedAdjustmentFactor;
      var formattedTexts =
           Stream.of(titleAndOthers)
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

      var whitespaceMargin = " ".repeat(marginLimitedBySize);
      var externalUpSpaces = "\n".repeat(upSpace);
      var externalBottomSpaces = "\n".repeat(downSpace);

      var upperFace = upperLine(scale, cornersFormat, centerMarksFormat, horizontalLinesFormat);
      var divider = middleLine(scale, cornersFormat, centerMarksFormat, horizontalLinesFormat);
      var bottomFace = bottomLine(scale, cornersFormat, centerMarksFormat, horizontalLinesFormat);
      var faceLine = faceLine(verticalLinesFormat);

      var titleTextArea = String.valueOf(fullSize);
      var textPreparation = new StringBuilder();
      textPreparation.append(externalUpSpaces)
                     .append(upperFace)
                     .append(faceLine)
                     .append("%s%%-%ss".formatted(whitespaceMargin, titleTextArea))
                     .append(faceLine)
                     .append("\n")
                     .append(divider);

      // "-1" Because the first element in the Array was used as title
      // The discont-number in bodyTextArea/fullsize, subtract the size of "ordinal-ASC" and ") "
      var bodyTextArea = String.valueOf(fullSize - 4);
      var topicEnumeration = 0;
      var ordinalSymbolEnumerator = '\u2070';
      for (int i = formattedTexts.length - 1; i > 0; i--) {
        ++ topicEnumeration;
        textPreparation.append(faceLine)
                       .append("%s%s%s) %%-%ss".formatted(
                            whitespaceMargin,
                            topicEnumeration,
                            ordinalSymbolEnumerator,
                            bodyTextArea
                       ))
                       .append(faceLine)
                       .append("\n");
      }
      textPreparation.append(bottomFace)
                     .append(externalBottomSpaces);
      System.out.printf(textPreparation.toString(), formattedTexts);
    }

    private static String generateLine(char baseChar, int scale, char BASE_LINE) {

      return
           String
                .valueOf(baseChar)
                .repeat(scale)
                .replace(baseChar, BASE_LINE);
    }

    private static String upperLine(
         int scale,
         Border corner,
         Border centerMark,
         Border line) {

      ArrayList<Character> borderStylingItems = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          borderStylingItems.add(BoldFont.UPPER_LEFT_CORNER.code);
          borderStylingItems.add(BoldFont.UPPER_RIGHT_CORNER.code);
        }
        case THIN -> {
          borderStylingItems.add(ThinFont.UPPER_LEFT_CORNER.code);
          borderStylingItems.add(ThinFont.UPPER_RIGHT_CORNER.code);
        }
        case DOUBLE -> {
          borderStylingItems.add(DoubleFont.UPPER_LEFT_CORNER.code);
          borderStylingItems.add(DoubleFont.UPPER_RIGHT_CORNER.code);
        }
      }

      switch (centerMark) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      switch (line) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      var baseline = generateLine('_', scale, borderStylingItems.get(3));

      return borderStylingItems.get(0) + baseline +
             borderStylingItems.get(2) + baseline +
             borderStylingItems.get(1) + "\n";
    }

    private static String middleLine(
         int scale,
         Border corner,
         Border centerMark,
         Border baseLine
    ) {

      ArrayList<Character> borderStylingItems = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          borderStylingItems.add(BoldFont.MIDDLE_LEFT.code);
          borderStylingItems.add(BoldFont.MIDDLE_RIGHT.code);
        }
        case THIN -> {
          borderStylingItems.add(ThinFont.MIDDLE_LEFT.code);
          borderStylingItems.add(ThinFont.MIDDLE_RIGHT.code);
        }
        case DOUBLE -> {
          borderStylingItems.add(DoubleFont.MIDDLE_LEFT.code);
          borderStylingItems.add(DoubleFont.MIDDLE_RIGHT.code);
        }
      }

      switch (centerMark) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      switch (baseLine) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      var divider = generateLine('_', scale, borderStylingItems.get(3));

      return borderStylingItems.get(0) + divider +
             borderStylingItems.get(2) + divider +
             borderStylingItems.get(1) + "\n";
    }

    private static String bottomLine(
         int scale,
         Border corner,
         Border centerMark,
         Border baseLine) {

      ArrayList<Character> borderStylingItems = new ArrayList<>();
      switch (corner) {
        case BOLD -> {
          borderStylingItems.add(BoldFont.LOWER_LEFT_CORNER.code);
          borderStylingItems.add(BoldFont.LOWER_RIGHT_CORNER.code);
        }
        case THIN -> {
          borderStylingItems.add(ThinFont.LOWER_LEFT_CORNER.code);
          borderStylingItems.add(ThinFont.LOWER_RIGHT_CORNER.code);
        }
        case DOUBLE -> {
          borderStylingItems.add(DoubleFont.LOWER_LEFT_CORNER.code);
          borderStylingItems.add(DoubleFont.LOWER_RIGHT_CORNER.code);
        }
      }

      switch (centerMark) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      switch (baseLine) {
        case BOLD -> borderStylingItems.add(BoldFont.BASE_LINE.code);
        case THIN -> borderStylingItems.add(ThinFont.BASE_LINE.code);
        case DOUBLE -> borderStylingItems.add(DoubleFont.BASE_LINE.code);
      }

      var baseline = generateLine('_', scale, borderStylingItems.get(3));

      return borderStylingItems.get(0) + baseline +
             borderStylingItems.get(2) + baseline +
             borderStylingItems.get(1) + "\n";
    }

    private static Character faceLine(Border corner) {

      return switch (corner) {
        case BOLD -> BoldFont.FACE_LINE.code;
        case THIN -> ThinFont.FACE_LINE.code;
        case DOUBLE -> DoubleFont.FACE_LINE.code;
      };
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

    private enum Border {
      BOLD, THIN, DOUBLE
    }
  }
}