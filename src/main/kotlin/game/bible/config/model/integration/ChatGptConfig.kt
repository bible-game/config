package game.bible.config.model.integration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial
import java.io.Serializable

/**
 * ChatGPT Configuration
 * @since 30th January 2025
 */
@Reloadable(
    prefix = "chat-gpt",
    path = "\${application.config.dir}")
class ChatGptConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    private val apiKey: String? = null
    private val preContext: PromptConfig? = null
    private val postContext: PromptConfig? = null
    private val daily: PromptConfig? = null

    fun getApiKey() = apiKey
    fun getPreContext() = preContext
    fun getPostContext() = postContext
    fun getDaily() = daily
    // Note: this getter is required or proxying this field fails

    companion object {
        @Serial private val serialVersionUID = 3578565273432319L

        private val log: Logger = LoggerFactory.getLogger(ChatGptConfig::class.java)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class PromptConfig : Serializable {

        private val promptDeveloper: String? = null
        private val promptUser: String? = null

        fun getPromptDeveloper() = promptDeveloper
        fun getPromptUser() = promptUser

        companion object {
            @Serial private val serialVersionUID = 6383267545394827L
        }
    }

}
