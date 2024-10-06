package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.ARG_POST_ID
import ru.netology.nmedia.util.goToLogin
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (!viewModel.isAuthorized) {
                    goToLogin(this@FeedFragment)
                    return
                }
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onImageClick(post: Post) {
                if (post.attachment?.type == AttachmentType.IMAGE) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_imageFragment,
                        Bundle().apply {
                            putLong(ARG_POST_ID, post.id)
                        }
                    )
                }

            }
        })
        binding.list.adapter = adapter
        binding.errorGroup.visibility = View.GONE
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
//                binding.emptyText.isVisible = state.empty
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing =
                    it.refresh is LoadState.Loading
                            || it.append is LoadState.Loading
                            || it.prepend is LoadState.Loading
            }
        }
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.retry()
                        viewModel.loadPosts()
                    }
                    .show()
            }
        }
//        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
//            binding.newer.visibility = View.VISIBLE
//            println(state)
//        }

        binding.newer.setOnClickListener {
            viewModel.showNewPosts()
            viewModel.loadPosts()
            binding.newer.visibility = View.GONE
            binding.list.smoothScrollToPosition(0)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
//            viewModel.refreshPosts()
//            binding.swiperefresh.isRefreshing = false
        }

        binding.fab.setOnClickListener {
            if (viewModel.isAuthorized) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                goToLogin(this)
            }
        }

        return binding.root
    }
}
