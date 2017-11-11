package org.notanothercaptcha.app.ctrl

import mu.KotlinLogging
import org.notanothercaptcha.app.model.AppClientRepo
import org.notanothercaptcha.app.service.CaptchaService
import org.notanothercaptcha.app.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


@Controller
class MainController {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var appClientRepo: AppClientRepo;

    @Autowired
    private lateinit var captchaService: CaptchaService;

    @RequestMapping("/")
    fun index(model: Model, authentication: OAuth2AuthenticationToken): String {
        model.addAttribute("userName", authentication.name)
        val clients = appClientRepo.findByUser(authentication.name).collectList().block()
        model.addAttribute("clients", clients)

        model.addAttribute("tag", "<script src=\"https://notanothercaptcha.com/captcha.js\">")
        model.addAttribute("element", "<div class=\"noc\" data-noc-client-id=\"YOUR_CLIENT_ID\"></div>")

        return "index"
    }

    @RequestMapping("/login")
    fun login(model: Model): String {
        return "login-page"
    }

    @PostMapping("/delete-client")
    fun deleteClient(model: Model, authentication: OAuth2AuthenticationToken, @RequestParam("clientId") clientId: String): String {
        captchaService.deleteClient(clientId, authentication).block();
        return "redirect:/"
    }

    @PostMapping("/create-client")
    fun createClient(model: Model, authentication: OAuth2AuthenticationToken): String {
        captchaService.createClient(authentication).block();
        return "redirect:/"
    }

    @PostMapping("/sample")
    fun samplePost(model: Model, @ModelAttribute sampleForm: SampleForm): String {
        logger.info { "Attempted submit with $sampleForm " }
        var valid = captchaService.validate("system", "secret", sampleForm.nocId, sampleForm.nocResponse);
        logger.info { "Valid : ${valid} "}
        model.addAttribute("valid", valid)
        return "sample"
    }

    @GetMapping("/sample")
    fun sampleGet(model: Model): String {
        model.addAttribute("valid", false)
        return "sample"
    }


}

data class SampleForm(var nocId: String, var nocResponse: String, var name: String)