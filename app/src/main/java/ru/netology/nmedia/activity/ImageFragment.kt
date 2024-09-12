package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.ARG_POST_ID
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.util.loadImage

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
        val post = viewModel.data.value?.posts?.first {
            it.id == currentPostId
        }

        loadImage(post, binding.photo)

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}
