Software Requirements: Java 8, latest version of Android Studio and Android SDK

Steps to Build Place Search App
1. Download compressed app source code available available at following Google drive location
2. Unzip app source to any local drive.
3. Open Android Studio
4. From Quick Start Menu, Select "Open an exiting Android Studio Project"
5. Select unzipped source code directory available on local drive from "Open file or Project" dialog
window of Android studio.
6. Update Android SDK path in local.properties file and wait for indexing and Gradle sync.
7. Select Gradle Tool window available on right side. Tasks would be available for 
   app-android-place-search and app.
8. Select assemble gradle command available under build folder of app gradle task.
9. Gradle build would start to build android app for Place search using Foursquare APIs.
10. Internet connection would be required to download dependencies from maven repositories. There 
might be build error in case internet connection is not available.
11. Ideally there would not be any build error by following above steps. Incase there is any build
error, APK file is available at root folder of zip file.
12. In case of successful build, APK file would be available at following path
app-android-place-search/app/build/outputs/apk/debug/app-debug.apk

Notes:
1. There are further improvement possibility in UI Screen designs.
2. Progress bar and Animation can be added to improve User experience.

Known Issue: 
