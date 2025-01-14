package game.bible.config.bean

import java.io.Serializable

/**
 * Config file bean that is watched for changes
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
class ConfigBean<T : Serializable?>(
    var bean: Serializable? = null,
    var beanType: Class<T>? = null,
    var filename: String? = null,
    var prefix: String? = null
)
