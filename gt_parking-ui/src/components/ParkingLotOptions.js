// src/components/ParkingLotsList.js

import React, { useState } from 'react';
import './ParkingLotOptions.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faApple, faGoogle } from '@fortawesome/free-brands-svg-icons';

const ParkingLotOptions = ({ parkingLots, getOccupancyColor }) => {
    const [selectedLot, setSelectedLot] = useState(null);

    const handleSelectLot = (lot) => {
        setSelectedLot(lot);
    };

    const openInMaps = (lot, provider) => {
        const { coordinates } = lot;
        const [lat, lng] = coordinates;
        const appleMapsUrl = `http://maps.apple.com/?ll=${lat},${lng}`; // replace
        const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;

        const url = provider === 'google' ? googleMapsUrl : appleMapsUrl;
        window.open(url, '_blank');
    };

    return (
        <div className="parking-lots-options">
            <div className="parking-lot-title">Parking Lots</div>
            {parkingLots.map((lot) => (
                <div key={lot.id} className={`parking-lot ${getOccupancyColor(lot.occupancy)}`} onClick={() => handleSelectLot(lot)}>
                    <span className="occupancy-indicator"></span>
                    <span className="parking-lot-info">{lot.name} - {lot.occupancy}% <span className="occupancy-status">({lot.occupancy < 40 ? 'Low' : lot.occupancy < 70 ? 'Med' : 'High'})</span></span>

                    {/*{lot.name} - {lot.occupancy}% Occupancy*/}
                </div>
            ))}

            {selectedLot && (
                <div className="map-buttons">
                    <button onClick={() => openInMaps(selectedLot, 'apple')}>
                        <FontAwesomeIcon icon={faApple} /> Map
                    </button>
                    <button onClick={() => openInMaps(selectedLot, 'google')}>
                        <FontAwesomeIcon icon={faGoogle} /> Map
                    </button>
                </div>
            )}
        </div>
    );
};

export default ParkingLotOptions;
