package game.bible.config.model.integration

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial

/**
 * Configuration for Bolls.Life Bible API
 * @since 11th October 2025
 */
@Reloadable(
    prefix = "bolls.api",
    path = "\${application.config.dir}"
)
class BollsLifeApiConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    private var baseUrl: String = "https://bolls.life"

    fun getBaseUrl(): String = baseUrl
    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    // API Endpoints
    fun getBooksUrl(version: String): String = "$baseUrl/get-books/$version/"
    fun getTextUrl(version: String, bookId: String, chapter: Int): String =
        "$baseUrl/get-text/$version/$bookId/$chapter/"

    companion object {
        @Serial private val serialVersionUID = 1L
        private val log: Logger = LoggerFactory.getLogger(BollsLifeApiConfig::class.java)
    }
}
