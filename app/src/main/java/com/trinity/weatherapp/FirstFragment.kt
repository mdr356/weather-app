package com.trinity.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.trinity.weatherapp.model.Main
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

   /* @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory*/

    lateinit var viewModel: WeatherViewModel
    lateinit var repository: WeatherRepository

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        viewModel = (activity as MainActivity).viewModel
        repository = (activity as MainActivity).repository
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/

        viewModel.isLoading.value = true

        viewModel.getCurrentWeather(repository, "Brooklyn", getString(R.string.api_key)).observe(context as LifecycleOwner, Observer {
            if(it == null) {
                showErrorDialog()
            } else {
                Log.d("Marc", it.weather.toString())
            }
        })

    }

    private fun showErrorDialog() {
        TODO("Not yet implemented")
    }
}