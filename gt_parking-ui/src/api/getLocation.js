function getLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.watchPosition(showPosition, error);

    function error(err) {
        console.warn(`ERROR(${err.code}): ${err.message}`);
    }

    function showPosition(position) {
      // replace this with function to display location on screen
      console.log( "Latitude: " + position.coords.latitude +
      "<br>Longitude: " + position.coords.longitude);
    }
  } else {
    alert("Geolocation is not supported by your browser.");
  }
}

