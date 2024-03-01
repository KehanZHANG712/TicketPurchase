import React, { useState, useEffect, useContext } from 'react';
import './Admin.css';  // 假设这个CSS适用于你的EventRecord组件
import { Link } from 'react-router-dom';
import UserContext from './UserContext';

function EventRecord() {
    const { currentUser } = useContext(UserContext);
    const [events, setEvents] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const ITEMS_PER_PAGE = 2;
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedEventName, setSelectedEventName] = useState('');
    const [artistName, setArtistName] = useState('');
    const openModal = (eventName) => {
        setSelectedEventName(eventName);
        setIsModalOpen(true);
    };
    const handleModalSubmit = () => {
        fetch('https://swen90007-wz2.onrender.com/wz2.1/addplanner', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                event_name: selectedEventName,
                event_planner_name: artistName
            }),
            credentials: 'include'
        })
        .then(response => {
            if (response.ok) {
                console.log('Artist added successfully.');
                setIsModalOpen(false);  // Close the modal after success
            } else {
                console.error('Failed to add artist.');
            }
        })
        .catch(error => console.error('Error:', error));
    };
    const [editEvent, setEditEvent] = useState(null); // 当前正在编辑的事件

    const handleEditEvent = (event) => {
        setEditEvent(event);
    };

    const handleEditSubmit = () => {
        const payload = {
            event_id: editEvent.event_id,
            event_name: editEvent.event_name,
            event_venue: editEvent.event_venue,
            event_start_time: editEvent.event_start_time + ':00', // 默认添加 ":00"
            event_end_time: editEvent.event_end_time + ':00', 
            mosh_price: parseInt(editEvent.mosh_price, 10), // 转换为整数
            standing_price: parseInt(editEvent.standing_price, 10), // 转换为整数
            seated_price: parseInt(editEvent.seated_price, 10), // 转换为整数
            vip_price: parseInt(editEvent.vip_price, 10) 
        };
    console.log(payload);
        fetch('https://swen90007-wz2.onrender.com/wz2.1/editevent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload),
            credentials: 'include'
        })
        .then(response => {
            if (response.ok) {
                console.log('Event updated successfully.');
                setEditEvent(null);  // 关闭编辑模态窗口
                // 你可以选择在这里再次调用事件获取方法来刷新事件列表，以确保显示的数据是最新的
            } else {
                console.error('Failed to update event.');
            }
        })
        .catch(error => console.error('Error:', error));
    };
    
    useEffect(() => {
        fetch(`https://swen90007-wz2.onrender.com/wz2.1/eventsbyplanner`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ event_planner_name: currentUser }),
            credentials: 'include'  // 传递当前用户到后端
        })
        .then(response => response.json())
        .then(data => setEvents(data))
        .catch(error => console.error('Error fetching events:', error));
    }, [currentUser]);

    const totalPage = Math.ceil(events.length / ITEMS_PER_PAGE);

    return (
        <div className="admin-container">
            <div className="welcome-message">My Events: {currentUser}</div>
            <h1>Event Dashboard</h1>
            <div className="event-list">
                {events.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE).map((event) => (
                    <div key={event.event_id} className="event-card">
                        <Link onClick={() => openModal(event.event_name)}>{event.event_name}</Link>
                        <p>Artist: {event.event_artist_name}</p>
                        <p>StartTime: {event.event_start_time}</p>
                        <p>EndTime: {event.event_end_time}</p>
                        <p>Venue: {event.event_venue}</p>
                        <p>Mosh Price: ${event.mosh_price}</p>
                        <p>Standing Price: ${event.standing_price}</p>
                        <p>Seated Price: ${event.seated_price}</p>
                        <p>VIP Price: ${event.vip_price}</p>
                        <button onClick={() => handleEditEvent(event)}>Edit</button>
                    </div>
                    
                ))}
            </div>
            {editEvent && (
                <div className="modal-content">
    <label>
        Start Time:
        <input type="datetime-local" value={editEvent.event_start_time} onChange={(e) => setEditEvent({...editEvent, event_start_time: e.target.value})} />
    </label>
    <label>
        End Time:
        <input type="datetime-local" value={editEvent.event_end_time} onChange={(e) => setEditEvent({...editEvent, event_end_time: e.target.value})} />
    </label>
    <label>
        Mosh Price:
        <input type="number" value={editEvent.mosh_price} onChange={(e) => setEditEvent({...editEvent, mosh_price: e.target.value})} />
    </label>
    <label>
        Standing Price:
        <input type="number" value={editEvent.standing_price} onChange={(e) => setEditEvent({...editEvent, standing_price: e.target.value})} />
    </label>
    <label>
        Seated Price:
        <input type="number" value={editEvent.seated_price} onChange={(e) => setEditEvent({...editEvent, seated_price: e.target.value})} />
    </label>
    <label>
        VIP Price:
        <input type="number" value={editEvent.vip_price} onChange={(e) => setEditEvent({...editEvent, vip_price: e.target.value})} />
    </label>
    <button onClick={handleEditSubmit}>Save Changes</button>
</div>

        )}

            {isModalOpen && (
    <div className="modal">
        <div className="modal-content">
            <div className="input-group">
                <label onClick={handleModalSubmit}>
                    Add Artist Name:
                    <input type="text" value={artistName} onChange={(e) => setArtistName(e.target.value)} />
                </label>
            </div>
            <button onClick={() => setIsModalOpen(false)}>Cancel</button>
        </div>
    </div>
)}
            <div className="pagination">
                <button 
                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))} 
                    disabled={currentPage === 1}>
                    Previous
                </button>
                <span>{currentPage} / {totalPage}</span>
                <button 
                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPage))} 
                    disabled={currentPage === totalPage}>
                    Next
                </button>
            </div>
        </div>
    );
}

export default EventRecord;
