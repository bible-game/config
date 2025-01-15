package game.bible.config.bean

import game.bible.config.model.scheduled.ScheduledTaskConfig
import game.bible.config.scheduled.ScheduledJobWrapper
import game.bible.config.watcher.ConfigurationChangedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.core.PriorityOrdered
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import java.util.TimeZone
import kotlin.collections.HashMap

/**
 * Handles any [ScheduledTaskConfig] annotations
 *
 * @author J. R. Smith
 * @since 13th January 2025
 */
@Component
class ScheduledTaskAnnotationProcessor(
    private var applicationContext: ApplicationContext,
    private var scheduledConfig: ScheduledTaskConfig?,
    private var taskScheduler: TaskScheduler?,
) : ScheduledAnnotationBeanPostProcessor(), PriorityOrdered {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ScheduledAnnotationBeanPostProcessor::class.java)
    }

    private var jobsTriggered = false
    private val jobsMap: MutableMap<String, ScheduledFuture<*>?> = HashMap()
    private val jobs: MutableMap<String, ScheduledJobWrapper> = HashMap()

    override fun processScheduled(scheduled: Scheduled, method: Method, bean: Any) {
        val taskAnnotation = method.getAnnotation(game.bible.config.scheduled.Scheduled::class.java)
        if (taskAnnotation != null) {
            jobs[taskAnnotation.value] = ScheduledJobWrapper(scheduled, bean, method)
        }
    }

    /**
     * Save all the scheduled jobs for later to make sure that the config class has been correctly initialised first
     */
    @EventListener(ApplicationReadyEvent::class)
    fun configureScheduledTasks() {
        if (scheduledConfig == null) {
            scheduledConfig = applicationContext.getBean(ScheduledTaskConfig::class.java)
        }
        if (taskScheduler == null) {
            taskScheduler = applicationContext.getBean<TaskScheduler>(TaskScheduler::class.java)
        }
        log.debug("[ScheduledTaskAnnotationProcessor] ENTER: configureScheduledTasks")
        val scheduledTasks: Map<String, ScheduledTaskConfig.TaskDefinition>? = (scheduledConfig as ScheduledTaskConfig).tasks
        if (!scheduledTasks.isNullOrEmpty()) {
            jobs.forEach { jobId: String, job: ScheduledJobWrapper ->
                if (jobsMap.containsKey(jobId)) {
                    removeTask(jobId)
                }
                if (scheduledTasks.get(jobId) == null) {
                    super.processScheduled(job.annotation, job.method, job.bean)
                } else {

                    val taskConfig: ScheduledTaskConfig.TaskDefinition = scheduledTasks[jobId] ?: ScheduledTaskConfig.TaskDefinition(
                        enabled = false
                    )
                    if (taskConfig.enabled) {
                        scheduleTask(taskConfig, jobId, job)
                    } else {
                        log.trace("Not scheduling $jobId because it has been removed or is disabled!")
                    }
                }
            }
        }

        jobsTriggered = true
    }

    fun scheduleTask(taskDef: ScheduledTaskConfig.TaskDefinition, jobId: String, job: ScheduledJobWrapper) {
        val task = Runnable {
            try {
                job.method.invoke(job.bean)
            } catch (e: Exception) {
                log.error("Failed to trigger task for: $jobId", e)
            }
        }
        val scheduledTask: ScheduledFuture<*>?
        if (taskDef.cron != null) {
            scheduledTask = taskScheduler!!.schedule(task, CronTrigger(taskDef.cron, TimeZone.getTimeZone(TimeZone.getDefault().id)))
            log.debug("Scheduling task with job id: [{}] and cron expression: [{}]", jobId, taskDef.cron)
        } else if (taskDef.fixedRate != null) {
            scheduledTask = taskScheduler!!.scheduleAtFixedRate(task, Instant.now(), Duration.ofMillis(taskDef.fixedRate))
            log.debug("Scheduling task with job id: [{}] and fixed rate: [{}]", jobId, taskDef.fixedRate)
        } else if (taskDef.fixedDelay != null) {
            scheduledTask = taskScheduler!!.scheduleWithFixedDelay(task, Instant.now(), Duration.ofMillis(taskDef.fixedDelay))
            log.debug("Scheduling task with job id: " + jobId + " and fixed delay: " + taskDef.fixedDelay)
        } else {
            return
        }

        jobsMap[jobId] = scheduledTask
    }

    fun removeTask(jobId: String) {
        val scheduledTask: ScheduledFuture<*>? = jobsMap[jobId]
        if (scheduledTask != null) {
            scheduledTask.cancel(true)
            jobsMap[jobId] = null
        }
    }

    @EventListener(condition = "#event.hasConfig('scheduled')")
    fun updateScheduledTasks(event: ConfigurationChangedEvent?) {
        configureScheduledTasks()
    }
}
