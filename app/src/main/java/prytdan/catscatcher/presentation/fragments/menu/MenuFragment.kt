package prytdan.catscatcher.presentation.fragments.menu

import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import prytdan.catscatcher.R
import prytdan.catscatcher.databinding.FragmentMenuBinding
import prytdan.catscatcher.presentation.fragments.BaseFragment

class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>() {

    override val viewModel: MenuViewModel by viewModel()

    override fun getViewBinding() = FragmentMenuBinding.inflate(layoutInflater)

    private val gameLink by lazy { getString(R.string.game_link, requireActivity().packageName) }

    private var bestResult = 0

    override fun initViews() {
        setupControls()
    }

    private fun setupControls() {
        overrideBackPressed()
        subscribeToViewModel()
        binding.apply {
            buttonPlay.setOnClickListener { findNavController().navigate(R.id.action_menuFragment_to_gameFragment) }
            buttonScores.setOnClickListener { findNavController().navigate(R.id.action_menuFragment_to_scoresFragment) }
            buttonRate.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(gameLink)
                    )
                )
            }
            buttonShare.setOnClickListener { createShareMessageIntent() }
            buttonExit.setOnClickListener { requireActivity().finishAffinity() }
        }
    }

    private fun subscribeToViewModel() {
        viewModel.topScores.observe(viewLifecycleOwner) { bestScoreResult ->
            if (bestScoreResult != null) bestResult = bestScoreResult.score
        }
    }

    private fun createShareMessageIntent() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            val shareMessage = getString(R.string.share_message, bestResult.toString(), gameLink)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun overrideBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }
}