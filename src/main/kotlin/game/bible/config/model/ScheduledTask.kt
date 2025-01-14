package game.bible.config.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import game.bible.config.bean.Reloadable
import java.io.Serial
import java.io.Serializable
import java.util.concurrent.TimeUnit

/**
 * Scheduled Task Configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Reloadable(
    prefix = "game/bible/config/scheduled",
    path = "\${application.config.dir}"
)
class ScheduledTask(val tasks: Map<String, TaskDefinition>? = null) : Serializable {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class TaskDefinition(val enabled: Boolean = false,
                         val cron: String? = null,
                         val fixedDelay: Long? = null,
                         val fixedRate: Long? = null,
                         val initialDelay: Long? = null,
                         val timeUnit: TimeUnit = TimeUnit.SECONDS) : Serializable {

        companion object {
            @Serial private val serialVersionUID = 6485260828417997685L
        }
    }

    companion object {
        @Serial private val serialVersionUID = 1654593950798190490L
    }
}
