package com.bennyhuo.kotlin.coroutines.sample

import com.bennyhuo.kotlin.coroutines.async
import com.bennyhuo.kotlin.coroutines.delay
import com.bennyhuo.kotlin.coroutines.launch
import com.bennyhuo.kotlin.coroutines.scope.GlobalScope
import com.bennyhuo.kotlin.coroutines.utils.log

suspend fun main() {
    GlobalScope.launch {
        log(1)
        val deferred = async {
            log(3)
            getValue()
        }
        log(2)
        val result = deferred.await()
        log(4, result)
        val deferredNullable = async {
            log(6)
            getValueNullable()
        }
        log(5)
        delay(2000)
        val resultNullable = deferredNullable.await()
        log(7, resultNullable)
    }.join()
    log(8)
}

suspend fun getValue(): String {
    delay(1000L)
    return "HelloWorld"
}

suspend fun getValueNullable(): String? {
    delay(1000L)
    return null
}