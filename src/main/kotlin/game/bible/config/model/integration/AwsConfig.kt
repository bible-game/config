package game.bible.config.model.integration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import game.bible.config.bean.Initialisable
import java.io.Serializable
import game.bible.config.bean.Reloadable

/**
 * AWS Configuration
 * @since 3rd July 2025
 */
@Reloadable(
    prefix = "aws",
    path = "\${application.config.dir}"
)
class AwsConfig : Initialisable {

    private val baseUrl: String? = null
    private val username: String? = null
    private val password: String? = null
    private val config: Map<String, String> = HashMap()
    private val connection: ConnectionConfig? = null
    private val s3: S3? = null

    // Note: this getter is required or proxying this field fails
    fun getBaseUrl() = baseUrl
    fun getUsername() = username
    fun getPassword() = password
    fun getConfig() = config
    fun getConnection() = connection
    fun getS3() = s3

    @JsonIgnoreProperties(ignoreUnknown = true)
    class ConnectionConfig : Serializable {

        private val readTimeout: Int? = null
        private val retryCount: Int? = null
        private val retryInterval: Int? = null
        private val timeout: Int? = null

        fun getReadTimeout() = readTimeout
        fun getRetryCount() = retryCount
        fun getRetryInterval() = retryInterval
        fun getTimeout() = timeout
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class S3 : Serializable {

        private val buckets: Map<String, String> = java.util.HashMap()
        private val api: Map<String, String> = java.util.HashMap()

        fun getBuckets() = buckets
        fun getApi() = api
    }
}
