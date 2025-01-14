package game.bible.config.scheduled

import game.bible.config.model.ScheduledTask
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

/**
 * Replaces values from annotation with those from configuration
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class ScheduledProcess(
    private val taskConfig: ScheduledTask.TaskDefinition,
    private val scheduled: Scheduled
) {

    override fun equals(other: Any?): Boolean {
        return scheduled == other
    }

    override fun hashCode(): Int {
        return scheduled.hashCode()
    }

    fun cron(): String? {
        return taskConfig.cron
    }

    fun zone(): String {
        return scheduled.zone
    }

    fun fixedDelay(): Long {
        return taskConfig.fixedDelay!!
    }

    fun fixedDelayString(): String {
        return taskConfig.fixedDelay.toString()
    }

    fun fixedRate(): Long {
        return taskConfig.fixedRate!!
    }

    fun fixedRateString(): String {
        return taskConfig.fixedRate.toString()
    }

    fun initialDelay(): Long {
        return taskConfig.initialDelay!!
    }

    fun initialDelayString(): String {
        return taskConfig.initialDelay.toString()
    }

    fun timeUnit(): TimeUnit {
        return taskConfig.timeUnit
    }

    fun scheduler(): String {
        return ""
    }

    override fun toString(): String {
        return taskConfig.toString()
    }

    fun annotationType(): Class<out Annotation> {
        return (scheduled as java.lang.annotation.Annotation).annotationType()
    }

}
