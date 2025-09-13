package game.bible.config.model.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial
import java.io.Serializable

/**
 * Security Configuration
 * @since 5th June 2025
 */
@Reloadable(
    prefix = "security",
    path = "\${application.config.dir}"
)
class SecurityConfig : Initialisable {

    private val corsAllowedHost: List<String>? = null
    private val domainName: String? = null
    private val jwts: Map<String, Jwt>? = null
    private val signingSecret: String? = null

    // Note: this getter is required or proxying this field fails
    fun getCorsAllowedHost() = corsAllowedHost
    fun getDomainName() = domainName
    fun getJwts() = jwts
    fun getSigningSecret() = signingSecret

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Jwt : Serializable {
        private val timeoutMins: Int? = null
        private val tokenHeader: String? = null
        private val cookieDomain: String? = null
        private val cookieName: String? = null

        fun getTimeoutMins() = timeoutMins
        fun getAuthTokenHeader() = tokenHeader
        fun getCookieDomain() = cookieDomain
        fun getCookieName() = cookieName

        companion object {
            @Serial
            private val serialVersionUID = 8857341430531819941L
        }
    }

    companion object {
        @Serial private val serialVersionUID = 4131124171920030926L

        private val log: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }

}
