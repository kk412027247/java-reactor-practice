package com.wangluoguimi.reactor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
public class Logging {

  @Test
  void name() {
    Flux<String> beltColors = Flux.just("white", "yellow", "orange", "green", "purple", "blue")
        .log();
    beltColors.subscribe();
  }


  @Test
  void logMapping() {
    Flux<String> beltColors = Flux.just("white", "yellow", "orange", "green", "purple", "blue")
        .map(String::toUpperCase)
        .log();
    beltColors.subscribe();
  }

  @Test
  void logFlatMapping() throws Exception {
    Flux<String> beltColors = Flux.just("white", "yellow", "orange", "green", "purple", "blue")
        .flatMap(cb -> Mono.just(cb)
            .map(String::toUpperCase)
            .log()
            .subscribeOn(Schedulers.parallel())
        );
    beltColors.subscribe();

    Thread.sleep(3000);
  }
}
