import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css'; // Import Leaflet styles
import './Home.css';
import L from 'leaflet';

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

const Home = () => {
  const [destination, setDestination] = useState('');
  const [parkingLots, setParkingLots] = useState([]);

  // This would be fetched from an API
  useEffect(() => {
    // Placeholder data
    const fetchedParkingLots = [
      { id: 1, name: 'E52 Peters Parking Deck', occupancy: 8, coordinates: [33.775292682929816, -84.39363979653825] },
      { id: 2, name: 'E40 Klaus Deck', occupancy: 52, coordinates: [33.78013695843671, -84.39842422570989] },
      { id: 3, name: 'ER66 10th & Home', occupancy: 78, coordinates: [33.78190123308837, -84.39493615910393] },
      { id: 4, name: 'Visitor\'s Parking 2', occupancy: 43, coordinates: [33.77388599065813, -84.39957233391993] },
      // ...other parking lots
    ];
    setParkingLots(fetchedParkingLots);
  }, []);

  const getOccupancyColor = (occupancy) => {
    if (occupancy < 40) return 'green';
    if (occupancy < 70) return 'yellow';
    return 'red';
  };

  return (
    <div className="home-container">
        <input
            type="text"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            placeholder="Where are you going to?"
            className="destination-input"
        />
        <MapContainer center={[33.775237150193355, -84.3936369094809]} zoom={13} className="map-view">
            <TileLayer
                url="https://api.mapbox.com/styles/v1/jodi2023/cltpmm7k600et01p5e95pbodv/tiles/256/{z}/{x}/{y}@2x?access_token=pk.eyJ1Ijoiam9kaTIwMjMiLCJhIjoiY2x0cGlnMHN5MHJteTJrbmlvaWh0a2MxdyJ9.M_ptTEE9hDlCk8g8_73j4g"
                // url="https://api.mapbox.com/styles/v1/mapbox/streets-v12/tiles/{tilesize}/{z}/{x}/{y}{@2x}?access_token=pk.eyJ1Ijoiam9kaTIwMjMiLCJhIjoiY2x0cGtqNHhxMHN1cjJxdDBlMHY5MHAxdyJ9.SVebeO8ZkGbYwkYswcPHcA"
                // attribution="Map data &copy; OpenStreetMap contributors, Imagery © Mapbox"
                // tileSize={512}
                // zoomOffset={-1}
                // detectRetina={true}
            />

            {parkingLots.map((lot) => (
                <Marker key={lot.id} position={lot.coordinates}>
                <Popup>{lot.name}</Popup>
                </Marker>
            ))}
        </MapContainer>

        <div className="overlay-container">
          {/* Put your scrollable content here */}
          <div className="search-bar">
            <input
              type="text"
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              placeholder="Where are you going to?"
              className="destination-input"
            />
          </div>

          <div className="parking-lots-list">
              {parkingLots.map((lot) => (
              <div key={lot.id} className={`parking-lot ${getOccupancyColor(lot.occupancy)}`}>
                  {lot.name} - {lot.occupancy}% Occupancy
              </div>
              ))}
          </div>
        </div>
        {/* More content that should scroll over the map */}
    </div>
  );
};

export default Home;