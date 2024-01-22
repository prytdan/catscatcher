package prytdan.catscatcher.di

import org.koin.dsl.module
import prytdan.catscatcher.domain.usecases.GetTopFiveScoresUseCase
import prytdan.catscatcher.domain.usecases.GetTopScoreUseCase
import prytdan.catscatcher.domain.usecases.InsertScoreUseCase

val domainModule = module {
    factory { GetTopFiveScoresUseCase(repository = get()) }
    factory { GetTopScoreUseCase(repository = get()) }
    factory { InsertScoreUseCase(repository = get()) }
}