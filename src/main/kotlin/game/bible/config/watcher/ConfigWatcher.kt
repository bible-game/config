package game.bible.config.watcher

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import game.bible.config.bean.ConfigBean
import game.bible.config.bean.ConfigBeanRepository
import game.bible.config.bean.Reloadable
import game.bible.config.bean.Initialisable
import org.apache.commons.lang3.SerializationUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.DependsOn
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.nio.file.StandardWatchEventKinds
import java.util.concurrent.Executors
import jakarta.annotation.PostConstruct

/**
 * Uses an event-based file watcher to monitors beans that are marked as [@Reloadable]
 * Beans are dynamically updated when changes occur to the underlying configuration file
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Service
@DependsOn("fileWatcherService")
class ConfigWatcher(
    private val beanRepository: ConfigBeanRepository,
    private val fileWatcherService: FileWatcherService,
    private val eventPublisher: ApplicationEventPublisher,
    private val environment: Environment
) {

    private val log: Logger = LoggerFactory.getLogger(ConfigWatcher::class.java)

    private val yamlParser: ObjectMapper = ObjectMapper(YAMLFactory())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
        .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)

    private val propertiesParser = JavaPropsMapper()
    private val jsonParser: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Value("\${application.config.dir}") val defaultConfigLocation: String? = null
    private var defaultConfigDir: File? = null

    @PostConstruct
    fun startMonitoring() {
        defaultConfigDir = File(defaultConfigLocation!!)
        Executors.newCachedThreadPool().execute {
            fileWatcherService.addToMonitor(defaultConfigDir!!)
            fileWatcherService.launchMonitoring()
        }
    }

    /**
     * Returns the relevant implement of [ObjectMapper] based on the filename extensions
     * Currently supports:
     * - YAML
     * - Properties
     * - JSON
     *
     * @param filename name of the file needed for parsing
     * @return ObjectMapper implementation
     */
    private fun getMapper(filename: String?): ObjectMapper {
        if (StringUtils.endsWithAny(filename, ".yml", ".yaml")) {
            return yamlParser
        }

        if (StringUtils.endsWith(filename, ".properties")) {
            return propertiesParser
        }

        return jsonParser
    }

    /**
     * Add a new config bean (if not already matched)
     *
     * @param annotation contains details of the configuration file
     */
    fun watchFile(annotation: Reloadable, type: Class<out Serializable?>?): Any? {
        val configFile: File
        if (annotation.path.isBlank()) {
            configFile = File(defaultConfigDir, annotation.filename)
        } else {
            val location = environment.resolvePlaceholders(annotation.path)
            configFile = File(location, annotation.filename)

            fileWatcherService.addToMonitor(File(location))
        }

        if (!configFile.exists()) {
            log.error("Config file [{}] not found on file system!", configFile.absolutePath)
            return null
        }

        if (!beanRepository.contains(type)) {
            try {
                val configObject = readConfigurationToObject(configFile, annotation.filename, annotation.prefix, null, type)
                if (configObject != null) {
                    val configBean: ConfigBean<out Serializable> = ConfigBean(configObject, configObject.javaClass, annotation.filename, annotation.prefix)
                    log.trace("[watchFile] Adding root config [{}] -> [{}]", configBean.filename, configBean.beanType)
                    log.trace("[watchFile] [{}]", configBean.bean)
                    beanRepository.add<Serializable>(configBean)
                    return configObject
                }
            } catch (e: Exception) {
                log.error("Error while reading config for: [" + annotation.filename + "] from configuration directory!", e)
                return null
            }
        }
        return beanRepository.get(type)
    }

    /**
     * Listens for low level FS events and if potentially valid tenant configuration folder and file has been added - raise appropriate event
     *
     * @param event
     */
    @EventListener
    fun handleFileChangedEvent(event: FileChangedEvent) {
        if (StandardWatchEventKinds.OVERFLOW.name() == event.kind.name()) {
            throw RuntimeException("Not implemented! Event: " + StandardWatchEventKinds.OVERFLOW.name())
        }

        log.debug("[handleFileChangedEvent] Incoming file event [{}]", event.kind)
        log.trace("[handleFileChangedEvent] Incoming file event [{}]", event)

        try {
            val toCheck = event.path!!.toFile()
            if (toCheck.isFile && beanRepository.isSupported(toCheck.name)) {
                // Otherwise, check if the file is already supported and reload config for root
                val isTopLevel = defaultConfigDir == toCheck.parentFile
                beanRepository.getConfigBeans().values.stream()
                    .filter { b: ConfigBean<out Serializable?> -> b.filename == toCheck.name }
                    .forEach { bean: ConfigBean<out Serializable?> ->
                        try {
                            if (isTopLevel) {
                                log.trace("[handleFileChangedEvent] Recognised change to top-level config file: [{}], reloading root and tenant beans as [{}]", bean.filename, bean.beanType)
                                val updatedBean = readConfigurationToObject(toCheck, bean.filename, bean.prefix, null, bean.beanType)
                                log.trace("[handleFileChangedEvent] New config: [{}]", updatedBean)
                                bean.bean = updatedBean
                                if (bean.bean is Initialisable) {
                                    // Make sure to initialise the bean if required
                                    (bean.bean as Initialisable?)!!.init()
                                }
                                eventPublisher.publishEvent(ConfigurationChangedEvent(bean.bean, bean.prefix!!, bean.beanType!!))
                            }
                        } catch (e: Exception) {
                            log.error("Error re-reading file: [{}] for [{}]", toCheck.name, bean.beanType)
                            log.error("Exception: ", e)
                        }
                    }
            }
        } catch (e: Exception) {
            log.error("Error processing event!", e)
        }
    }

    @Throws(IOException::class)
    private fun readConfigurationToObject(configFile: File, filename: String?, prefix: String?, defaults: Serializable?, type: Class<out Serializable?>?): Serializable? {
        val mapper = getMapper(filename)
        // We need to get the tree so that we can make sure to read the file from the correct node only
        var root = mapper.readValue(configFile, JsonNode::class.java)
        if (StringUtils.isNotBlank(prefix)) {
            log.trace("[readConfigurationToObject] Getting [{}] node from [{}]", prefix, filename)
            root = root!![prefix]
        }

        val `object`: Serializable
        if (root != null) {
            log.trace("[readConfigurationToObject] Creating configuration of type [{}]", filename)
            if (defaults != null) {
                log.trace("[readConfigurationToObject] Applying default values from [{}]", defaults)
                // Clone the original "default" bean and apply any overrides
                val deepClone = SerializationUtils.clone(defaults)
                `object` = mapper.readerForUpdating(deepClone).readValue(root.traverse())
            } else {
                log.trace("[readConfigurationToObject] No default values")
                `object` = mapper.readValue(root.traverse(), type)!!
            }

            log.trace("[readConfigurationToObject] Created new object of type: [{}] from [{}]", type, filename)
            if (`object` is Initialisable) {
                log.trace("[readConfigurationToObject] Initialise object of type: [{}]", type)
                `object`.init()
            }
            return `object`
        }
        return null
    }
}

