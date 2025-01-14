package game.bible.config

import game.bible.config.bean.ConfigBeanRepository
import game.bible.config.bean.ConfigBeanProcessor
import game.bible.config.model.Bible
import game.bible.config.watcher.ConfigWatcher
import game.bible.config.watcher.FileWatcherService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Groups universal configuration to ease setup across services
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Configuration
@Import(
    // Reloadable functionality
    ConfigWatcher::class,
    ConfigBeanRepository::class,
    ConfigBeanProcessor::class,
    // Configuration
    Bible::class
)
class ReloadableConfig {

    @Bean
    @Throws(Exception::class)
    fun fileWatcherService(eventPublisher: ApplicationEventPublisher): FileWatcherService {
        return FileWatcherService(eventPublisher = eventPublisher)
    }
}
