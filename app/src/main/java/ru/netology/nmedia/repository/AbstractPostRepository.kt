package ru.netology.nmedia.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.TimingSeparator
import ru.netology.nmedia.util.agoToText
import java.time.Instant
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

@RequiresApi(Build.VERSION_CODES.O)
fun PagingData<out FeedItem>.insertSeparators(): PagingData<FeedItem> =
    insertSeparators { previous, next ->
        val currentTime = Instant.now().epochSecond
        val today = "Сегодня"
        val yesterday = "Вчера"
        val twoDaysAgo = "Позавчера"
        val lastWeek = "На прошлой неделе"
        if ((previous is Post && next is Post)) {
            val howOlderPrev = agoToText(
                (currentTime - previous.published.toLong()).toInt(),
                inLastWeekTextDescription = lastWeek,
                inTwoDaysAgoTextDescription = twoDaysAgo,
                inYesterdayTextDescription = yesterday,
                inTodayTextDescription = today,
            )
            val howOlderNext = agoToText(
                (currentTime - next.published.toLong()).toInt(),
                inLastWeekTextDescription = lastWeek,
                inTwoDaysAgoTextDescription = twoDaysAgo,
                inYesterdayTextDescription = yesterday,
                inTodayTextDescription = today,
            )
            when {
                (howOlderPrev != howOlderNext) -> {
                    TimingSeparator(
                        Random.nextLong(),
                        howOlderNext
                    )
                }

                (previous.id.rem(5) == 0L) -> {
                    Ad(
                        Random.nextLong(),
                        "figma.jpg"
                    )
                }

                else -> null
            }
        } else {
            null
        }
    }


fun interface RetryInterface {
    suspend fun retry()
}