package ru.netology.nmedia.repository

import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.PostRepository.Companion.pageSize
import javax.inject.Inject

class PostRepositoryDbImpl @Inject constructor(
    private val dao: PostDao,
) : AbstractPostRepository() {

    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            PostDbPagingSource(dao)
        }
    ).flow

    override suspend fun getInitialPostPage() {
        dao.getLatest(pageSize)
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val result = dao.getNewer(id)
            emit(result.size)
        }
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
    }

    override suspend fun save(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }

    override suspend fun upload(upload: MediaUpload): Media {
        throw UnsupportedOperationException()
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val uri = upload.file.toUri()
            val postWithAttachment =
                post.copy(attachment = Attachment(uri.toString(), AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun showNewPosts() {
        dao.showNewPosts()
    }
}