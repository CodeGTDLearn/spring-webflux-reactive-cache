package caching.config.utils;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@NoArgsConstructor
public class BlockhoundUtils {

  //EXCECOES DE METODOS BLOQUEANTES DETECTADOS PELO BLOCKHOUND:
  static BlockHoundIntegration allowedBlockingCalls =
       builder -> builder
            .allowBlockingCallsInside("java.io.PrintStream",
                                      "write"
                                     )
            .allowBlockingCallsInside("java.io.PrintStream",
                                      "print"
                                     )
            .allowBlockingCallsInside("java.io.PrintStream",
                                      "println"
                                     )
            .allowBlockingCallsInside("java.io.FilterInputStream",
                                      "read"
                                     )
            .allowBlockingCallsInside("java.io.FileOutputStream",
                                      "writeBytes"
                                     )
            .allowBlockingCallsInside("java.io.BufferedOutputStream",
                                      "flushBuffer"
                                     )
            .allowBlockingCallsInside("java.io.BufferedOutputStream",
                                      "flush"
                                     )
            .allowBlockingCallsInside("java.io.OutputStreamWriter",
                                      "flushBuffer"
                                     )
            .allowBlockingCallsInside("java.io.RandomAccessFile",
                                      "read"
                                     )
            .allowBlockingCallsInside("java.io.RandomAccessFile",
                                      "readFully"
                                     )
            //problems with transactions
            .allowBlockingCallsInside("java.util.UUID",
                                      "randomUUID"
                                     )
            .allowBlockingCallsInside("java.io.RandomAccessFile",
                                      "readBytes"
                                     )
            .allowBlockingCallsInside("java.io.RandomAccessFile",
                                      "read"
                                     )
            .allowBlockingCallsInside("java.io.RandomAccessFile",
                                      "readFully"
                                     )
            .allowBlockingCallsInside("java.util.concurrent.ConcurrentMap",
                                      "computeIfAbsent"
                                     );


  public static void blockhoundInstallWithAllAllowedCalls() {

    BlockHound.install(allowedBlockingCalls);
  }


  public static void blockhoundInstallWithSpecificAllowedCalls() {

    BlockHoundIntegration allowedCalls =
         builder -> builder
              .allowBlockingCallsInside("java.util.concurrent.ConcurrentMap",
                                        "computeIfAbsent"
                                       )
              .allowBlockingCallsInside("java.io.FilterInputStream",
                                        "read"
                                       );

    BlockHound.install(allowedCalls);
  }


  public static void blockHoundTestCheck() {

    try {
      FutureTask<?> task = new FutureTask<>(() -> {
        Thread.sleep(0);
        return "";
      });

      Schedulers.parallel()
                .schedule(task);

      task.get(10, TimeUnit.SECONDS);
      Assertions.fail("should fail");
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      Assertions.assertTrue(e.getCause() instanceof BlockingOperationError, "detected");
    }
  }
}