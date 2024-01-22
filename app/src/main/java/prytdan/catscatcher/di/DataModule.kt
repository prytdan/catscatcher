package prytdan.catscatcher.di

import androidx.room.Room
import org.koin.dsl.module
import prytdan.catscatcher.data.local.AppDatabase
import prytdan.catscatcher.data.repository.ScoreRepositoryImpl
import prytdan.catscatcher.domain.repository.ScoreRepository

val dataModule = module {

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().scoreDao() }

    single<ScoreRepository> { ScoreRepositoryImpl(dao = get()) }
}