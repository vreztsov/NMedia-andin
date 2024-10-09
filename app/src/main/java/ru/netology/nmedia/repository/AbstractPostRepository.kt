package ru.netology.nmedia.repository

abstract class AbstractPostRepository: PostRepository {
    protected var retryFun: RetryInterface? = null

    protected fun clearRetryFun() {
        retryFun = null
    }

    override suspend fun retry() {
        retryFun?.retry()
    }
}

fun interface RetryInterface {
    suspend fun retry()
}