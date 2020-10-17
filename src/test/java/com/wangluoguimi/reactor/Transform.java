package com.wangluoguimi.reactor;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class Transform {

  @Test
  public void skipAFew() {
    Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred").skip(3);
    StepVerifier.create(countFlux).expectNext("ninety nine", "one hundred").verifyComplete();
  }

  @Test
  public void skipAFewSeconds(){
    Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
        .delayElements(Duration.ofSeconds(1))
        .skip(Duration.ofSeconds(4));


    StepVerifier.create(countFlux)
        .expectNext("ninety nine", "one hundred")
        .verifyComplete();
  }

  @Test
  public void take(){
    Flux<String> nationalParkFlux = Flux.just("Tyellowstone","Yosemite", "Grand Canyou", "Zion", "Acadia")
        .take(3);

    StepVerifier.create(nationalParkFlux)
        .expectNext("Tyellowstone","Yosemite", "Grand Canyou")
        .verifyComplete();
  }

  @Test
  void takeForAwhile() {
    Flux <String> nationalParkFlux =  Flux.just("Tyellowstone","Yosemite", "Grand Canyou", "Zion", "Acadia")
        .delayElements(Duration.ofSeconds(1))
        .take(Duration.ofMillis(3500));

    StepVerifier.create(nationalParkFlux)
        .expectNext("Tyellowstone","Yosemite", "Grand Canyou")
        .verifyComplete();
  }

  @Test
  void filter() {
    Flux<String> nationalParkFlux = Flux.just("Tyellowstone","Yosemite", "Grand Canyou", "Zion", "Grand Teton")
        .filter(np -> !np.contains(" "));

    StepVerifier.create(nationalParkFlux)
        .expectNext("Tyellowstone","Yosemite",  "Zion" )
        .verifyComplete();
  }

  @Test
  void distinct() {
    Flux<String> animalFlux = Flux.just("dog", "cat", "bird", "dog","bird", "anteater")
        .distinct();

    StepVerifier.create(animalFlux).expectNext("dog", "cat", "bird", "anteater").verifyComplete();
  }

  @Data
  private static class Player{
    private final String firstName;
    private final String lastName;
  }

  @Test
  void map() {
    Flux<Player> playerFlux = Flux.just("Micheal Jordan", "Scottie Pippen", "Steve Kerr")
        .map(n -> {
          String[] split = n.split("\\s");
          return new Player(split[0], split[1]);
        });

    StepVerifier.create(playerFlux)
        .expectNext(new Player("Micheal", "Jordan"))
        .expectNext(new Player("Scottie", "Pippen"))
        .expectNext(new Player("Steve", "Kerr"))
        .verifyComplete();
  }


  @Test
  void flatMap() {
    Flux<Player> playerFlux = Flux.just("Micheal Jordan", "Scottie Pippen", "Steve Kerr")
        .flatMap(n -> Mono.just(n)
        .map(p->{
          String[] split = p.split("\\s");
          return new Player(split[0], split[1]);
        }).subscribeOn(Schedulers.parallel()));

    List<Player> playerList = Arrays.asList(
        new Player("Micheal", "Jordan"),
        new Player("Scottie", "Pippen"),
        new Player("Steve", "Kerr")
    );
    StepVerifier.create(playerFlux)
        .expectNextMatches(playerList::contains)
        .expectNextMatches(playerList::contains)
        .expectNextMatches(playerList::contains)
        .verifyComplete();
  }
}
