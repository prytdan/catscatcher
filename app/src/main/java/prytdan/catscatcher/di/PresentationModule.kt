package prytdan.catscatcher.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import prytdan.catscatcher.presentation.fragments.game.GameViewModel
import prytdan.catscatcher.presentation.fragments.menu.MenuViewModel
import prytdan.catscatcher.presentation.fragments.scores.ScoresViewModel

val presentationModule = module {
    viewModel { MenuViewModel(getTopScoreUseCase = get()) }
    viewModel { GameViewModel(insertScoreUseCase = get()) }
    viewModel { ScoresViewModel(getTopFiveScoresUseCase = get()) }
}