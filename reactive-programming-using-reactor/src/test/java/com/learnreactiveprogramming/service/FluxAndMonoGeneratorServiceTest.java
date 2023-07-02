package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.Delayed;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void nameFlux() {
        // given

        // when
        var stringFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(stringFlux)
//                .expectNext("alex", "ben", "chloe")
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {
        // given

        // when
        var fluxMap = fluxAndMonoGeneratorService.namesFlux_map();

        // then
        StepVerifier.create(fluxMap)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        // given

        // when
        var fluxMap = fluxAndMonoGeneratorService.namesFlux_immutability();

        // then
        StepVerifier.create(fluxMap)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }


    @Test
    void namesFlux_mapLength() {
        // given

        // when
        var fluxMap = fluxAndMonoGeneratorService.namesFlux_map(3);

        // then
        StepVerifier.create(fluxMap)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {

        var stringFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(3);
        StepVerifier.create(stringFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {
        var stringFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(3);

        stringFlux.subscribe(s -> {
            System.out.println("test " + s);
        });

        /*while (true) {
            //
        }*/

        /*StepVerifier.create(stringFlux)
                //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                 .expectNextCount(9)
                .verifyComplete();*/
    }

    @Test
    void namesFlux() {
    }

    @Test
    void namesMono() {
    }

    @Test
    void namesFlux_concatmap() {
        var stringFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(3);

        StepVerifier.create(stringFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                //.expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void splitString() {
    }

    @Test
    void splitString_withDelay() {
    }

    @Test
    void namesMono_flatMap() {
        Mono<List<String>> listMono = fluxAndMonoGeneratorService.namesMono_flatMap(3);

        StepVerifier.create(listMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        Flux<String> stringFlux = fluxAndMonoGeneratorService.namesFlux_transform(3);
        StepVerifier.create(stringFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }
}