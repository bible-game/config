package game.bible.common.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.work2net.common.config.bean.Initialisable
import com.work2net.common.config.bean.ReloadableConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serial
import java.io.Serializable

/**
 * Bible Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@ReloadableConfig(prefix = "bible", path = "\${application.config.dir}")
open class BibleConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    companion object {
        @Serial private val serialVersionUID = 874623178652743219L

        private val log: Logger = LoggerFactory.getLogger(BibleConfig::class.java)
    }

    private val old: Old? = null
    open fun getOld() = old // Note: this getter is required or proxying this field fails

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Old : Serializable {

        private val genesis: List<Int>? = null
        fun getGenesis() = genesis

        companion object {
            @Serial
            private val serialVersionUID = 296041764552816883L
        }
    }

}
