import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, CircleMarker } from 'react-leaflet';
import 'leaflet/dist/leaflet.css'; // Import Leaflet styles
import './Home.css';
import L from 'leaflet';
import getAllParkingLots from "../api/getDatabase";

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

const Home = () => {
    const [destination, setDestination] = useState('');
    const [parkingLots, setParkingLots] = useState([]);
    const [userLocation, setUserLocation] = useState(null);
    const [userId, setUserId] = useState('');


    // This would be fetched from an API
    useEffect(() => {
        // Placeholder data
        // const fetchedParkingLots = [
        //   { id: 1, name: 'E52 Peters Parking Deck', occupancy: 8, coordinates: [33.775292682929816, -84.39363979653825] },
        //   { id: 2, name: 'E40 Klaus Deck', occupancy: 52, coordinates: [33.78013695843671, -84.39842422570989] },
        //   { id: 3, name: 'ER66 10th & Home', occupancy: 78, coordinates: [33.78190123308837, -84.39493615910393] },
        //   { id: 4, name: 'Visitor\'s Parking 2', occupancy: 43, coordinates: [33.77388599065813, -84.39957233391993] },
        //   // ...other parking lots
        // ];
        function generateUniqueId() {
            return Date.now().toString(36) + Math.random().toString(36).substr(2);
        }
         let id = sessionStorage.getItem('userId');
            if (!id) {
                id = generateUniqueId();
                sessionStorage.setItem('userId', id);
            }
            setUserId(id);
        const fetchParkingLots = async () => {
            try {
                process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
                const fetchedParkingLots = await getAllParkingLots();
                setParkingLots(fetchedParkingLots);
            } catch (error) {
                console.error('Failed to fetch parking lots:', error);
            }
        };
        // fetchParkingLots();

        const updateLocation = () => {
            if ('geolocation' in navigator) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        setUserLocation([position.coords.latitude, position.coords.longitude]);
                    },
                    (error) => {
                        console.error('Error fetching the position:', error);
                    },
                    { enableHighAccuracy: true }
                );
            } else {
                console.error('Geolocation is not supported by this browser.');
            }
        };

        // Update location immediately when component mounts
        updateLocation();

        // Set an interval to update location every 5 seconds
        const intervalId = setInterval(updateLocation, 5000);

        // Cleanup interval on component unmount
        return () => clearInterval(intervalId);
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

                {userLocation && (
                   <CircleMarker
                    center={userLocation}
                    radius={10} // Radius in pixels
                    color="red"
                    fillColor="#f03"
                    fillOpacity={0.5}
                  >
                    <Popup>
                      User ID: {userId}
                    </Popup>
                  </CircleMarker>
                )}
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