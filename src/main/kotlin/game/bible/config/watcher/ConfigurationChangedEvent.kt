package game.bible.config.watcher

import org.springframework.context.ApplicationEvent
import java.io.Serial
import java.io.Serializable

/**
 * Configuration Changed Event
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class ConfigurationChangedEvent(
    source: Any?,
    @field:Transient val prefix: String,
    @field:Transient val configClass: Class<out Serializable?>
) : ApplicationEvent(source!!) {

    fun hasConfig(prefix: String): Boolean {
        return prefix == this.prefix
    }

    override fun equals(other: Any?): Boolean {
        return (other is ConfigurationChangedEvent && prefix == other.prefix)
                && configClass == other.configClass && super.equals(other)
    }

    override fun hashCode(): Int {
        var result = prefix.hashCode()
        result = 31 * result + configClass.hashCode()
        return result
    }

    companion object {
        @Serial
        private val serialVersionUID = 4866828187748553025L
    }
}
