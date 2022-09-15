package caching.config;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*╔═══════════════════════════════╗
  ║ USING CONTAINERS IN THE TESTS ║
  ╠═══════════════════════════════╩════════════════╗
  ║ CONFLICT: TEST-CONTAINERS X DOCKER-CONTAINERS  ║
  ║           THEY DO NOT WORKS TOGETHER           ║
  ╠════════════════════════════════════════════════╩═════════╗
  ║A) TEST-CONTAINERS:                                       ║
  ║A.1) Define @ActiveProfiles                               ║
  ║A.1) STOP+CLEAN DOCKER-CONTAINERS  (DOCKER-BAT-SCRIPT)    ║
  ║A.2) "UNCOMMENT" TEST-CONTAINERS-ANNOTATIONS IN ALL TESTS ║
  ║ - TEST-CLASSES: @Container                               ║
  ║ - Tc-Compose Annotattion: @Testcontainers                ║
  ║ - caching.config.TcContainerReplicaset Annotattion: @ExtendWith(...)    ║
  ║A.3) RUN THE TESTS                                        ║
  ║                                                          ║
  ║B) DOCKER-CONTAINERS:                                     ║
  ║B.1) Define @ActiveProfiles                               ║
  ║B.2) "COMMENT" TEST-CONTAINERS-ANNOTATIONS:               ║
  ║ - TEST-CLASSES: @Container                               ║
  ║ - Tc-Compose Annottation: @Testcontainers                ║
  ║ - caching.config.TcContainerReplicaset Annottation: @ExtendWith(...)    ║
  ║B.3) START DOCKER-CONTAINER (DOCKER-BAT-SCRIPT-PROFILE)   ║
  ║B.4) RUN THE TESTS                                        ║
  ╚══════════════════════════════════════════════════════════╝*/
@Retention(RUNTIME)
@Target({TYPE})
@ExtendWith(TcContainerConfig.class)
public @interface TcContainerReplicaset {
}