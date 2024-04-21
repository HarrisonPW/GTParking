import React, { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, CircleMarker } from 'react-leaflet';
import { RemoteStorage } from 'remote-storage'
import 'leaflet/dist/leaflet.css'; // Import Leaflet styles
import './Home.css';
import L from 'leaflet';
import getAllParkingLots from "../api/getDatabase";
import ParkingLotOptions from "./ParkingLotOptions";
import Filters from "./Filters";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';


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
    const remoteStorage = new RemoteStorage({userId: 30})
    const [parkingStatus, setParkingStatus] = useState({isParked: false});
    const [selectedLot, setSelectedLot] = useState(null);
    const markerRefs = useRef({});
    const [showFilters, setShowFilters] = useState(false);
    const [selectedPermits, setSelectedPermits] = useState([]);
    const [permits, setPermits] = useState([]); //Dummy data to be filled
    const [filteredParkingLots, setFilteredParkingLots] = useState([]);


    // Fetched from an API
    useEffect(() => {
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

        // Set the user's Parking Status
        const fetchParkingStatus = async () => {
            try {
                const data = {isParked: false} // Get the parking status (DUMMY DATA)
                setParkingStatus({ isParked: data.isParked});
            } catch (error) {
                console.error('Failed to fetch parking status:', error);
            }
        };

        fetchParkingStatus();

        // Cleanup interval on component unmount
        return () => {
            clearInterval(intervalId);
            clearInterval(fetchParkingId)
        };
    }, []);

    // Extract permits from parking lots whenever parking lots has an update
    useEffect(() => {
        const allPermits = parkingLots.map(lot => lot.permit);
        const uniquePermits = Array.from(new Set(allPermits));
        setPermits(uniquePermits);
    },[parkingLots]);

    // Add link for selecting a lot and the popup marker
    useEffect(() => {
        if (selectedLot && markerRefs.current[selectedLot.id]) {
            markerRefs.current[selectedLot.id].openPopup();
        }
    }, [selectedLot]);

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
    // If the user selects a permit,
    // this functions called and the permit is added to the list of selected permits
    const togglePermit = (permit) => {
        setSelectedPermits((prevSelectedPermits) =>
            prevSelectedPermits.includes(permit)
                ? prevSelectedPermits.filter((p) => p !== permit)
                : [...prevSelectedPermits, permit]
        );
    };

    // Filter parking lots based on selected permits
    useEffect(() => {
        const getFilteredParkingLots = () => {
            // If no permits are selected, show all lots
            if (selectedPermits.length === 0) return parkingLots;
            // If there is a permit selection show only those with matching permits
            return parkingLots.filter(lot => selectedPermits.includes(lot.permit));
        };
        setFilteredParkingLots(getFilteredParkingLots());
    }, [selectedPermits, parkingLots]);

    return (
        <div className="home-container">
            <MapContainer center={[33.775237150193355, -84.3936369094809]} zoom={13} className="map-view" whenCreated={(mapInstance) => {
                mapInstance.on('click', () => {
                    setShowFilters(false);
                });
            }}>
                <TileLayer
                    // url = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                    url="https://api.mapbox.com/styles/v1/jodi2023/cltpmm7k600et01p5e95pbodv/tiles/256/{z}/{x}/{y}@2x?access_token=pk.eyJ1Ijoiam9kaTIwMjMiLCJhIjoiY2x0cGlnMHN5MHJteTJrbmlvaWh0a2MxdyJ9.M_ptTEE9hDlCk8g8_73j4g"
                />

                {filteredParkingLots.map((lot) => (
                    <Marker key={lot.id} position={lot.coordinates} icon={getOccupancyIcon(lot.occupancy)}
                            eventHandlers={{
                                click: () => {
                                    setSelectedLot(lot);
                                },
                            }}
                            ref={(ref) => {
                                markerRefs.current[lot.id] = ref;
                            }}
                    >
                        <Popup>
                            <div>
                                <h2>{lot.name}</h2> {/* Parking lot name */}
                                <p>Location: {lot.location}</p> {/* Additional detail: ID */}
                                <p>Available Spots: {lot.availableSpots}</p> {/* Additional detail: ID */}
                                <p>Permit: {lot.permit}</p>
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
                <div className={`statusBar ${parkingStatus.isParked  ? 'parked' : 'notParked'}`}>
                    {parkingStatus.isParked ? `Parked` : 'Not parked'}
                </div>

                {/* Show the Filters on click */}
                <button className="menu-button" onClick={() => setShowFilters(!showFilters)}>
                    <FontAwesomeIcon icon={faBars} />
                </button>

                {/* Show the Filters if showFilters = true*/}
                {showFilters && (
                    <Filters permits={permits} selectedPermits={selectedPermits} togglePermit={togglePermit} />
                )}

                {/*<div className="search-bar">*/}
                {/*    <input*/}
                {/*        type="text"*/}
                {/*        value={destination}*/}
                {/*        onChange={(e) => setDestination(e.target.value)}*/}
                {/*        placeholder="Where are you going to?"*/}
                {/*        className="destination-input"*/}
                {/*    />*/}
                {/*</div>*/}

                <ParkingLotOptions parkingLots={filteredParkingLots} getOccupancyColor={getOccupancyColor} selectedLot={selectedLot} setSelectedLot={setSelectedLot} />
            </div>
            {/* More content that should scroll over the map */}
        </div>
    );
};

export default Home;