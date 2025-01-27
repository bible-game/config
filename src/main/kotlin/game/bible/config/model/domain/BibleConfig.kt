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

    private val books: List<BibleBookConfig>? = null

    fun getBooks() = books
    // Note: getter required to proxy fields

    @JsonIgnoreProperties(ignoreUnknown = true)
    class BibleBookConfig : Serializable {

        private val book: String? = null
        private val chapters: List<BiblePassageConfig>? = null

        fun getBook() = book
        fun getChapters() = chapters

        companion object {
            @Serial private val serialVersionUID = 987654321098765432L
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class BiblePassageConfig : Serializable {

            private val chapter: Int? = null
            private val title: String? = null
            private val verseStart: Int? = null
            private val verseEnd: Int? = null

            fun getChapter() = chapter
            fun getTitle() = title
            fun getVerseStart() = verseStart
            fun getVerseEnd() = verseEnd

            companion object {
                @Serial private val serialVersionUID = 10645876543987432L
            }
        }
    }
}


