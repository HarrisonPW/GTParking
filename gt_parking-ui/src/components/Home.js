import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, CircleMarker } from 'react-leaflet';
import { RemoteStorage } from 'remote-storage'
import 'leaflet/dist/leaflet.css'; // Import Leaflet styles
import './Home.css';
import L from 'leaflet';
import getAllParkingLots from "../api/getDatabase";
import ParkingLotOptions from "./ParkingLotOptions";

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

const greenIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const yellowIcon = new L.Icon({
  iconUrl: 'https://github.com/pointhi/leaflet-color-markers/blob/master/img/marker-icon-2x-gold.png?raw=true',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const redIcon = new L.Icon({
  iconUrl: 'https://github.com/pointhi/leaflet-color-markers/blob/master/img/marker-icon-2x-red.png?raw=true',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const Home = () => {
    const [destination, setDestination] = useState('');
    const [parkingLots, setParkingLots] = useState([]);
    const [userLocation, setUserLocation] = useState(null);
    const [userId, setUserId] = useState('');
    const remoteStorage = new RemoteStorage({userId: 2})

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

        const setRootUser = async() => {
            const rootUserId = await remoteStorage.getItem('rootUserId');
            console.log(rootUserId);
            if (!rootUserId) {
                console.log(id);
                await remoteStorage.setItem('rootUserId', id);
            }
        }
        setRootUser();

        const fetchParkingLots = async () => {
            try {
                // Check if we already have the parking lots data in remoteStorage
                const cachedParkingLots = await remoteStorage.getItem('parkingLots');
                console.log(cachedParkingLots)
                if (cachedParkingLots) {
                    // If we do, use the cached data instead of fetching it again
                    console.log('cached');
                    setParkingLots(cachedParkingLots);
                } else {
                    console.log('fetching');
                    // If not, fetch the data and store it in both state and remoteStorage
                    const fetchedParkingLots = await getAllParkingLots();
                    await remoteStorage.setItem('parkingLots', fetchedParkingLots);
                    setParkingLots(fetchedParkingLots);
                }
            } catch (error) {
                console.error('Failed to fetch parking lots:', error);
            }
        };
        fetchParkingLots();

        const routineFetchParkingLots = async () => {
            const rootUserId = await remoteStorage.getItem('rootUserId');
            if (rootUserId === id) {
                try {
                    console.log("Routine Fetch")
                    const fetchedParkingLots = await getAllParkingLots();
                    await remoteStorage.setItem('parkingLots', fetchedParkingLots);
                } catch (error) {
                    console.error('Failed to fetch parking lots:', error);
                }
            }
            const cachedParkingLots = await remoteStorage.getItem('parkingLots');
            setParkingLots(cachedParkingLots);
        };


        const fetchParkingId = setInterval(() => {
            routineFetchParkingLots(); // Then fetch every minute
        }, 5000); // 60000 milliseconds = 1 minute

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
        return () => {
            clearInterval(intervalId);
            clearInterval(fetchParkingId)
        };
    }, []);

    const getOccupancyColor = (occupancy) => {
        if (occupancy < 40) return 'green';
        if (occupancy < 70) return 'yellow';
        return 'red';
    };

    function getOccupancyIcon(occupancy) {
    if (occupancy < 40) {
        return greenIcon;
    } else if (occupancy < 70) {
        return yellowIcon;
    } else {
        return redIcon;
    }
}

    return (
        <div className="home-container">
            <MapContainer center={[33.775237150193355, -84.3936369094809]} zoom={13} className="map-view">
                <TileLayer
                    // url = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                    url="https://api.mapbox.com/styles/v1/jodi2023/cltpmm7k600et01p5e95pbodv/tiles/256/{z}/{x}/{y}@2x?access_token=pk.eyJ1Ijoiam9kaTIwMjMiLCJhIjoiY2x0cGlnMHN5MHJteTJrbmlvaWh0a2MxdyJ9.M_ptTEE9hDlCk8g8_73j4g"
                />

                {parkingLots.map((lot) => (
                    <Marker key={lot.id} position={lot.coordinates} icon={getOccupancyIcon(lot.occupancy)}>
                        <Popup>
                            <div>
                                <h2>{lot.name}</h2> {/* Parking lot name */}
                                <p>Location: {lot.location}</p> {/* Additional detail: ID */}
                                <p>Available Spots: {lot.availableSpots}</p> {/* Additional detail: ID */}
                            </div>
                        </Popup>
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
                <div className="search-bar">
                    <input
                        type="text"
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                        placeholder="Where are you going to?"
                        className="destination-input"
                    />
                </div>

                <ParkingLotOptions parkingLots={parkingLots} getOccupancyColor={getOccupancyColor} />
            </div>
            {/* More content that should scroll over the map */}
        </div>
    );
};

export default Home;