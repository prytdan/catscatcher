package prytdan.catscatcher.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import prytdan.catscatcher.domain.models.Score

@Dao
interface ScoreDao {

    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 5")
    fun getTopFiveScores(): LiveData<List<Score>>

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 1")
    fun getTopScore(): LiveData<Score>
}