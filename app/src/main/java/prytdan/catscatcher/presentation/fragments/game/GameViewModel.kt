package prytdan.catscatcher.presentation.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import prytdan.catscatcher.R
import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.usecases.InsertScoreUseCase
import prytdan.catscatcher.presentation.fragments.BaseViewModel
import kotlin.math.abs
import kotlin.random.Random

class GameViewModel(private val insertScoreUseCase: InsertScoreUseCase) : BaseViewModel() {

    private companion object {
        const val INITIAL_TIME_OF_FALL = 5000L // initial time of cat's falling
        const val SPEED_INCREASE_STEP =
            20 // value, by which INITIAL_TIME_OF_FALL decreased after successful catch
        const val MAXIMUM_TIME_OF_FALL = 2000L
    }

    private var fallingSpeed = INITIAL_TIME_OF_FALL

    private var scores = 0

    private val scoresMutableLiveData = MutableLiveData<Int>()

    val scoresLiveData: LiveData<Int> = scoresMutableLiveData

    private val catsImages by lazy {
        listOf(
            R.mipmap.neko1,
            R.mipmap.neko2,
            R.mipmap.neko3,
            R.mipmap.neko4,
            R.mipmap.neko5,
            R.mipmap.neko6
        )
    }

    fun getRandomCatDrawable(): Int {
        return catsImages.random()
    }

    fun getRandomX(screenWidth: Int, imageWidth: Int): Float {
        return Random.nextInt(0, abs(screenWidth - imageWidth)).toFloat()
    }

    fun getCurrentFallingSpeed(): Long {
        return fallingSpeed
    }

    fun increaseFallingSpeed() {
        if (fallingSpeed > MAXIMUM_TIME_OF_FALL) fallingSpeed -= SPEED_INCREASE_STEP
    }

    fun incrementScores() {
        scores++
        scoresMutableLiveData.postValue(scores)
    }

    fun getScores(): Int {
        return scores
    }

    fun resetGameValues() {
        fallingSpeed = INITIAL_TIME_OF_FALL
        scores = 0
        scoresMutableLiveData.postValue(0)
    }

    fun saveGameResultScores() {
        viewModelScope.launch(Dispatchers.IO) { insertScoreUseCase.execute(Score(score = scores)) }
    }
}