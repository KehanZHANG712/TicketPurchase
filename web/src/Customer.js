import React, { useState, useContext, useEffect } from 'react';
import './Customer.css';
import { Link } from 'react-router-dom';
import { TextField, Button, Card, CardContent, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
import UserContext from './UserContext';

function Customer() {
    const { setCurrentUser, currentUser } = useContext(UserContext);
    const [concert, setConcert] = useState({
        name: '',
        date: new Date(),
        startTime: '',
        endTime: '',
        venue: '',
        moshPrice: '',
        standingPrice: '',
        seatedPrice: '',
        vipPrice: '',
    });

    const [modalOpen, setModalOpen] = useState(false);
    const [venues, setVenues] = useState([]);

    useEffect(() => {
        fetch('https://swen90007-wz2.onrender.com/wz2.1/getallvenuesname')
            .then(response => response.json())
            .then(data => setVenues(data))
            .catch(error => console.error('Error fetching venues:', error));
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setConcert(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        let formattedDate = concert.date.toISOString().split('T')[0];  
        let startDateTime = `${formattedDate}T${concert.startTime}:00`;
        let endDateTime = `${formattedDate}T${concert.endTime}:00`;

        fetch('https://swen90007-wz2.onrender.com/wz2.1/createEvent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                event_artist_name: currentUser,
                event_name: concert.name,
                event_start_time: startDateTime,
                event_end_time: endDateTime,
                event_venue: concert.venue,
                event_permission: 0,
                moshPrice: concert.moshPrice,
                standingPrice: concert.standingPrice,
                seatedPrice: concert.seatedPrice,
                vipPrice: concert.vipPrice
            }),
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => Promise.reject(text));
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
        })
        .catch(error => {
            console.error('Error:', error);
        });

        setModalOpen(true);
    };

    return (
        <div className="user-container">
            <div className="welcome-message">Welcome: {currentUser}</div>
            <div>
            <nav className="user-nav">
                <Link to={`/EventRecord`}>My Events</Link>
                <Link to="/">Logout</Link>
            </nav>
            </div>
            <Card style={{ backgroundColor: 'white', padding: '20px', margin: '20px', height: '600px' }}>
                <CardContent>
                    <div className="user-body">
                        <h2>Create a Concert</h2>
                        <TextField 
                            label="Concert Name" 
                            variant="outlined"
                            name="name"
                            value={concert.name}
                            onChange={handleInputChange}
                        />
                        <LocalizationProvider dateAdapter={AdapterDateFns}>
                            <DatePicker
                                disableToolbar
                                variant="inline"
                                format="MM/dd/yyyy"
                                margin="normal"
                                label="Date"
                                value={concert.date}
                                onChange={date => setConcert(prev => ({ ...prev, date }))}
                                renderInput={({ inputRef, inputProps, ...other }) => (
                                    <TextField
                                        inputRef={inputRef}
                                        {...inputProps}
                                        {...other}
                                    />
                                )}
                            />
                        </LocalizationProvider>
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '10px' }}>
                            <TextField 
                                label="Start Time" 
                                variant="outlined"
                                type="time"
                                name="startTime"
                                value={concert.startTime}
                                onChange={handleInputChange}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                inputProps={{
                                    step: 300,  // 5 min
                                }}
                            />
                            <span>～</span>
                            <TextField 
                                label="End Time" 
                                variant="outlined"
                                type="time"
                                name="endTime"
                                value={concert.endTime}
                                onChange={handleInputChange}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                inputProps={{
                                    step: 300,  // 5 min
                                }}
                            />
                        </div>
                        <FormControl fullWidth variant="outlined" style={{ marginTop: '20px' }}>
                            <InputLabel id="venue-label">Event Venue</InputLabel>
                            <Select
                                labelId="venue-label"
                                label="Event Venue"
                                name="venue"
                                value={concert.venue}
                                onChange={handleInputChange}
                            >
                                {venues.map(venue => (
                                    <MenuItem key={venue} value={venue}>{venue}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        {concert.venue && ['mosh', 'standing', 'seated', 'vip'].map(area => (
                            <TextField 
                                key={area}
                                label={`${area} Area Price`} 
                                variant="outlined"
                                name={`${area}Price`}
                                value={concert[`${area}Price`]}
                                onChange={handleInputChange}
                                style={{ marginTop: '10px' }}
                            />
                        ))}

                        <Button variant="contained" color="primary" onClick={handleSubmit} style={{ marginTop: '20px' }}>
                            Submit
                        </Button>
                    </div>
                </CardContent>
            </Card>
            {modalOpen && <ConcertModal concert={concert} onClose={() => setModalOpen(false)} />}
        </div>
    );
}

function ConcertModal({ concert, onClose }) {
    const handleOverlayClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
      <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal-content">
          <h3>{concert.name}</h3>
          <p>Date: {concert.date.toLocaleDateString()}</p>
          <p>Start Time: {concert.startTime}</p>
          <p>End Time: {concert.endTime}</p>
          <p>Venue: {concert.venue}</p>
          {['mosh', 'standing', 'seated', 'vip'].map(area => (
              <p key={area}>{`${area.charAt(0).toUpperCase() + area.slice(1)} Price`}: ${concert[`${area}Price`]}</p>
          ))}
  
          <button onClick={onClose}>Close</button>
      </div>
  </div>
  
    );
}

export default Customer;
