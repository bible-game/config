package game.bible.config.model.integration

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial

/**
 * Bible Api Configuration
 *
 * @author J. R. Smith
 * @since 14th January 2025
 */
@Reloadable(
    prefix = "bible-api",
    path = "\${application.config.dir}")
class BibleApiConfiguration : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    private val baseUrl: String? = null
    private val api: Map<String, String> = java.util.HashMap()

    fun getBaseUrl() = baseUrl
    fun getApi() = api
    // Note: this getter is required or proxying this field fails

    companion object {
        @Serial private val serialVersionUID = 874621378652743219L

        private val log: Logger = LoggerFactory.getLogger(BibleApiConfiguration::class.java)
    }

}
