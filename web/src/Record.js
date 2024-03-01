import React, { useState, useContext, useEffect } from 'react';
import { Link } from 'react-router-dom';
import UserContext from './UserContext';

function Record() {
  const [reservations, setReservations] = useState([]);
  const { setCurrentUser, currentUser } = useContext(UserContext);

  useEffect(() => {
    if (currentUser) {
      fetch('https://swen90007-wz2.onrender.com/wz2.1/getSelectReservation', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId: currentUser.toString()}),
        credentials: 'include'
      })
      .then(response => response.json())
      .then(data => {
        setReservations(data);
      })
      .catch(error => {
        console.error('Error fetching reservations:', error);
      });
    }
  }, [currentUser]);

  const handleCancel = (reservationId) => {
    fetch('https://swen90007-wz2.onrender.com/wz2.1/cancelReservation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ reservationId }),
      credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
      // You can update the state or navigate somewhere else after the cancellation
      setReservations(prevReservations => prevReservations.filter(res => res.reservationId !== reservationId));
    })
    .catch(error => {
      console.error('Error canceling the reservation:', error);
    });
  };

  const cardStyle = {
    backgroundColor: 'white',
    padding: '20px',
    margin: '10px 0',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    borderRadius: '10px'
  };

  return (
    <div className="record-container">
      <h1 style={{ color: 'white' }}>Purchase Record</h1>
      <ul>
        {reservations.map(reservation => (
          <li key={reservation.reservationId} style={cardStyle}>
            <h2>Reservation ID: {reservation.reservationId}</h2>
            <p>Price: ${reservation.reservationPrice}</p>
            <p>Number: {reservation.reservationNumber}</p>
            <p>User ID: {reservation.reservationUserId}</p>
            <p>Ticket ID: {reservation.reservationTicketId}</p>
            <button onClick={() => handleCancel(reservation.reservationId)}>Cancel</button>
          </li>
        ))}
      </ul>
      <Link to="/User">Return Home</Link>
    </div>
  );
}

export default Record;
