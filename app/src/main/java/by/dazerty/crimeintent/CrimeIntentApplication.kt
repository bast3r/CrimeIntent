package by.dazerty.crimeintent

import android.app.Application
import by.dazerty.crimeintent.database.CrimeRepository

class CrimeIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)

    }
}