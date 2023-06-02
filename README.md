


<h1 align="center">Location Reminder</h1>

<p align="center">

<!-- <a href="https://github.com/romanyn36/location_reminder/graphs/contributors">

  <img src="https://img.shields.io/github/contributors/romanyn36/location_reminder.svg?style=for-the-badge" alt="Contributors" />
  
</a> -->





<!-- <a href="https://github.com/romanyn36/location_reminder/issues">

  <img src="https://img.shields.io/github/issues/romanyn36/location_reminder.svg?style=for-the-badge" alt="issues" />
</a> -->
   <img alt="Github top language" src="https://img.shields.io/github/languages/top/romanyn36/location_reminder?color=56BEB8">
<!-- 
  <img alt="Github language count" src="https://img.shields.io/github/languages/count/romanyn36/location_reminder?color=56BEB8">
   -->

  <!-- <img alt="Repository size" src="https://img.shields.io/github/repo-size/romanyn36/location_reminder?color=56BEB8"> -->

  <!-- <img alt="License" src="https://img.shields.io/github/license/romanyn36/location_reminder?color=56BEB8"> -->

  <!-- <img alt="Github issues" src="https://img.shields.io/github/issues/romanyn36/location_reminder?color=56BEB8" /> -->

<!-- <a href="https://github.com/romanyn36/location_reminder/network/members"> -->

<!-- <img alt="Github forks" src="https://img.shields.io/github/forks/romanyn36/location_reminder.svg?color=56BEB8" /> -->

  <!-- <img src="https://img.shields.io/github/forks/romanyn36/location_reminder.svg?style=for-the-badge" alt="forks" /> -->


  

<!-- stars -->
  <a href="https://github.com/romanyn36/location_reminder/stargazers">
  
  <img alt="Github stars" src="https://img.shields.io/github/stars/romanyn36/location_reminder?color=56BEB" />

  <!-- <img src="https://img.shields.io/github/stars/romanyn36/location_reminder.svg?style=for-the-badge" alt="stars" /> -->

</a>






</p>
<p align="center">
 <a href="https://www.facebook.com/romanyn3/" target="_blank">
  <img src="https://img.shields.io/badge/-Romani-1877F2?style=flat&logo=facebook&logoColor=white" alt="Facebook" />
</a>

<a href="https://twitter.com/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-1DA1F2?style=flat&logo=twitter&logoColor=white" alt="Twitter" />
</a>

<!-- <a href="https://www.instagram.com/romanyn36/" target="_blank">
  <img src="https://img.shields.io/badge/-romanyn36-E4405F?style=flat&logo=instagram&logoColor=white" alt="Instagram" />
</a> -->


<!-- <a href="mailto:youremail@example.com" target="_blank">
  <img src="https://img.shields.io/badge/-Email-D14836?style=flat&logo=mail.ru&logoColor=white" alt="Email" />
</a> -->

<a href="https://www.linkedin.com/in/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-0077B5?style=flat&logo=linkedin&logoColor=white" alt="LinkedIn" />
</a>

<a href="https://github.com/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-181717?style=flat&logo=github&logoColor=white" alt="GitHub" />
</a>

</p>



<!-- TABLE OF CONTENTS -->

  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#dart-about">About</a></li>
    <li><a href="#sparkles-features">Features</a></li>
    <li><a href="#rocket-technologies">Technologies</a></li>
    <li><a href="#checkered_flag-Starting-and-installation">Starting</a></li>
     <li><a href="#testing">Testing</a></li>
      <li><a href="#built-with">Built With</a></li>
    <li><a href="#email-contact">Contact</a></li>
    <li><a href="#memo-license">License</a></li>

  </ol>

<br>

<p align="center" id="top"> 
<img src="/images/login.jpg" alt="Blood_bank Project" width="200" height="300" />
<img src="/images/list.jpg" alt="Blood_bank Project" width="200" height="300" /> 
<img src="/images/info.jpg" alt="Blood_bank Project" width="200" height="300" />


<img src="/images/map.jpg" alt="Blood_bank Project" width="200" height="300" />

</p>

####  try the Location Reminders App :[APK](location_reminder.apk)
## :dart: About ##

A Todo list app with location reminders that remind the user to do something when he reaches a specific location. The app will require the user to create an account and login to set and access reminders.

## :sparkles: Features ##
- :heavy_check_mark: Login/Register users using an email address or a Google account.
  
- :heavy_check_mark: shows a map with the user's current location and asks the user to select a point of interest to create a reminder.
- :heavy_check_mark: add a reminder when a user reaches the selected location
- :heavy_check_mark: Reminder data should be saved to local storage.
- :heavy_check_mark: For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area.


## :rocket: Technologies ##

The following tools were used in this project:
- Firebase authentication, Maps API
- Geofencing
- Kotlin, Coroutines
- Espresso, Mockito
- MVVM, LiveData 
- Android Testing ,UnitTesting, MainCoroutineRule, FakeData Source, dependency injection, End-To-End
- Android Studio


## :checkered_flag: Starting and installation ##
Step by step explanation of how to get a dev environment running.
```bash
# Clone this project
$ git clone https://github.com/romanyn36/location_reminder.git
# Access
$ cd starter
```

```
1. To enable Firebase Authentication:
        a. Go to the authentication tab at the Firebase console and enable Email/Password and Google Sign-in methods.
        b. download `google-services.json` and add it to the app.
2. To enable Google Maps:
    a. Go to APIs & Services at the Google console.
    b. Select your project and go to APIs & Credentials.
    c. Create a new api key and restrict it for android apps.
    d. Add your package name and SHA-1 signing-certificate fingerprint.
    c. Enable Maps SDK for Android from API restrictions and Save.
    d. Copy the api key to the `google_maps_api.xml`
3. Run the app on your mobile phone or emulator with Google Play Services in it.
```






## Testing
app contain testing :
- ViewModels, Coroutines and LiveData objects.
- FakeDataSource replace the Data Layer and test the app in isolation.
- Espresso and Mockito to test each screen of the app
- Test DAO (Data Access Object) and Repository classes.
- testing for the error messages.
- End-To-End testing for the Fragments navigation.
```
Right click on the `test` or `androidTest` packages and select Run Tests
```


## Built With

* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.

</br>

### :email: Contact ##
<p align="center">
 <a href="https://www.facebook.com/romanyn3/" target="_blank">
  <img src="https://img.shields.io/badge/-Romani Nasser-1877F2?style=flat&logo=facebook&logoColor=white" alt="Facebook" />
</a>

<!-- <a href="https://twitter.com/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-1DA1F2?style=flat&logo=twitter&logoColor=white" alt="Twitter" /> -->
</a>

<!-- <a href="https://www.instagram.com/romanyn36/" target="_blank">
  <img src="https://img.shields.io/badge/-romanyn36-E4405F?style=flat&logo=instagram&logoColor=white" alt="Instagram" />
</a> -->


<!-- <a href="mailto:youremail@example.com" target="_blank">
  <img src="https://img.shields.io/badge/-Email-D14836?style=flat&logo=mail.ru&logoColor=white" alt="Email" />
</a> -->

<!-- <a href="https://www.linkedin.com/in/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-0077B5?style=flat&logo=linkedin&logoColor=white" alt="LinkedIn" />
</a> -->

<a href="https://github.com/romanyn36" target="_blank">
  <img src="https://img.shields.io/badge/-@romanyn36-181717?style=flat&logo=github&logoColor=white" alt="GitHub" />
</a>
<a href="https://stackoverflow.com/users/17348975/romani" target="_blank">
  <img src="https://img.shields.io/badge/-Stack%20Overflow-FE7A16?style=flat&logo=stackoverflow&logoColor=white" alt="Stack Overflow" />
</a>
</p>

<!-- social links -->
<p align="center">
<a href="https://romanyn36.github.io" target="_blank">
  <img height="50" src="https://user-images.githubusercontent.com/46517096/166972883-f5f1d88c-0246-4374-88ac-ded0f2cf0699.png"/>
</a>

<a href="https://www.linkedin.com/in/romanyn36/" target="_blank">
  <img height="50" src="https://user-images.githubusercontent.com/46517096/166973395-19676cd8-f8ec-4abf-83ff-da8243505b82.png"/>
</a>

<a href="https://dev.to/romanyn36" target="_blank">
  <img height="50" src="https://user-images.githubusercontent.com/46517096/166974096-7aeecad4-483e-4c85-983f-f4b37b3f794e.png"/>
</a>

<a href="https://twitter.com/romanyn36" target="_blank">
  <img height="50" src="https://user-images.githubusercontent.com/46517096/166974271-91dfa250-d70b-4cb9-8707-f1bda1b708c3.png"/>
</a>

</p>



## :memo: License ##

This project is under  [Open Source](https://bumptech.github.io/glide/dev/open-source-licenses.html) license


Made by <a href="https://github.com/romanyn36" target="_blank">Romani</a>

&#xa0;

<a href="#top">Back to top</a>


