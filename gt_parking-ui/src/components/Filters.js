// src/components/Filters.js

import React from 'react';
import './Filters.css';

const Filters = ({ permits, selectedPermits, togglePermit }) => {
    return (
        <div className="filters-container">
            <div className="filters-title-bar">
                <h3 className="filters-title">Permits</h3>
                <hr className="filters-divider" />
            </div>
            <ul className="filters-list">
                {permits.map((permit) => (
                    <li key={permit}>
                        <label className="filter-item">
                            <input
                                type="checkbox"
                                checked={selectedPermits.includes(permit)}
                                onChange={() => togglePermit(permit)}
                            />
                            {permit}
                        </label>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Filters;
