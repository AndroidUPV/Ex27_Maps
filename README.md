# Ex27_Maps

Lecture 07 - Geolocation and Maps

The app waits for a Google Map to be ready before activating the rest of functionalities.

The Google maps key must be located in a file named keystore.properties located at the root of the project (at the same level as local.properties).
It must contain a single line with the following format: GOOGLE_MAPS_KEY="<insert_here_your_google_maps_key>"
This file must not be shared on Github, so your key is not publicly visible.

The action menu enables:
- Displaying the map in Normal, Terrain, and Satellite mode.
- Adding markers by clicking on the map. A BottomSheetDialog allows the user to set the marker's title, description, and color.
- Removing markers from the map by clicking on them.

The information of the marker is shown (in an InfoWindow) after clicking on the marker.

If the InfoWindow is clicked, the camera is animated to zoom in and center the marker on the screen.