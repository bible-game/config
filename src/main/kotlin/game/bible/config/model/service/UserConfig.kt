package game.bible.config.model.service

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.LoggerFactory
import java.io.Serial
import java.io.Serializable

/**
 * Configuration for the User services
 *
 * @author Hayden Eastwell (haydende)
 */
@Reloadable(
    prefix = "user-service",
    path = "\${application.config.dir}",
)
class UserConfig : Initialisable {

    private val resetUrl: String? = null
    private val comms: Communications? = null

    fun getResetUrl() = resetUrl
    fun getComms() = comms

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    class Communications: Serializable {

        private val apiKey: String? = null
        private val baseUrl: String? = null
        private val secretKey: String? = null
        private val sendAddress: String? = null
        private val templateId: Long? = null

        fun getApiKey() = apiKey
        fun getBaseUrl() = baseUrl
        fun getSecretKey() = secretKey
        fun getSendAddress() = sendAddress
        fun getTemplateId() = templateId

        companion object {
            @Serial
            private val serialVersionUID: Long = 76746274567L
        }

    }

    companion object {
        @Serial
        private val serialVersionUID: Long = 8087528475298487240L
        private val log = LoggerFactory.getLogger(UserConfig::class.java)
    }


}