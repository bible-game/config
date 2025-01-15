package game.bible.config.watcher

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import jakarta.annotation.PreDestroy
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.io.path.name

/**
 * File Watcher Service Logic
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Service
class FileWatcherService(
    var watchService: WatchService = FileSystems.getDefault().newWatchService(),
    val eventPublisher: ApplicationEventPublisher
) {

    val keys: MutableMap<WatchKey, Path> = ConcurrentHashMap<WatchKey, Path>()
    val lastModifiedMap: MutableMap<String, Long> = ConcurrentHashMap<String, Long>()

    @Async
    fun addToMonitor(file: File) {
        try {
            val path = file.toPath()
            if (!Files.isDirectory(path)) {
                return // do not monitor individual files
            }

            if (keys.containsValue(path)) {
                log.info("[addToMonitor] File [{}] is already monitored!", path.toAbsolutePath().name)
                return
            }

            log.debug("[addToMonitor] MONITORING_FOLDER: [{}]", path.toFile())
            keys[path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE
            )] = path

            path.toFile().listFiles { f: File ->
                if (f.isDirectory) {
                    log.trace("[addToMonitor] MONITORING_SUBFOLDER : [{}]", path.toFile())
                    addToMonitor(f)
                }
                true
            }
        } catch (e: IOException) {
            log.error("exception for watch service creation:", e)
        }
    }

    @Async
    fun launchMonitoring() {
        try {
            var key: WatchKey
            while ((watchService.take().also { key = it }) != null) {
                val rootPath = keys[key]
                log.debug("[addToMonitor] MONITORING EVENT of [{}]", rootPath)
                for (event in key.pollEvents().stream()
                    .map { p: WatchEvent<*>? -> p as WatchEvent<*> }
                    //.filter( e -> e.count() <= 2) // skip repeated events
                    .toList()) {
                    log.debug("[launchMonitoring] root : [{}]; File affected [{}][{}]: [{}]", rootPath, event.hashCode(), event.count(), (event.context() as Path).toFile())
                    val realPath: Path = rootPath!!.resolve(event.context() as Path)
                    // Following block if to skip potential duplicates FS events sent - on some OS' first event is sent for content modification and second for file's timestamp update
                    val lastModified = realPath.toFile().lastModified()
                    lastModifiedMap.computeIfAbsent(realPath.toFile().path) { f: String? -> lastModified }
                    val previousModified = lastModifiedMap[realPath.toFile().path]
                    log.trace("[launchMonitoring] Current TIMESTAMP for [{}] [{}] [{}]", realPath.toFile().path, previousModified, lastModified)
                    if (lastModified == 0L || previousModified!! <= lastModified) {
                        @Suppress("UNCHECKED_CAST")
                        eventPublisher.publishEvent(raiseFileChangedEvent(realPath, event.kind() as WatchEvent. Kind<Path>))
                    }

                    lastModifiedMap[realPath.toFile().path] = System.currentTimeMillis()

                    // only CREATE and DELETE needs to be checked for further, recursive monitoring setup
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        log.trace("[launchMonitoring] Recursively [{}] [{}] [{}] [{}]", rootPath, realPath.toFile(), (event.context() as Path).toFile().exists(), realPath.toFile().isDirectory)
                        addToMonitor(realPath.toFile())
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        log.trace("[launchMonitoring] Stop monitoring [{}] [{}] [{}] [{}]", rootPath, realPath.toFile(), (event.context() as Path).toFile().exists(), realPath.toFile().isDirectory)
                        lastModifiedMap.remove(realPath.toFile().path)
                    }
                }
                key.reset()
            }
        } catch (e: InterruptedException) {
            log.warn("interrupted exception for monitoring service")
            Thread.currentThread().interrupt()

        } catch (exc: ClosedWatchServiceException) {
            // All good here as thrown on graceful stop
        }
    }

    @PreDestroy
    fun stopMonitoring() {
        log.debug("[stopMonitoring] STOP_MONITORING")

        try {
            watchService.close()
        } catch (e: IOException) {
            log.error("exception while closing the monitoring service")
        }
    }

    fun raiseFileChangedEvent(path: Path, kind: WatchEvent.Kind<Path>): FileChangedEvent {
        log.trace("[raiseFileChangedEvent] Raising fileChangedEvent [{}] [{}]", path.toFile().path, kind.name())
        return FileChangedEvent(this, path, kind)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(FileWatcherService::class.java)
    }

}
