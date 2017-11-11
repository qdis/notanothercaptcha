package org.notanothercaptcha.app.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient

@Document
data class OAuthClient(@Id var identifier: String = "",
                       var data: OAuth2AuthorizedClient)

interface OAuthClientRepo : ReactiveMongoRepository<OAuthClient, String>