package by.dazerty.crimeintent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

//фрагмент для списка преступлений
class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView : RecyclerView
    private var adapter : CrimeAdapter? = null

    //лист модель
    private val crimeListViewModel : CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    //холдер хранит представление для ячейки ресайклера
    private open inner class CrimeHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime : Crime
        //получили контролы
        val titleTextView : TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView : TextView = itemView.findViewById(R.id.crime_date)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()
        }

        //кликабл интерфейс для всей панели
        override fun onClick(view : View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }
    }
    //холдер для преступлений, в которых необходим вызов полиции
    private inner class CrimeWithPoliceHolder(view : View) : CrimeHolder(view) {
        val callPoliceButton : Button = itemView.findViewById(R.id.call_police_button)

        init {
            callPoliceButton.setOnClickListener { view : View ->
                Toast.makeText(context, "We are going to call the police!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //переходник для генерации нужного холдера для ресайклера
    private inner class CrimeAdapter(var crimes : List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = when (viewType) {
                0 -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                1 -> layoutInflater.inflate(R.layout.list_item_crime_with_police, parent, false)
                else -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            }
            if (viewType == 0) {
                return CrimeHolder(view)
            } else {
                return CrimeWithPoliceHolder(view)
            }
        }
        //кол во элементов в списке
        override fun getItemCount() = crimes.size
        //тип элемента
        override fun getItemViewType(position: Int): Int {
            if (crimes[position].requiresPolice) {
                return 1;
            }
            return 0
        }
        //навешние данных из модели
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
//            holder.apply {
//                titleTextView.text = crime.title
//                dateTextView.text = crime.date.toString()
//            }
            holder.bind(crime)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Total crimes : ${crimeListViewModel.crimes.size}")
    }
    //создание контейнера ресайклер
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }
    //привязываение адаптера и ресайклера
    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

}