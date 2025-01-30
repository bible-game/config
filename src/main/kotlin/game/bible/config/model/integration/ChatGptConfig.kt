package game.bible.config.model.integration

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial

/**
 * ChatGPT Configuration
 *
 * @author J. R. Smith
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
    private val promptDeveloper: String? = null
    private val promptUser: String? = null

    fun getApiKey() = apiKey
    fun getPromptDeveloper() = promptDeveloper
    fun getPromptUser() = promptUser
    // Note: this getter is required or proxying this field fails

    companion object {
        @Serial private val serialVersionUID = 3578565273432319L

        private val log: Logger = LoggerFactory.getLogger(ChatGptConfig::class.java)
    }

}
