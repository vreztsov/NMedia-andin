package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE isVisible == 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>
    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

//    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
//    suspend fun updateContentById(id: Long, content: String)

//    suspend fun save(post: PostEntity) =
//        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
    @Query("UPDATE PostEntity SET isVisible = 1 WHERE isVisible == 0")
    fun showNewPosts()

    @Query("SELECT * FROM PostEntity WHERE isVisible == 1 ORDER BY id DESC LIMIT :count")
    fun getLatest(count: Int): List<PostEntity>

    @Query("SELECT * FROM (SELECT * FROM PostEntity WHERE (isVisible == 1 AND id < :id)) ORDER BY id DESC LIMIT :count")
    fun getBefore(id: Long, count: Int): List<PostEntity>

    @Query("SELECT * FROM PostEntity WHERE (isVisible == 1 AND id > :id) ORDER BY id")
    fun getNewer(id: Long): List<PostEntity>

    @Query("DELETE FROM PostEntity")
    suspend fun clear()
}
