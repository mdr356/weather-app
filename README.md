# weather-app

I decided to use Retrofit, which is a REST Client for android application.
It is also an open source library and is useful in serializing and deserializing objects
to and from GSON. To make http request retrofit I used the OkHttp Library.

MVVM is used as the architecture. The MVVM patterns have a two data binding between the
View and the Viewmodel. There are many to one relationships between the
Viewmodel and the Model. The View notifies the Viewmodel about different actions.
The View has a reference of the Viewmodel while the Viewmodel does not
have a reference of the View. I chose ViewModel along with LiveData api, to update
the View whenever the ViewModel has updated information, whether from the
repository or the sql data base containing the temperature.

I decided to use Dagger for dependencies injection, which removes large amounts of
boilerplate code.

To inject the Viewmodel, I created a custom ViewModel factory, which makes it
possible to add additional Viewmodels in the future.

I decided to use a JobScheduler to update the View every 2 hours. The reason for
this is because the device goes to sleep, and all the applications are stopped. There
are windows of time when the device is allowed to have a background application to be run.
Therefore, JobScheduler API seemed to be the best to make http requests in the background.

I decided to use SQLiteOpenHelper because as the application grows, the amount
of data will increase as well, which would need to be saved. Therefore, sharedPreference
was not an option.

I have a util class that looks at when the device is on wifi, during job scheduler.
If the device is not on wifi, the call is not made to the OpenWeatherMap API.

I have a util class to also convert the units of the temperature from Kelvin to
Fahrenheit.

For the future, I would add more functionalities, such as the user wouldn't have to
click on a button in order for the app to find it's location. That should be done as soon
as the user opens the app. I would also allow the user to input a specific location
to display. Also I would allow the app to show the temperature data for the whole week.

I'm a believer in tech debt. With that being said, in my queue (every
other Friday in my case) I would add the necessary test cases. Unfortunately, time
did not allow me to do so.
