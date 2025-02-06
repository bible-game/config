package game.bible.config.model.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
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
@Reloadable(
    path = "\${application.config.dir}",
    filename = "bible.json"
)
class BibleConfig : Initialisable {

    init {
        log.info("Created [{}]", this.javaClass.name)
    }

    companion object {
        @Serial private val serialVersionUID = 874623178652743219L

        private val log: Logger = LoggerFactory.getLogger(BibleConfig::class.java)
    }

    private val testaments: List<BibleTestamentConfig>? = null

    fun getTestaments() = testaments
    // Note: getter required to proxy fields

    @JsonIgnoreProperties(ignoreUnknown = true)
    class BibleTestamentConfig : Serializable {

        private val name: String? = null
        private val divisions: List<BibleDivisionConfig>? = null

        fun getName() = name
        fun getDivisions() = divisions

        companion object {
            @Serial private val serialVersionUID = 10645876543987432L
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class BibleDivisionConfig : Serializable {

            private val name: String? = null
            private val books: List<BibleBookConfig>? = null

            fun getName() = name
            fun getBooks() = books

            companion object {
                @Serial private val serialVersionUID = 7066743554336982L
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            class BibleBookConfig : Serializable {

                private val key: String? = null
                private val name: String? = null
                private val chapters: Int? = null
                private val verses: List<Int>? = null

                fun getKey() = key
                fun getName() = name
                fun getChapters() = chapters
                fun getVerses() = verses

                companion object {
                    @Serial private val serialVersionUID = 4706367545394827L
                }
            }
        }
    }

}


