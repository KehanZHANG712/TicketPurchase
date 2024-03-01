import React, { useState } from 'react';
import './Register.css';
import { Link } from 'react-router-dom'; 
function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [role, setRole] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        let roleValue; // 定义一个变量来存储role的转换值
        switch (role) {
            case 'user':
                roleValue = 1;
                break;
            case 'admin':
                roleValue = 2;
                break;
            case 'customer':
                roleValue = 3;
                break;
            default:
                alert('Please select a valid role.');
                return;
        }

        // 使用fetch向后端发送请求
        fetch('https://swen90007-wz2.onrender.com/wz2.1/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                user_name: username,
                user_email: email,
                user_password: password,
                user_power: roleValue
            }),
            credentials: 'include'
        })
            //.then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 注册成功处理，例如导航到登录页面或显示一个成功消息
                    console.log('success');
                } else {
                    // 如果后端返回错误消息，则显示它
                    alert('register success');
                    window.location.href = '/';
                }
            })
            .catch(error => {
                // 错误处理
                console.error('Registration request failed:', error);
                alert('An error occurred. Please try again.');
            });
        // Submit the form data here (e.g., make an API call)
        console.log({
            username,
            email,
            password,
            role
        });
    };

    return (

        
        <div className="register-container">
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>*Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>*Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>*Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>*Confirm Password</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>*Role</label>
                    <select
                        value={role}
                        onChange={(e) => setRole(e.target.value)}
                        required
                    >
                        <option value="">--Select a role--</option>
                        <option value="user">User</option>
                        <option value="admin">Administrator</option>
                        <option value="customer">Customer</option>
                    </select>
                </div>

                <button type="submit">Register</button>
            </form>
            <div></div>
            <div className="back-button-container">
                <Link to="/">
                    <button>Back to Login</button>
                </Link>
            </div>
        </div>
    );
}

export default Register;
