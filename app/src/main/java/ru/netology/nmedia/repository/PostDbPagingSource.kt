package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.entity.toDto
import java.io.IOException

class PostDbPagingSource(
    private val postDao: PostDao
) : PagingSource<Long, FeedItem>() {
    override fun getRefreshKey(state: PagingState<Long, FeedItem>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, FeedItem> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    postDao.getLatest(params.loadSize)
                }

                is LoadParams.Append -> {
                    postDao.getBefore(id = params.key, count = params.loadSize)
                }

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            val data = result.toDto()
            return LoadResult.Page(data, prevKey = params.key, data.lastOrNull()?.id)
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}