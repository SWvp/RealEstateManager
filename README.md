<p align="center">
 	<img 
   		width="300"
   		height="300"
   		src="screenshot/logo_real-estate-manager_bck-white.png"
		alt="accueil" 
  	>
</p>

# REALESTATE MANAGER
Android application that allows you to list properties for a real estate agency. It will be possible to login with Gmail, list new property with text and photos, modify them, be up-to-date with other agent, performing search with multiple parameters, convert price to euros/dollars, check your position and that of the properties, marked property as sold. The properties can't be deleted.
This application allows user to get Master/detail view that allows to see property and its details view on same screen, when on tablet mode.

<p align="center">
 	<img alt="demo" src="screenshot/demo2.gif" width="30%">
</p>

## Features
* Authentification with Gmail via Firebase Authentification
* Activity to see the list of properties with the agent name in charge
* Activity to see details of a property with a static map
* Activity to edit property: photos, text, sold button (permanent)
* Activity to add a property
* Activity to search with multiple parameters: price, surface and room range, by interest, the number of photo, property type, county
* Activity with a Goole map, to see your position and that of the properties
* A logout item
* List and details activity are converted to fragment with a master/detail, when on tablet mode.
* Databases:
	* A local Room database
	* A distant Firestore database
	* All databases is up-to-date according to their timestamp creation/changes

## Library
* Material Design:
	* constraint layout
* Glide
* Coroutines
* GoogleMap play services
* Firebase Auth
* Firebase Firestore
* Firebase storage
* Room
* Hilt
* Mockk
* Desugar

## Architecture
* MVVM with an approach of clean Architecture (viewModel, ViewState, Usecase, Repository)
* LiveData for presentation layer
* Flow for data and domain layer

## Dependency injection
* Hilt

## Unit tests
* ViewModel and usecase with Mockk

## How to use this app
* Go to [build.gradle](https://github.com/SWvp/RealEstateManager/blob/main/app/build.gradle) line 20 & 21
* replace GOOGLE_MAP_API_KEY by '"your-google-map-key-here"'
* or edit/create gradle.properties in user\.gradle folder with GOOGLE_MAP_API_KEY="your-google-map-key-here"

Feel free to send feedback about your experience !
Happy coding !

Stéphane
