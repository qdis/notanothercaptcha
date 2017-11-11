package org.notanothercaptcha.app.ctrl

import org.notanothercaptcha.app.model.Captcha
import org.notanothercaptcha.app.service.CaptchaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@CrossOrigin
@RestController
@RequestMapping("/captcha")
class CaptchaCtrl {

    @Autowired
    private lateinit var captchaService: CaptchaService;

    @GetMapping("/generate/{clientId}")
    fun generateCaptcha(@PathVariable("clientId") clientId: String): Mono<Captcha> {
        return captchaService.genCaptcha(clientId)
    }

    @PostMapping("/validate")
    fun validate(@RequestParam("clientId") clientId: String,
                 @RequestParam("secret")  secret: String,
                 @RequestParam("captchaId")  captchaId: String,
                 @RequestParam("response") response : String): Boolean {
        return captchaService.validate(clientId,secret,captchaId,response)
    }

}