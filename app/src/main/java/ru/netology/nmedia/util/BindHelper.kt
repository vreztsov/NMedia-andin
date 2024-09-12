package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

fun loadImage(post: Post?, imageView: ImageView) {
    imageView.setImageDrawable(null)
    if (post != null) {
        val imgUrl =
            "$BASE_URL/media/${post.attachment?.url ?: ""}"
        Glide.with(imageView)
            .load(imgUrl)
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(imageView)
    }
}