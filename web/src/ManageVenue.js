import React, { useState, useEffect, useContext } from 'react';
import UserContext from './UserContext';
import './ManageVenue.css';
import { Link } from 'react-router-dom';

function VenueModal({ isOpen, onClose, onSubmit }) {
    const [address, setAddress] = useState("");
    const [moshCapacity, setMoshCapacity] = useState("");
    const [standingCapacity, setStandingCapacity] = useState("");
    const [seatedCapacity, setSeatedCapacity] = useState("");
    const [vipCapacity, setVipCapacity] = useState("");
    const handleSubmit = () => {
        const venueCapacityNumber = parseInt(moshCapacity) + parseInt(standingCapacity) + parseInt(seatedCapacity) + parseInt(vipCapacity);
        const venueCapacityString = venueCapacityNumber.toString();
        onSubmit({
            venue_address:address,
            mosh_capacity:moshCapacity,
            standing_capacity: standingCapacity,
            seated_capacity: seatedCapacity,
            vip_capacity: vipCapacity,
            venue_capacity:venueCapacityString,
        });
    }; 
    return isOpen ? (
        <div className="modal">
            <div className="modal-content">
            <label>Hello, Admin</label>
                <Link onClick={handleSubmit}>Add Venue</Link>
                <input placeholder="Venue Address" value={address} onChange={(e) => setAddress(e.target.value)} required />
                <input placeholder="Mosh Capacity" type="number" value={moshCapacity} onChange={(e) => setMoshCapacity(e.target.value)} required />
                <input placeholder="Standing Capacity" type="number" value={standingCapacity} onChange={(e) => setStandingCapacity(e.target.value)} required />
                <input placeholder="Seated Capacity" type="number" value={seatedCapacity} onChange={(e) => setSeatedCapacity(e.target.value)} required />
                <input placeholder="VIP Capacity" type="number" value={vipCapacity} onChange={(e) => setVipCapacity(e.target.value)} required />
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    ) : null;
}

function ManageVenue() {
    const [venues, setVenues] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [tempVenue, setTempVenue] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const {currentUser} = useContext(UserContext);

    const fetchVenues = () => {
        fetch('https://swen90007-wz2.onrender.com/wz2.1/getAllVenue', { method: 'POST',credentials: 'include' })
            .then(response => response.json())
            .then(data => setVenues(data))
            .catch(error => console.error('Error fetching venues:', error));
    };

    useEffect(() => {
        fetchVenues();
    }, []);

    const handleAddVenueSubmit = (newVenue) => {
        fetch('https://swen90007-wz2.onrender.com/wz2.1/createVenue', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newVenue),
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            setIsModalOpen(false);
            fetchVenues();  // 重新获取数据
        })
        .catch(error => console.error('Error adding venue:', error));
    };


    return (
        <div>
            <div className="welcome-message">Welcome: {currentUser}</div>
            <div className="back-to-home">
                <Link to="/Admin">Go back to home</Link>
            </div>
            <VenueModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleAddVenueSubmit} />
            <button onClick={() => setIsModalOpen(true)}>Add Venue</button>
            <table>
                <thead>
                    <tr>
                        <th>Venue Address</th>
                        <th>Mosh Capacity</th>
                        <th>Standing Capacity</th>
                        <th>Seated Capacity</th>
                        <th>VIP Capacity</th>
                    </tr>
                </thead>
                <tbody>
                    {venues.map(venue => (
                        <tr key={venue.venueId}>
                            <td>{venue.venueAddress}</td>
                            <td>{venue.moshCapacity}</td>
                            <td>{venue.standingCapacity}</td>
                            <td>{venue.seatedCapacity}</td>
                            <td>{venue.vipCapacity}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default ManageVenue;
