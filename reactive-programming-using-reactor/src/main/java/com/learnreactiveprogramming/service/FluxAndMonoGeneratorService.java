package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_map(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_immutability() {
        var nameFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        nameFlux.map(String::toUpperCase);
        return nameFlux;
    }

    public Mono<String> namesMono() {
        return Mono.just("alex")
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString)
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString_withDelay)
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitString_withDelay)
                .log(); // db or a remote service call
    }

    public Flux<String> splitString(String name) {
        String[] split = name.split("");
        return Flux.fromArray(split);
    }

    public Flux<String> splitString_withDelay(String name) {
        log.info("splitString_withDelay::name:{}" ,name);
        String[] split = name.split("");
        int i = new Random().nextInt(1000);
        return Flux.fromArray(split)
                .delayElements(Duration.ofMillis(i))
                ;
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        String[] split = s.split("");
        List<String> split1 = List.of(split);//ALEX -> A, L, E, E
        return Mono.just(split1);
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        Function<Flux<String>, Flux<String>> myfilter = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(myfilter)
                .flatMap(this::splitString)
                .log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();
        /*service.namesFlux()
                .subscribe(s -> {
                    System.out.println("Name is :" + s);
                });

        service.namesMono()
                .subscribe(s -> {
                    System.out.println("Name is :" + s);
                });*/
    }
}
