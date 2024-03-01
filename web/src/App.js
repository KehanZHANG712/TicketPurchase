import React, { useState,useContext } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes, Link,useNavigate } from 'react-router-dom';
import Register from './Register';
import User from './User';
import Admin from './Admin';
import Customer from './Customer';
import Record from './Record';
import UserContext from './UserContext';
import ManageUser from './ManageUser';
import ManageVenue from './ManageVenue';
import EventRecord from './EventRecord';
function LoginComponent() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const handleUsernameChange = (event) => {
        setUsername(event.target.value);
    };

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };
    const { setCurrentUser } = useContext(UserContext);
    const handleSubmit = (e) => {
        e.preventDefault();
    
        fetch('https://swen90007-wz2.onrender.com/wz2.1/login', {  // 注意这里更改了URL
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                user_name: username,
                user_password: password
            }),
            credentials: 'include'
        })
        .then(response => response.json())  // 解析响应为JSON
        .then(data => {
            if (data.success) {     
                setCurrentUser(data.user_name);
                switch(Number(data.user_power)) {
                    case 1:
                        setCurrentUser(data.user_id)
                        navigate('/User');
                        break;
                    case 2:
                        navigate('/Admin');
                        break;
                    case 3:
                        navigate('/customer');
                        break;
                    default:
                        alert('Unknown role.');
                        break;
                }
            } else {
                alert(data.message || 'Login failed.');
            }
        })
        .catch(err => {  // 添加一个catch来处理可能的错误
            console.error("Error during login:", err);
            alert('There was an error. Please try again.');
        });
    };

    return (
        <div className="login-container">
            <img src="https://th.bing.com/th/id/OIP.eLMgLPNv6CHCl0ms1d3V2AHaHa?w=149&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" alt="Logo" />
            <h2>Music Events System</h2>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={handleUsernameChange}
                        placeholder="Enter your username"
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={handlePasswordChange}
                        placeholder="Enter your password"
                    />
                </div>
                <div className="form-group">
                    <a href="#">Forget your password?</a>
                </div>
                <div className="button-container">                  
                <button type="submit" className="action-button">Login</button>
                 <Link to="/Register">
                 <button type="button" className="action-button">Register</button>
                 </Link>
                </div>
            </form>
        </div>
    );
}

function App() {
    const [currentUser, setCurrentUser] = useState(null);
    return (
        <UserContext.Provider value={{ currentUser, setCurrentUser }}>
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/" element={<LoginComponent />} />
                    <Route path="/Register" element={<Register />} />
                    <Route path="/User" element={<User />} />
                    <Route path="/Admin" element={<Admin />} />
                    <Route path="/Customer" element={<Customer />} />
                    <Route path="/Record" element={<Record/>} />
                    <Route path="/ManageUser" element={<ManageUser/>} />
                    <Route path="/ManageVenue" element={<ManageVenue/>} />
                    <Route path="/EventRecord" element={<EventRecord/>} />
                </Routes>
            </div>
        </Router>
        </UserContext.Provider>
    );
}

export default App;
