package caching.config.utils;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.MongoDBContainer;

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
                + "                      %s                        ║\n" + "║ --> Service: %s\n" + "║ --> Host: %s\n" + "║ --> Port: %s\n" + "║ --> Created: %s\n" + "║ --> Running:" + " %s\n" + "╚═══════════════════════════════════════════════════════════════════════\n\n",
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

      generatePanel(22, 5, "myTitcccle", "myBoxxxdy", "myBvvy2", "myBvvy2");
    }

    public static void generatePanel(int size, int margin, String... texts) {

      margin = Math.min(margin, size);
      if (margin % 2 != 0) -- margin;

      if (size % 2 != 0) ++ size;

      int internalValue = (size * 2) - margin;

      if (internalValue % 2 == 0) ++ internalValue;
      else -- internalValue;

      final var internal = String.valueOf(internalValue);

      String myMargin = " ".repeat(margin);
      final var leftSide = "_".repeat(size);
      final var rightSide = "_".repeat(size);

      final var upperLine = "a" + leftSide + "*" + rightSide + "c\n";
      final var middleLine = "d" + leftSide + "*" + rightSide + "f\n";
      final var bottonLine = "g" + leftSide + "*" + rightSide + "i\n";

      var builder = new StringBuilder();
      builder.append(mixedBorder(upperLine));
      builder.append(simpleBorder("|" + myMargin + "%-" + internal + "s|\n"));
      builder.append(mixedBorder(middleLine));

      // "-1" Because the first element in the Array was used as title
      for (int i = texts.length - 1; i > 0; i--)
        builder.append(simpleBorder("|" + myMargin + "%-" + internal + "s|\n"));

      builder.append(mixedBorder(bottonLine));
      System.out.printf(builder.toString(), (Object[]) texts);
    }

    private static String simpleBorder(String str) {

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

    private static String mixedBorder(String str) {
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
                .replace('_', '\u2500')
                .replace('*', '\u2501')
                .replace('|', '\u2502');
    }

  }


}