package prytdan.catscatcher.presentation.fragments.scores

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import prytdan.catscatcher.R
import prytdan.catscatcher.databinding.FragmentScoresBinding
import prytdan.catscatcher.presentation.fragments.BaseFragment

class ScoresFragment : BaseFragment<FragmentScoresBinding, ScoresViewModel>() {

    override val viewModel: ScoresViewModel by viewModel()

    private val listAdapter: ScoresListAdapter = ScoresListAdapter()

    override fun getViewBinding() = FragmentScoresBinding.inflate(layoutInflater)

    override fun initViews() {
        subscribeToViewModel()
        setupViews()
        setupControls()
    }

    private fun setupControls() {
        binding.buttonToMenu.setOnClickListener {
            findNavController().navigate(R.id.action_scoresFragment_to_menuFragment)
        }
    }

    private fun setupViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun subscribeToViewModel() {
        viewModel.topFiveScores.observe(viewLifecycleOwner) { topFiveScores ->
            listAdapter.submitList(topFiveScores)
        }
    }

}