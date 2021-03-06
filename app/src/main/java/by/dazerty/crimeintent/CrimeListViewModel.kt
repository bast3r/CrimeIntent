package by.dazerty.crimeintent

import androidx.lifecycle.ViewModel
import by.dazerty.crimeintent.database.CrimeRepository

//список преступлений
class CrimeListViewModel : ViewModel() {

//    val crimes = mutableListOf<Crime>()

    init {
//        for (i in 0 until 100) {
//            val crime = Crime()
//            crime.title = "Crime #$i"
//            crime.isSolved = i % 2 == 0
//            crime.requiresPolice = i % 3 == 0
//            crimes += crime
//        }
    }

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}