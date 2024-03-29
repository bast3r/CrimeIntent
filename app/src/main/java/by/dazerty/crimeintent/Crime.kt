package by.dazerty.crimeintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//entity for saving data about crime
@Entity
data class Crime (@PrimaryKey val id : UUID = UUID.randomUUID(),
                  var title : String = "",
                  var date : Date = Date(),
                  var isSolved : Boolean = false,
                  var requiresPolice : Boolean = false,
                  var suspect: String = ""
)

