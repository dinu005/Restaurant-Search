# Restaurant-Search
Android application to list nearby restaurants

#### Architecture
This application uses MVVM architecture using Android architecure components like ViewModel

[Model](app/src/main/java/com/sample/restuarant/search/model/)

[View Model](app/src/main/java/com/sample/restuarant/search/viewmodel)

[View](app/src/main/java/com/sample/restuarant/search/view)

#### Language and Libraries used
* Kotlin
* Retrofit
* RxJava
* Dagger2
* MockWebServer

#### setup
The application uses Foursquare api to show nearby restaurants on Google Map.
The key for Foursquare Api and Google maps are defined in gradle.properties

You need to add below keys to the [gradle.properties](/gradle.properties)
- client_id
- client_secret
- google_maps_key

In [build.gradle](/app/build.gradle) file the string resources are created during compile time and used in the application.

#### Endpoint
* FourSquare venue search

###### API Layer
Used Retrofit to retrieve the nearby restaurants. Currently this app uses FourSquare userless authentication method.
The Client Id and secret are added to in the interceptor instead of adding it in the [network controller](app/src/main/java/com/sample/restuarant/search/controller/RestaurantService.kt).  
In future if we need to change the authentication mode we will make changes to the creds interceptor instead of the controller.  
Also added error interceptor to log Api failures to Google Analytics or to Crashlytics

Tested this layer using MockWebServer.

[Network controller](app/src/main/java/com/sample/restuarant/search/controller/RestaurantService.kt) makes the API call and transforms the data from the Api to data Model which is to be used across the application.  
The intention of having the data transformation is to minimize the changes when API contract is changed in future.

###### View Model
The purpose of using the view model is to manage the data when the application undergo's lifecycle changes.
Reason being not to make the Api calls on screen orientation unless there is a change in the Map events and also updates the UI upon new data from the API.

###### View
The application is a Single activity application [Activity](/app/src/main/java/com/sample/restuarant/search/view/MainActivity.kt)
Currently the Map Fragment is added statically in the Activity layout file. And all the logic of loading the map with restaurants are added to MapViewController
The best way to scale the application is to add the map fragment dynamically.

The users current location is fetched using the [Current Location Service](/app/src/main/java/com/sample/restuarant/search/service/CurrentLocationService.kt)

The Main Activity extends [Restaurant Base Activity](/app/src/main/java/com/sample/restuarant/search/view/RestaurantBaseActivity.kt). Added the logic to ask for location permission in the application.  
All common views will be added to the base activity.

The logic to retrieve new API data is based on the user interaction with the application. Implemented GoogleMap camera state listener for idle, upon calcuating the bounds of the viewable map will determine the distance from the users current location.

###### Assumptions
- Device location is always enabled.

###### Limitations
- The user will be able to see the list of restaurants around him.












