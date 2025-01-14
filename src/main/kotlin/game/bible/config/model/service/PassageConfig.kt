package game.bible.config.model.service

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial

/**
 * Passage Service Configuration
 *
 * @author J. R. Smith
 * @since 14th January 2025
 */
@Reloadable(
    prefix = "passage-service",
    path = "\${application.config.dir}")
class PassageConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    companion object {
        @Serial private val serialVersionUID = 642314752734543219L

        private val log: Logger = LoggerFactory.getLogger(PassageConfig::class.java)
    }

    private val minVerses: Int? = null
    fun getMinVerses() = minVerses
    // Note: this getter is required or proxying this field fails

    private val maxVerses: Int? = null
    fun getMaxVerses() = maxVerses
    // Note: this getter is required or proxying this field fails

}
