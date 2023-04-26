package net.itsmeeandrew.aoe4esports.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig {

    @Bean
    fun getWebClient(): WebClient {
        val size = 16 * 1024 * 1024
        val strategies = ExchangeStrategies.builder()
            .codecs { codecs: ClientCodecConfigurer ->
                codecs.defaultCodecs().maxInMemorySize(size)
            }
            .build()

        return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("User-Agent", "AgeOfEmpires4Esports (localhost / in development; schin.andras01@gmail.com)")
            .exchangeStrategies(strategies)
            .filters { filters -> filters.add(logRequest()) }
            .build()
    }

    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { request ->
            println("Sending request: ${request.method()} ${request.url()}")
            Mono.just(request)
        }
    }
}

