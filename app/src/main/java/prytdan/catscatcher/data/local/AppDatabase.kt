package prytdan.catscatcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import prytdan.catscatcher.domain.models.Score

@Database(entities = [Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scoreDao(): ScoreDao
}