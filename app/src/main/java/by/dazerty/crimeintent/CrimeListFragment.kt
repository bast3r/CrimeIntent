package by.dazerty.crimeintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"

//фрагмент для списка преступлений
class CrimeListFragment : Fragment() {
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }
    //текущие коллбеки
    private var callbacks : Callbacks? = null
    //лист криминалов
    private lateinit var crimeRecyclerView : RecyclerView
    //адаптер для генерации элеменов
    private var adapter : CrimeAdapter? = CrimeAdapter(/*emptyList()*/)

    //лист модель
    private val crimeListViewModel : CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    //показать другой фрагмент и привязать его колбеки
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    //отвязать фрагмент и колбеки
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    //после создания и отображения листа начинаем следить за изменениеями в модели
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeRecyclerView.adapter = adapter

        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer {crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
//                    updateUI(crimes)
                    //обновляет только нужные элеметы
                    adapter?.submitList(it)
                }
            }
        )
    }

    //холдер хранит представление для ячейки ресайклера
    private open inner class CrimeHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime : Crime
        var dateFormat = SimpleDateFormat("dd-MM-yyyy")
        //получили контролы
        val titleTextView : TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView : TextView = itemView.findViewById(R.id.crime_date)
        val imageView : ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }

        //привязка данных к вью
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = dateFormat.format(crime.date)

            imageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        //кликабл интерфейс для всей панели
        override fun onClick(view : View) {
//            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
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
    private inner class CrimeAdapter(/*var crimes : List<Crime>*/) : //RecyclerView.Adapter<CrimeHolder>() {
            androidx.recyclerview.widget.ListAdapter<Crime, CrimeHolder>(CrimeDiffCallback()) {//эффективная перезагрузка списка, только новые изменения
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
        //кол во элементов в списке, в лист адаптере не нужен
//        override fun getItemCount() = crimes.size
        //тип элемента
        override fun getItemViewType(position: Int): Int {
            if (getItem(position).requiresPolice) {
                return 1;
            }
            return 0
        }
        //навешние данных из модели
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)//crimes[position]
//            holder.apply {
//                titleTextView.text = crime.title
//                dateTextView.text = crime.date.toString()
//            }
            holder.bind(crime)
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        Log.d(TAG, "Total crimes : ${crimeListViewModel.crimes.size}")
//    }
    //создание контейнера ресайклер
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

//        updateUI()

        return view
    }
    //привязываение адаптера и ресайклера
    private fun updateUI(crimes: List<Crime>) {
//        val crimes = crimeListViewModel.crimes
//        adapter = CrimeAdapter(crimes)
//        crimeRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}

//проверки на изменение элементов
class CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {
    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem == newItem
    }
}