package ru.netology.nmedia.repository

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import kotlin.random.Random

abstract class AbstractPostRepository : PostRepository {
    protected var retryFun: RetryInterface? = null

    protected fun clearRetryFun() {
        retryFun = null
    }

    override suspend fun retry() {
        retryFun?.retry()
    }
}

fun PagingData<out FeedItem>.insertSeparators(): PagingData<FeedItem> =
    insertSeparators { previous, _ ->
        if (previous?.id?.rem(5) == 0L) {
            Ad(Random.nextLong(), "figma.jpg")
        } else {
            null
        }
    }


fun interface RetryInterface {
    suspend fun retry()
}