package game.bible.config.model.domain

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial

/**
 * Bible Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Reloadable(
    prefix = "bible",
    path = "\${application.config.dir}",
    filename = "bible.yml"
)
class BibleConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    companion object {
        @Serial private val serialVersionUID = 874623178652743219L

        private val log: Logger = LoggerFactory.getLogger(BibleConfig::class.java)
    }

    private val old: Map<String, Map<Int, Int>>? = null
    fun getOld() = old
    // Note: getter required to proxy this field

    private val new: Map<String, Map<Int, Int>>? = null
    fun getNew() = new
    // Note: getter required to proxy this field

}

