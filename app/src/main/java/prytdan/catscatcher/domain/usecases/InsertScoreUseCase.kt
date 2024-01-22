package prytdan.catscatcher.domain.usecases

import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.repository.ScoreRepository

class InsertScoreUseCase(private val repository: ScoreRepository) {

    suspend fun execute(score: Score) {
        repository.insertScore(score)
    }
}