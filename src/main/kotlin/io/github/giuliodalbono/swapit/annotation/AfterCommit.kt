package io.github.giuliodalbono.swapit.annotation

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.function.Consumer
import kotlin.concurrent.getOrSet

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AfterCommit

@Component
class AfterCommitAdapter : TransactionSynchronization {
    // register a new runnable for after commit execution
    fun execute(runnable: Runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            RUNNABLE.getOrSet { ArrayList() }.add(runnable)
            TransactionSynchronizationManager.registerSynchronization(this)
            return
        }
        // if transaction synchronisation is not active
        runnable.run()
    }

    override fun afterCommit() {
        RUNNABLE.getOrSet { ArrayList() }.forEach(Consumer { obj: Runnable -> obj.run() })
    }

    override fun afterCompletion(status: Int) {
        RUNNABLE.remove()
    }

    companion object {
        private val RUNNABLE = ThreadLocal<MutableList<Runnable>>()
    }
}

@Aspect
@Configuration
class PostCommitAnnotationAspect(
    @Autowired val afterCommitAdapter: AfterCommitAdapter
) {
    @Around("@annotation(io.github.giuliodalbono.swapit.annotation.AfterCommit)")
    fun aroundAdvice(pjp: ProceedingJoinPoint) {
        afterCommitAdapter.execute { pjp.proceed() }
    }
}


