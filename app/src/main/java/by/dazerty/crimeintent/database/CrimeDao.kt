package by.dazerty.crimeintent.database

import androidx.room.Dao
import androidx.room.Query
import by.dazerty.crimeintent.Crime
import java.util.*

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): List<Crime>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): Crime?
}