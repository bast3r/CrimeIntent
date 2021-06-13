package by.dazerty.crimeintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
//фрагмент для детального отображения преступления
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime : Crime
    private lateinit var titleField : EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckBox: CheckBox
    //create viewmodel before first use
    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
        //read crime id
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        Log.d(TAG, "args ${crimeId}")
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

//        dateButton.apply {
//            text = crime.date.toString()
//        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    //обновление экрана детализаци
    private fun updateUI() {
        titleField.setText(crime.title)
//        dateButton.text = crime.date.toString()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        dateButton.text = dateFormat.format(crime.date)

//        solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()
        //слежение за текстом для тайтла
        val titleWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        //слежение за чек боксом решения проблемы
        solvedCheckBox.apply {
            setOnCheckedChangeListener {
                _, isChecked -> crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
//            DatePickerFragment().apply {
//                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
//            }
            //show datepicker dialog window
            DatePickerFragment.newInstance(crime.date).apply {
                //установка получателя ответа в будущем
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    //сохранение изменений преступления при выходе из экрана
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    //создание нового обьекта для показа деталей
    companion object {
        fun newInstance(crimeId : UUID) : CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId) //передача как параметра
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

}