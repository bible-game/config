package game.bible.config.bean

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Component

/**
 * Field-level annotation indicating that config should be reloadable
 * It works by applying a proxy object in place of the bean
 * This proxy object executes the required methods against the latest version of the underlying bean class.
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented @Component @JsonIgnoreProperties(ignoreUnknown = true)
annotation class Reloadable(
    val prefix: String = "",
    val path: String = "\${application.config.dir}",
    val filename: String = "config.yml"
)
