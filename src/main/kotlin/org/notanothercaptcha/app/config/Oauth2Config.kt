package org.notanothercaptcha.app.config

import org.notanothercaptcha.app.model.OAuthClientRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository


@EnableWebSecurity
class OAuth2LoginConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository;

    @Autowired
    private lateinit var oauthClientRepo: OAuthClientRepo;

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http.csrf().disable()
        http.authorizeRequests().antMatchers("/captcha.js", "/login", "/login-page","/sample","/favicon.ico","/fb.png").permitAll()
        http.authorizeRequests().antMatchers("/captcha/**").permitAll()
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().loginPage("/login")
                .clientRegistrationRepository(this.clientRegistrationRepository)
                .authorizedClientService(this.authorizedClientService())
    }

    @Bean
    fun authorizedClientService(): OAuth2AuthorizedClientService {
        return InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository)
    }


}


//
//
//class MongoOAuth2AuthorizedClientService(val clientRegistrationRepository: ClientRegistrationRepository, val oauthClientRepo: OAuthClientRepo) : OAuth2AuthorizedClientService {
//
//
//    override fun <T : OAuth2AuthorizedClient> loadAuthorizedClient(clientRegistrationId: String, principalName: String): T? {
//        val registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId) ?: return null
//        return oauthClientRepo.findById(getIdentifier(registration, principalName)).block().data as T
//    }
//
//    override fun saveAuthorizedClient(authorizedClient: OAuth2AuthorizedClient, principal: Authentication) {
//
//        val id = this.getIdentifier(authorizedClient.clientRegistration, principal.name)
//
//        oauthClientRepo.save(OAuthClient(id, authorizedClient)).subscribe()
//
//    }
//
//    override fun removeAuthorizedClient(clientRegistrationId: String, principalName: String) {
//        val registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
//        if (registration != null) {
//            oauthClientRepo.deleteById(this.getIdentifier(registration, principalName)).subscribe()
//        }
//    }
//
//    private fun getIdentifier(registration: ClientRegistration, principalName: String): String {
//        val identifier = "[" + registration.registrationId + "][" + principalName + "]"
//        return Base64.getEncoder().encodeToString(identifier.toByteArray())
//    }
//
//}