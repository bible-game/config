package game.bible.config.bean

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository to store a cache of config beans
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Service
class ConfigBeanRepository {
    // Collection of root bean implementations read from the file system automatically by Spring
    private val configBeans: MutableMap<Class<out Serializable>?, ConfigBean<out Serializable>> = ConcurrentHashMap()

    fun getConfigBeans(): Map<Class<out Serializable>?, ConfigBean<out Serializable>> {
        return configBeans
    }

    fun isSupported(filename: String): Boolean {
        return configBeans.values.stream().map{ c -> c.filename }.anyMatch { anObject: String? -> filename.equals(anObject) }
    }

    fun <T : Serializable?> add(bean: ConfigBean<out Serializable>?) {
        if (bean == null) {
            log.debug("[add] Ignoring null reference")
            return
        }
        log.debug("[add] Adding root bean [{}] -> [{}]", bean.filename, bean.beanType)
        configBeans[bean.beanType] = bean
    }

    fun <T : Serializable?> contains(type: Class<T>?): Boolean {
        return configBeans.containsKey(type)
    }

    fun remove(type: Class<out Serializable>?) {
        log.debug("[remove] Removing config bean of type: [{}]", type)
        configBeans.remove(type)
    }

    fun <T : Serializable?> get(type: Class<T>?): T? {
        val bean: ConfigBean<*>? = configBeans[type]
        if (bean != null) {
            return bean.bean as T?
        }
        return null
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ConfigBeanRepository::class.java)
    }
}
