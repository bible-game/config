package game.bible.config.bean

import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import java.io.Serializable
import game.bible.config.watcher.ConfigWatcher
import java.util.*

/**
 * Captures any tenant aware beans and injects a proxy to
 * handle requests and ensures that the correct configuration is used
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class ConfigBeanProcessor(
    private val configWatcher: ConfigWatcher,
    private val beanRepository: ConfigBeanRepository
) : BeanPostProcessor {

    private val requiredAnnotation: List<Class<out Annotation?>> = listOf<Class<out Annotation?>>(
        Reloadable::class.java)

    /**
     * Any classes annotated with the following are added to [ConfigWatcher] to be monitored for changes and applied as
     * tenant configuration where appropriate:
     * - [ReloadableConfiguration]
     *
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return the original bean
     */
    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(springBean: Any, beanName: String): Any? {
        var bean: Any? = springBean
        try {
            val annotation = bean!!.javaClass.getAnnotation(Reloadable::class.java)
            if (bean is Serializable && annotation != null) {
                // Add to watcher (pass bean config)

                log.info("Found config for: [{}]/[{}] -> [{}]", annotation.path, annotation.filename, annotation.prefix)
                bean = configWatcher.watchFile(annotation, bean.javaClass as Class<out Serializable?>)
            }
        } catch (e: Exception) {
            log.error("Error checking if class is a reloadable configuration bean!", e)
        }
        if (bean is Initialisable) {
            // Initialise the root bean, Spring might not have done it
            bean.init()
        }
        return bean
    }

    /**
     * Provides an Advice implementation to override the returned implementation based on the tenant ID provided in the current request cycle
     *
     * @param bean the new bean instance
     * @param name the name of the bean
     * @return a proxy of the given bean
     */
    override fun postProcessAfterInitialization(bean: Any, name: String): Any {
        val type: Class<*> = bean.javaClass
        if (requiredAnnotation.stream().allMatch { a: Class<out Annotation?>? -> bean.javaClass.isAnnotationPresent(a) }) {
            return ByteBuddy()
                .subclass(type)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(
                    ReloadableConfigHandler(bean.javaClass as Class<out Serializable?>, beanRepository)
                ))
                .make()
                .load(type.classLoader)
                .getLoaded()
                .getConstructor().newInstance()
        }

        // Only proxy beans that have the required annotations applied should be proxied
        return bean
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ConfigBeanProcessor::class.java)
    }
}
