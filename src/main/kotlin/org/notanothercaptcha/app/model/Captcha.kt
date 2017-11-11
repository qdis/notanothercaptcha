package org.notanothercaptcha.app.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

@Document
data class Captcha(@Id var captchaId: String = "",
                   var clientId: String = "",
                   var response: String = "",
                   var data: String = "")

interface CaptchaRepo : ReactiveMongoRepository<Captcha, String>