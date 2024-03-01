import React, { useState, useEffect,useContext } from 'react';
import './User.css';
import { Link, useNavigate } from 'react-router-dom';
import UserContext from './UserContext';
import { DatePicker } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
function User() {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(null);
  const { setCurrentUser, currentUser } = useContext(UserContext);
  const [currentPage, setCurrentPage] = useState(1);
  const [concerts, setConcerts] = useState([]);
  const concertsPerPage = 4;
  const [search_name, setSearchTerm] = useState('');
  const handleDateSearch = () => {
    
    fetch('https://swen90007-wz2.onrender.com/wz2.1/findEventsByDate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ search_date: selectedDate.toISOString().split('T')[0] }),
      credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
      const concertsWithTickets = data.map(concert => ({ ...concert, tickets: [] }));
      setConcerts(concertsWithTickets);
      setCurrentPage(1); 
    })
    .catch(error => console.error('Error fetching search results by date:', error));
  }
  
  const handleSearch = () => {
    fetch('https://swen90007-wz2.onrender.com/wz2.1/searchEvent', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ search_name}),
      credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
      const concertsWithTickets = data.map(concert => ({ ...concert, tickets: [] }));
      setConcerts(concertsWithTickets);
      setCurrentPage(1); 
    })
    .catch(error => console.error('Error fetching search results:', error));
  }
  const handleBuy = (ticketId) => {
    // Find the ticket that matches the given ticketId
    const selectedTicket = concerts.flatMap(concert => concert.tickets).find(ticket => ticket.ticketId === ticketId);
    if (!selectedTicket) {
      console.error('Ticket not found!');
      return;
    }
  
    const quantity = ticketQuantities[ticketId];
    const totalAmount = quantity * selectedTicket.ticketPrice;
  
    // Prepare the data to send to the backend
    const reservationData = {
      reservation_user_id: currentUser.toString(),
      ticket_event: concerts.find(concert => concert.tickets.includes(selectedTicket)).event_name,
      ticket_type: selectedTicket.ticketType,
      reservation_number: quantity.toString(),
      reservation_price: totalAmount.toString()
    };
  console.log(reservationData);
    // Send the data to the backend
    fetch('https://swen90007-wz2.onrender.com/wz2.1/addReservation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(reservationData),
      credentials: 'include'
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // Handle the response from the backend. For example, you can show a success message or navigate the user to another page.
      console.log('Reservation added successfully:', data);
    })
    .catch(error => {
      console.error('There was a problem with the fetch operation:', error.message);
    });
  };
  
  useEffect(() => {
    fetch('https://swen90007-wz2.onrender.com/wz2.1/getAllEvent1', { method: 'POST' })
      .then(response => response.json())
      .then(data => {
        // Append an empty array for tickets to each concert
        const concertsWithTickets = data.map(concert => ({ ...concert, tickets: [] }));
        setConcerts(concertsWithTickets);
      })
      .catch(error => console.error('Error fetching venues:', error));
  }, []);

  const fetchSelectedTickets = (eventName, eventId) => {
    fetch('https://swen90007-wz2.onrender.com/wz2.1/getSelectTicketByEvent', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ event_name: eventName }),
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        setConcerts(prevConcerts => {
            return prevConcerts.map(concert => {
                if (concert.event_id === eventId) {
                    return { ...concert, tickets: data };
                }
                console.log(data);
                return concert;
            });
        });
    })
    .catch(error => console.error('Error fetching tickets:', error));
  }

  const lastConcertIndex = currentPage * concertsPerPage;
  const firstConcertIndex = lastConcertIndex - concertsPerPage;
  const currentConcerts = concerts.slice(firstConcertIndex, lastConcertIndex);
  const [ticketQuantities, setTicketQuantities] = useState({});

  return (
    <div className="user-container">
      <nav className="user-nav">
      <div className="welcome-message">Welcome, userID: {currentUser}</div>
        <div className="nav-title">Music Event</div>
        <div className="nav-buttons">
        <LocalizationProvider dateAdapter={AdapterDateFns}>
                <DatePicker selected={selectedDate} onChange={date => setSelectedDate(date)} dateFormat="yyyy-MM-dd" />
                <button onClick={handleDateSearch}>🔍 Search By Date</button>
            </LocalizationProvider>
        <input
            type="text"
            placeholder="Search event..."
            value={search_name}
            onChange={e => setSearchTerm(e.target.value)}
          />
          <button onClick={handleSearch}>🔍 Search</button>
          <Link to="/Record">
  <button className="btn-icon">🛒My History</button>
          </Link>       
          <Link to="/">Logout</Link>
        </div>
      </nav>

      <div className="concert-column">
        {currentConcerts.map(concert => (
          <div key={concert.event_id} className="concert-row">
            <div className="concert-details">
              <Link onClick={() => fetchSelectedTickets(concert.event_name, concert.event_id)}>
                {concert.event_name}
              </Link>
              <p>Date: {concert.event_start_time} - {concert.event_end_time}</p>
              <p>Venue: {concert.event_venue}</p>
            </div>

            {concert.tickets && concert.tickets.length > 0 && (
              <table className="tickets-table">
                <thead>
                  <tr>
                    <th>Ticket Type</th>
                    <th>Ticket Price</th>
                    <th>Rest</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {concert.tickets.map(ticket => (
                    <tr key={ticket.ticketId}>
                      <td>{ticket.ticketType}</td>
                      <td>{ticket.ticketPrice}</td>
                      <td>{ticket.ticketRest}</td>
                      <td> 
            <input 
                type="number" 
                min="1" 
                max={ticket.ticketRest} 
                value={ticketQuantities[ticket.ticketId] || ''} 
                onChange={e => setTicketQuantities({...ticketQuantities, [ticket.ticketId]: e.target.value})} 
            />
            <button onClick={() => handleBuy(ticket.ticketId)}>
                Buy
            </button>
        </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        ))}

        <div className="pagination-controls">
          <button onClick={() => setCurrentPage(prevPage => Math.max(1, prevPage - 1))}>Prev</button>
          <span>Page {currentPage}</span>
          <button onClick={() => setCurrentPage(prevPage => Math.min(concerts.length / concertsPerPage, prevPage + 1))}>Next</button>
        </div>
      </div>
    </div>
  );
}

export default User;
