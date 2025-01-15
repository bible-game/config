package game.bible.config.bean

import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.lang.reflect.Method

class ReloadableConfigHandler(
    private val type: Class<out Any>,
    private val beanRepository: ConfigBeanRepository
) {

    @RuntimeType
    @Throws(Throwable::class)
    fun intercept(
        @This target: Any,
        @Origin method: Method,
        @AllArguments args: Array<Any?>
    ): Any? {
        log.trace("[intercept] Intercepted: [{}].[{}]", target.javaClass, method.name)

        @Suppress("UNCHECKED_CAST")
        val configBean = beanRepository.get(type as Class<out Serializable>)
        log.trace("[intercept] Calling method [{}] on root bean [{}] (if null, no root bean found)", method.name, configBean?.javaClass)
        return if (configBean != null) method.invoke(configBean, *args) else null
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ReloadableConfigHandler::class.java)
    }
}
