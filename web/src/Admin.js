import React, { useState, useEffect, useContext } from 'react';
import './Admin.css';
import { Link } from 'react-router-dom'; 
import UserContext from './UserContext';

function Admin() {
  const { setCurrentUser, currentUser } = useContext(UserContext);
  const [concerts, setConcerts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const ITEMS_PER_PAGE = 4;

  const fetchConcerts = () => {
    fetch('https://swen90007-wz2.onrender.com/wz2.1/getAllEvent', { method: 'POST' })
      .then(response => response.json())
      .then(data => setConcerts(data))
      .catch(error => console.error('Error fetching events:', error));
  };

  useEffect(() => {
    fetchConcerts();
  }, []);

  const handleApprove = (id) => {
    fetch(`https://swen90007-wz2.onrender.com/wz2.1/approveevent`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ event_id: id }),
        credentials: 'include'
    })
    .then(response => {
        if(!response.ok) {
            throw new Error('Failed to approve event');
        }
        alert('Approve successfully');
        fetchConcerts();  // 重新获取数据
    })
    .catch(error => {
        console.error('Error approving event:', error);
        // 这里可以设置一些错误处理或者提示用户出错了
    });
  };


  const handleReject = (id) => {
    fetch(`https://swen90007-wz2.onrender.com/wz2.1/deleteevent`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json'
      },
      body: JSON.stringify({ event_id: id }),
      credentials: 'include' 
  })
  .then(response => {
      if(!response.ok) {
          throw new Error('Failed to delete event');
      }
      alert('Delete successfully');
      fetchConcerts();  // 重新获取数据
  })
  .catch(error => {
      console.error('Error deleting event:', error);
  });
};

  const totalPage = Math.ceil(concerts.length / ITEMS_PER_PAGE);

  return (
    <div className="admin-container">
      <div className="welcome-message">Welcome: {currentUser}</div>
      <nav className="admin-navbar">
        <Link to="/ManageUser">Manage User</Link>
        <span>           --              </span>
        <Link to="/ManageVenue">Manage Venue</Link>
      </nav>

      <h1>Admin Dashboard</h1>
      <div className="concert-list">
        {concerts.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE).map((concert) => (
          <div key={concert.event_id} className="concert-card">
            <h3>{concert.event_name}</h3>
            <p>StartTime: {concert.event_start_time}</p>
            <p>EndTime: {concert.event_end_time}</p>
            <p>Location: {concert.event_venue}</p>
            {concert.event_permission !== 1 && ( // 这里添加条件判断
      <div className="approval-buttons">
        <button onClick={() => handleApprove(concert.event_id)}>Approve</button>
        <button onClick={() => handleReject(concert.event_id)}>Reject</button>
      </div>
    )}
            <p>Status: {concert.event_permission === 1 ? 'Approved' : 'Pending'}</p>
          </div>
        ))}
      </div>

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

export default Admin;
