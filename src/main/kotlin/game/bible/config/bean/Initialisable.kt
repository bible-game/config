package game.bible.config.bean

import java.io.Serializable
import java.util.function.Consumer

/**
 * Promises the ability to initialise a config class
 * This is picked up by the tenant bean creator and invoked after creation
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
interface Initialisable : Serializable {

    /**
     * Provides the ability to run some extra code on creation.
     * Intended to behave like @PostConstruct in that it is executed after object creation
     */
    fun init() {}

    /**
     * Provides the created initialisable object with a lambda function that will be executed upon creation
     * Intended to behave like @PostConstruct but passes responsibility to calling class to provide logic
     */
    fun init(function: Consumer<Initialisable?>) {
        function.accept(this)
    }
}
