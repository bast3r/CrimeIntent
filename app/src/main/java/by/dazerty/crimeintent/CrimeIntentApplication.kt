package by.dazerty.crimeintent

import android.app.Application
import by.dazerty.crimeintent.database.CrimeRepository

//main class
class CrimeIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //init db connetion
        CrimeRepository.initialize(this)

    }
}