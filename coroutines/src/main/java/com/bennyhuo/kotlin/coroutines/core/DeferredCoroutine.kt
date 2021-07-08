package com.bennyhuo.kotlin.coroutines.core

import com.bennyhuo.kotlin.coroutines.Deferred
import com.bennyhuo.kotlin.coroutines.Job
import com.bennyhuo.kotlin.coroutines.cancel.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class DeferredCoroutine<T>(context: CoroutineContext) : AbstractCoroutine<T>(context), Deferred<T> {

    override suspend fun await(): T {
        val currentState = state.get()
        return when (currentState) {
            is CoroutineState.InComplete,
            is CoroutineState.CompleteWaitForChildren<*>,
            is CoroutineState.Cancelling,
            -> awaitSuspend()
            is CoroutineState.Complete<*> -> {
                coroutineContext[Job]?.isActive?.takeIf { !it }?.let {
                    throw CancellationException("Coroutine is cancelled.")
                }
                currentState.exception?.let { throw it } ?: (currentState.value as T)
            }
        }
    }

    private suspend fun awaitSuspend() = suspendCancellableCoroutine<T> { continuation ->
        val disposable = doOnCompleted { result ->
            continuation.resumeWith(result)
        }
        continuation.invokeOnCancellation { disposable.dispose() }
    }
}