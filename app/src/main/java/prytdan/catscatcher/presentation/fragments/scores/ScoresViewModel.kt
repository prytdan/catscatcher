package prytdan.catscatcher.presentation.fragments.scores

import androidx.lifecycle.LiveData
import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.usecases.GetTopFiveScoresUseCase
import prytdan.catscatcher.presentation.fragments.BaseViewModel

class ScoresViewModel(getTopFiveScoresUseCase: GetTopFiveScoresUseCase) : BaseViewModel() {

    val topFiveScores: LiveData<List<Score>> = getTopFiveScoresUseCase.execute()
}