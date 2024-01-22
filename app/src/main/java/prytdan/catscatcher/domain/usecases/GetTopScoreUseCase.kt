package prytdan.catscatcher.domain.usecases

import androidx.lifecycle.LiveData
import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.repository.ScoreRepository

class GetTopScoreUseCase(private val repository: ScoreRepository) {

    fun execute(): LiveData<Score> {
        return repository.getTopScore()
    }
}