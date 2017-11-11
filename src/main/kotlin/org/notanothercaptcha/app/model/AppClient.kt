package org.notanothercaptcha.app.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Document
data class AppClient(@Id var clientId: String = "",
                     var secret: String = "",
                     var user: String = "")

interface AppClientRepo : ReactiveMongoRepository<AppClient, String> {

    fun findByClientIdAndSecret(clientId: String, secret: String): Mono<AppClient>

    fun findByUser(user: String): Flux<AppClient>
}