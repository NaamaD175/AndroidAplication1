# RaceRush – Android Game App

**RaceRush** is a fun and interactive Android game, developed as part of the course *Mobile Application Development* by Naama Drory.

The objective of the game is to control a car, avoid obstacles, and collect fuel. Players can choose between button control or tilt sensor control. The app saves high scores with distance and location and displays them on a map.

---

## Main Features

-  **Two control modes**: tilt sensor (accelerometer) or on-screen buttons
-  **Collect fuel** and **avoid obstacles**
-  **Real-time score and distance tracking**
-  **Game Manager logic**: manages collisions, movements, lives, and game loop
-  **Automatic game reset** when all lives are lost
-  **Sound effects and background music**

---

##  High Scores Screen

- Displays top 10 scores of all time
- Shows name, score, distance, and location
- Uses `SharedPreferences` to store scores locally
- Saves score only if it's in the top 10

---

## ️ Google Maps Integration

- Pins the GPS location where each high score was achieved
- Clicking on a score centers the map to that location
- Uses `MapFragment` and `Google Maps SDK for Android`
- Asks for location permission and handles missing permission gracefully

---

##  Technologies Used

- Android Studio + Kotlin
- Fragments and RecyclerView
- Google Maps API
- SharedPreferences
- SensorManager (accelerometer)
- LocationServices (GPS)
- MediaPlayer / SoundPool (game sounds)

---

##  How to Run

1. Open the project in **Android Studio**
2. Add your Google Maps API key to `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
3. Run the app on a physical device or emulator

