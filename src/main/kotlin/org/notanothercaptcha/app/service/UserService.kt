package org.notanothercaptcha.app.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class UserService {

    @Autowired
    private lateinit var authorizedClientService: OAuth2AuthorizedClientService

    private val reference = object : ParameterizedTypeReference<Map<String, String>>() {

    }

    fun getUserData(authentication: OAuth2AuthenticationToken): Mono<Map<String, String>> {
        val authorizedClient = this.getAuthorizedClient(authentication)
        val userInfoEndpointUri = authorizedClient.clientRegistration
                .providerDetails.userInfoEndpoint.uri

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            return WebClient.builder()
                    .filter(oauth2Credentials(authorizedClient))
                    .build()
                    .get()
                    .uri(userInfoEndpointUri)
                    .retrieve()
                    .bodyToMono(reference)
        }
        return Mono.empty()
    }


    private fun getAuthorizedClient(authentication: OAuth2AuthenticationToken): OAuth2AuthorizedClient {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.authorizedClientRegistrationId, authentication.name)
    }

    private fun oauth2Credentials(authorizedClient: OAuth2AuthorizedClient): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            val authorizedRequest = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizedClient.accessToken.tokenValue)
                    .build()
            Mono.just(authorizedRequest)
        }
    }
}