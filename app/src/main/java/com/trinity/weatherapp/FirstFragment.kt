package com.trinity.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.trinity.weatherapp.database.DataBaseHandler
import com.trinity.weatherapp.model.Main
import com.trinity.weatherapp.model.WeatherMapResponse
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
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

        view.findViewById<TextView>(R.id.btnGetLocation).setOnClickListener {
            /*val nManager: location =
                getSystemService<Any>(Context.LOCATION_SERVICE) as LocationManager*/

            /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS()
            } else {
                getLocation()
            }*/
            (context as MainActivity).checkPermissions()
            (context as MainActivity).startLocationUpdates()
        }

        val mydb: DataBaseHandler = DataBaseHandler(activity as MainActivity)

        viewModel.isLoading.value = true

        viewModel.latLng.observe(context as LifecycleOwner, Observer {
                    viewModel.getCurrentWeather(
                        (activity as MainActivity).request,
                        activity as MainActivity, repository,
                        it.latitude,
                        it.longitude,
                        getString(R.string.api_key)
                    ).observe(context as LifecycleOwner, Observer {
                        if (it == null) {
                            // showErrorDialog()
                        } else {
                            view.findViewById<TextView>(R.id.temp).text = it.main?.temp.toString()
                            view.findViewById<TextView>(R.id.feelsLike).text =
                                it.main?.feelsLike.toString()
                            view.findViewById<TextView>(R.id.tempMin).text =
                                it.main?.tempMin.toString()
                            view.findViewById<TextView>(R.id.tempMax).text =
                                it.main?.tempMax.toString()
                            view.findViewById<TextView>(R.id.pressure).text =
                                it.main?.pressure.toString()
                            view.findViewById<TextView>(R.id.humidity).text =
                                it.main?.humidity.toString()
                            /*mydb.insertData(
                                response = it,
                                longitude = this.viewModel.latLng.value?.longitude.toString(),
                                latitude = this.viewModel.latLng.value?.latitude.toString()
                            )*/
                        }
                    })
        })
    }

    /*private fun showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity as MainActivity);
        builder.setMessage(R.string.deleteContact)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mydb.deleteContact(id_To_Update);
                    Toast.makeText(getApplicationContext(), "Deleted Successfully",
                        Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            })
    }*/
}