import React, { useState, useEffect } from 'react';
import './ManageUser.css';
import { Link } from 'react-router-dom';
function ManageUser() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const deleteUser = async (user) => {
      const dataToSend = {
          ...user,
          password: user.hashPassword
      };
  
      const requestOptions = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(dataToSend),
          credentials: 'include'
      };
    
        try {
            const response = await fetch('https://swen90007-wz2.onrender.com/wz2.1/deleteUser', requestOptions);
            if (response.ok) {
                const data = await response.text();
                console.log(data);  // 输出成功或失败的消息
                // 从列表中移除该用户
                setUsers(prevUsers => prevUsers.filter(u => u.userId !== user.userId));
            } else {
                console.error('Failed to delete user:', await response.text());
            }
        } catch (error) {
            console.error('Error occurred while deleting user:', error);
        }
    };
    
    const handleDelete = (user) => {
        // 请求用户确认删除操作
        const isConfirmed = window.confirm(`Are you sure you want to delete ${user.userName}?`);
        if (isConfirmed) {
            deleteUser(user);
        }
    }
    
    useEffect(() => {
        setLoading(true);
        fetch('https://swen90007-wz2.onrender.com/wz2.1/getAllUser', { method: 'POST' ,credentials: 'include'})
          .then(response => {
            if (!response.ok) {
              throw new Error('Network response was not ok');
            }
            return response.json();
          })
          .then(data => {
              console.log(data);
              if (Array.isArray(data)) {
                  setUsers(data);
              }
          })
          .catch(error => {
            console.error("There was a problem fetching the user data:", error);
          })
          .finally(() => {
            setLoading(false);
          });
    }, []);
  return (
    
    <div className="manage-user-container">
           <div className="back-to-home">
        <Link to="/Admin">Go back to home</Link>
      </div>
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>UserPower</th>
            <th>Edit</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.userId}>  {/* 修改了 key 的值 */}
              <td>{user.userName}</td>
              <td>{user.email}</td>
              <td>{user.power}</td>  {/* 修改了 power 的大小写 */}
              <td>
    <button 
        className="delete-button" 
        onClick={() => handleDelete(user)}>
        Delete
    </button>
      </td>
            </tr>
          ))}
        </tbody>
      </table>
      {loading && <div>Loading users...</div>}
    </div>
  );
}

export default ManageUser;
