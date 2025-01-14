package game.bible.config.scheduled

import org.springframework.scheduling.annotation.Scheduled

/**
 * Represents a scheduled task that should use a definition from [ScheduledTasksConfiguration]
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Scheduled(initialDelay = Long.MAX_VALUE, fixedDelay = Long.MAX_VALUE)
annotation class Scheduled(val value: String = "scheduled")
