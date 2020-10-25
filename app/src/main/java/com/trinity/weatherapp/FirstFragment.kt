package com.trinity.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.trinity.weatherapp.database.DataBaseHandler
import com.trinity.weatherapp.model.Main
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.util.TemperatureConverter
import com.trinity.weatherapp.viewmodel.WeatherViewModel

class FirstFragment : Fragment() {

    lateinit var viewModel: WeatherViewModel
    lateinit var repository: WeatherRepository

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        repository = (activity as MainActivity).repository
        val btnGetLocation = view.findViewById<TextView>(R.id.btnGetLocation)
        val loadingIndicator = view.findViewById<ProgressBar>(R.id.loadingIndicator)
        val mydb = DataBaseHandler(activity as MainActivity)
        val mainView = view.findViewById<LinearLayout>(R.id.mainview)

        viewModel.isLoading.observe(
            context as MainActivity, Observer{ isLoading: Boolean ->
                if (isLoading) loadingIndicator.visibility = (View.VISIBLE)
                else loadingIndicator.visibility = (View.GONE)
            })


        btnGetLocation.setOnClickListener {
            viewModel.readFromDataBase(mydb).observe(context as LifecycleOwner, Observer {
                if (it != null) {
                    btnGetLocation.visibility = View.GONE
                    populateView(view,it)
                    btnGetLocation.visibility = View.GONE
                    mainView.visibility = View.VISIBLE
                } else {
                    (context as MainActivity).checkPermissions()
                    (context as MainActivity).startLocationUpdates()
                    btnGetLocation.visibility = View.GONE
                    mainView.visibility = View.VISIBLE
                    viewModel.isLoading.postValue(true)
                }
            })
        }

        viewModel.readFromDataBase(mydb).observe(context as LifecycleOwner, Observer {
            if (it != null) {
                btnGetLocation.visibility = View.GONE
                populateView(view, it)
                btnGetLocation.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            } else {
                btnGetLocation.visibility = View.VISIBLE
                mainView.visibility = View.GONE
            }
        })



        viewModel.tempAddress.observe(context as LifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.address).text = it
        })

        viewModel.latLng.observe(context as LifecycleOwner, Observer {
            viewModel.isLoading.postValue(true)

            viewModel.getCurrentWeather(
                        (activity as MainActivity).request,
                        activity as MainActivity, repository,
                        it.latitude,
                        it.longitude,
                        getString(R.string.api_key)
                    ).observe(context as LifecycleOwner, Observer {
                        if (it == null) {
                            loadingIndicator.visibility = View.GONE
                            view.findViewById<LinearLayout>(R.id.mainview).visibility = View.GONE
                            view.findViewById<TextView>(R.id.errorview).visibility = View.VISIBLE
                        } else {
                            view.findViewById<TextView>(R.id.temp).text = it.main?.temp?.let { it -> TemperatureConverter().fahrenheit(it).toString()}
                            view.findViewById<TextView>(R.id.feelsLike).text = it.main?.feelsLike?.let { it -> TemperatureConverter().fahrenheit(it).toString()}
                            view.findViewById<TextView>(R.id.pressure).text =
                                it.main?.pressure.toString()
                            view.findViewById<TextView>(R.id.humidity).text =
                                it.main?.humidity.toString()
                            view.findViewById<TextView>(R.id.address).text = viewModel.tempAddress.value
                            viewModel.addToDataBase(mydb, it, viewModel.zipcode.value!!, viewModel.tempAddress.value!!)
                            mainView.visibility = View.VISIBLE
                            btnGetLocation.visibility = View.GONE
                            loadingIndicator.visibility = View.GONE
                        }
                    })
        })
    }

    private fun populateView(
        view: View,
        screenData: DataBaseHandler.TempResponse
    ) {
        view.findViewById<TextView>(R.id.temp).text = screenData.temp.toString()
        view.findViewById<TextView>(R.id.feelsLike).text =
            screenData.feelsLike.toString()
        view.findViewById<TextView>(R.id.pressure).text =
            screenData.pressure.toString()
        view.findViewById<TextView>(R.id.humidity).text =
            screenData.humidity.toString()

        viewModel.isLoading.postValue(false)

    }
}