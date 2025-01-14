package game.bible.config.watcher

import org.springframework.context.ApplicationEvent
import java.io.Serial
import java.nio.file.Path
import java.nio.file.WatchEvent

/**
 * File Changed Event
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class FileChangedEvent(
    source: Any?,
    path: Path?,
    kind: WatchEvent.Kind<Path>
) : ApplicationEvent(source!!) {

    @Transient val kind: WatchEvent.Kind<Path>
    @Transient var path: Path? = null

    init {
        // In order to normalize directory separators
        if (path != null) {
            this.path = path.toFile().toPath()
        }
        this.kind = kind
    }

    override fun equals(other: Any?): Boolean {
        return other is FileChangedEvent && kind == other.kind && super.equals(other)
    }

    override fun hashCode(): Int {
        var result = kind.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }

    companion object {
        @Serial
        private val serialVersionUID = 4866828187748553025L
    }
}
