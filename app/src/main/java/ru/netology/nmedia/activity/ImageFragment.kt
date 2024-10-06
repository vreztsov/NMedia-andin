package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.ARG_POST_ID
import ru.netology.nmedia.util.loadImage
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class ImageFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(
            inflater,
            container,
            false
        )

        val currentPostId = arguments?.getLong(ARG_POST_ID) ?: 0
//        val post = viewModel.data.value?.posts?.first {
//            it.id == currentPostId
//        }
        val post = viewModel.emptyPostForCurrentUser() // пока вот так

        loadImage(post, binding.photo)

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}
