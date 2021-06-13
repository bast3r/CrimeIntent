package by.dazerty.crimeintent.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import by.dazerty.crimeintent.Crime
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context){
    //connection to db
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    )
        .addMigrations(migration_1_2)//add new field about suspect
        .build()
    //dao
    private val crimeDao = database.crimeDao()
    //thread for manipulating data
    private val executor = Executors.newSingleThreadExecutor() //отдельный тред для работы с обновлением БД
    //livedata for background changes
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute{
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        //use extra thread for working db
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null)
                INSTANCE = CrimeRepository(context)
        }

        fun get() : CrimeRepository {
            return INSTANCE ?:
                    throw IllegalStateException("Did not initialize")

        }
    }
}