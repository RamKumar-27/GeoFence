# GEOFENCING APPLICATION

## ABOUT 
By using this application, whenever the user navigates to or from the geofencing location the user is notified by a push notification

## TECHNOLOGIES USED
	Design Pattern : MVVM(Basic Architecture)
	Language       : Kotlin
	Google APIs    : GoogleMaps, GeoFencing, Firebase Crashlytics
	Others         : ViewBinding
	
## TECHNOLOGY AND MODULE NOTE
This Application includes:
* View
* ViewModel
* Engine
* Push Notifications
### View
 The View is nothing but, its an User Interface
* Maps
> Google Map is used to Show the user Current location and also find where the GeoFence location is pointed
 
 ### ViewModel
 >In MVVM design pattern, ViewModel is a class that is responsible for preparing and managing the data for an Activity or a Fragment, also to hold and manage UI-related data in a life-cycle conscious way. 
 
 ### Engine
 Engine is used to create and manage the GeoFence
 * GeoFence
 >A geofence is a virtual perimeter for a real-world geographic area. A geo-fence could be dynamically generated—as in a radius around a point location, or a geo-fence can be a predefined set of boundaries
 
 * GeoFenceReciver
 >It is used to recive wether the user is entered or exited from the location. It will be show the notification to the User
 
 ### Push Notifications
 >A push notification is a message that pops up on a mobile device, it provide convenience and value to app users.
  
## WORKFLOW 
  ### DEMO
  https://user-images.githubusercontent.com/18497287/129335224-829167bf-5039-49c3-a2c1-4ee0689da288.mp4


 * When the user enters the app for the very first time, it asks for Location Permission (Need to give allow all).
 <img src="screenshot/permission.JPEG" width="150" height="320"> 
 
 * When the user allows for accessing his location, the Enable Location Dialog is displayed.
 <img src="screenshot/enable_loc.jpeg" width="150" height="320"> 
  
 * When the user location is enabled, the map shows the exact current location of the user.
 <img src="screenshot/home.jpeg" width="150" height="320"> 
 
  * Whenever the user taps on any location, the user can enable the GeoFencing location for that tapped location in the map.
  * While tapping a location, the location is surrounded with radius of certain distance, it can be increased or decreased manually.
  * After selecting the location and then enable button is clicked.Now the geoFencing for that location is enabled
 <img src="screenshot/process.JPEG" width="150" height="320"> 
 
 * Now whenever the user navigates through that geofenced location i.e., the user may enter or exit that location, the user is notified with a popup notification.
 <img src="screenshot/callback.jpeg" width="150" height="320">
 
### The Application is also having crashlytics integrated.
