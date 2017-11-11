package org.notanothercaptcha.app.service

import mu.KotlinLogging
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.notanothercaptcha.app.model.AppClient
import org.notanothercaptcha.app.model.AppClientRepo
import org.notanothercaptcha.app.model.Captcha
import org.notanothercaptcha.app.model.CaptchaRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*


@Service
class CaptchaService {

    private val logger = KotlinLogging.logger {}


    @Autowired
    private lateinit var appClientRepo: AppClientRepo

    @Autowired
    private lateinit var captchaRepo: CaptchaRepo

    fun createClient(authentication: OAuth2AuthenticationToken): Mono<AppClient> {

        var count = appClientRepo.findByUser(authentication.name).count().block();

        if (count < 5) {
            return appClientRepo.save(AppClient(UUID.randomUUID().toString(), UUID.randomUUID().toString(), authentication.name))
        } else {
            return Mono.empty()
        }
    }

    fun deleteClient(clientId: String, authentication: OAuth2AuthenticationToken): Mono<Void> {
        return appClientRepo.findById(clientId).flatMap {
            if (it.user.equals(authentication.name))
                appClientRepo.deleteById(clientId)
            else
                Mono.empty()
        }
    }

    fun validate(clientId: String, secret: String, captchaId: String, response: String): Boolean {
        logger.info { " Requested for $clientId / $secret" }
        var client = appClientRepo.findByClientIdAndSecret(clientId, secret).block();
        if(client !=null){
            logger.info { "Client Found" }
            var resp = captchaRepo.findById(captchaId).block();
            if(resp!=null) {
                logger.info { "Captcha Code: ${resp.response} - vs $response" }
                return resp.response.equals(response)
            }
        }
        logger.info { "No Match" }
        return false;
    }


    fun genCaptcha(clientId: String): Mono<Captcha> {

        val code = UUID.randomUUID().toString()
        val id = UUID.randomUUID().toString();
        logger.info { "Generated captcha: $code with id ${id}" }

        val base64STR = "data:image\\/jpg;base64," + Base64.getEncoder().encodeToString(QRCode.from(code).to(ImageType.JPG).stream().toByteArray())
        return captchaRepo.save(Captcha(id, clientId, code, base64STR))

    }

    @Scheduled(fixedRate = 10000)
    fun deleteOld() {

    }


}

@Service
class Init : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var appClientRepo: AppClientRepo

    @Autowired
    private lateinit var captchaRepo: CaptchaRepo

    override fun run(vararg args: String?) {
        appClientRepo.save(AppClient("system", "secret", "system")).block()
        logger.info { "Created appClient ${appClientRepo.findByClientIdAndSecret("system","secret").block()}" }
    }


}