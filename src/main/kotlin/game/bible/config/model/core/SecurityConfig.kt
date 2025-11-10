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
    private val passwordReset: PasswordReset? = null
    private val jwt: Jwt? = null

    // Note: this getter is required or proxying this field fails
    fun getCorsAllowedHost() = corsAllowedHost
    fun getPasswordReset() = passwordReset
    fun getJwt() = jwt

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Jwt : Serializable {
        private val sessionTimeoutMins: Int? = null
        private val signingSecret: String? = null
        private val authTokenHeader: String? = null
        private val cookieDomain: String? = null
        private val cookieName: String? = null

        fun getSessionTimeoutMins() = sessionTimeoutMins
        fun getSigningSecret() = signingSecret
        fun getAuthTokenHeader() = authTokenHeader
        fun getCookieDomain() = cookieDomain
        fun getCookieName() = cookieName

        companion object {
            @Serial
            private val serialVersionUID = 8857341430531819941L
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class PasswordReset : Serializable {

        private val expireInMins: Long? = null

        fun getExpireInMins() = expireInMins

        companion object {
            @Serial
            private const val serialVersionUID: Long = -4586408218691178200L
        }

    }

    companion object {
        @Serial
        private val serialVersionUID = 4131124171920030926L
        private val log: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }

}
