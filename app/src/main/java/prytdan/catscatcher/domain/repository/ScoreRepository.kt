package prytdan.catscatcher.domain.repository

import androidx.lifecycle.LiveData
import prytdan.catscatcher.domain.models.Score

interface ScoreRepository {

    suspend fun insertScore(score: Score)

    fun getTopFiveScores(): LiveData<List<Score>>

    fun getTopScore(): LiveData<Score>
}