package game.bible.config.scheduled

import org.springframework.scheduling.annotation.Scheduled
import java.lang.reflect.Method

/**
 * Represents a scheduled job
 * Contains a reference to the bean and method that is applicable to the given job
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class ScheduledJobWrapper(
    val annotation: Scheduled,
    val bean: Any,
    val method: Method
)
