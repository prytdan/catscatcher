package prytdan.catscatcher.presentation.fragments.menu

import androidx.lifecycle.LiveData
import prytdan.catscatcher.domain.models.Score
import prytdan.catscatcher.domain.usecases.GetTopScoreUseCase
import prytdan.catscatcher.presentation.fragments.BaseViewModel

class MenuViewModel(getTopScoreUseCase: GetTopScoreUseCase) : BaseViewModel(){

    val topScores: LiveData<Score> = getTopScoreUseCase.execute()
}