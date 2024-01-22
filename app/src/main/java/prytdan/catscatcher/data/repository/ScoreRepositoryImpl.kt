package prytdan.catscatcher.data.repository

import androidx.lifecycle.LiveData
import prytdan.catscatcher.data.local.ScoreDao
import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.repository.ScoreRepository

class ScoreRepositoryImpl(private val dao: ScoreDao): ScoreRepository {

    override suspend fun insertScore(score: Score) {
        dao.insertScore(score)
    }

    override fun getTopFiveScores(): LiveData<List<Score>> {
        return dao.getTopFiveScores()
    }

    override fun getTopScore(): LiveData<Score> {
        return dao.getTopScore()
    }
}