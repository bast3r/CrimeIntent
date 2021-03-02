package by.dazerty.crimeintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import by.dazerty.crimeintent.Crime
import java.util.*

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>
}