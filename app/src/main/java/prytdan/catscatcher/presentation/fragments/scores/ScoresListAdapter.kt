package prytdan.catscatcher.presentation.fragments.scores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import prytdan.catscatcher.R
import prytdan.catscatcher.databinding.ScoreItemBinding
import prytdan.catscatcher.domain.models.Score

class ScoresListAdapter : ListAdapter<Score, ScoresListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScoreItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: Score) {
            binding.textPlace.text =
                binding.root.context.getString(R.string.score_place, bindingAdapterPosition + 1)

            binding.textNumberOfScores.text = score.score.toString()
        }
    }
}