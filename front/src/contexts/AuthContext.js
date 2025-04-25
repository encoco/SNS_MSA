import React, {createContext, useContext, useState} from 'react';
import {useNavigate} from "react-router-dom";
import axios from 'axios';
import { webSocketService } from '../services/WebSocketService';

const AuthContext = createContext({
    login: () => {
    },
    logout: () => {
    },
});

// Custom hook to use the auth context
export function useAuth() {
    return useContext(AuthContext);
}

export const AuthProvider = ({children}) => {
    const [users, setUser] = useState(null);
    const navigate = useNavigate();
    const login = (userData) => {
        setUser(userData);
    };

    const logout = async () => {
        try {
            //const response  = await axios.get('http://localhost:8000/api/users/Logout', {
            const response  = await axios.get('/api/users/Logout', {
                withCredentials: true
            });
            // 여기서 응답 처리
            localStorage.removeItem('userInfo');
            localStorage.removeItem('nickname');
            webSocketService.disconnect();
            navigate('/');
        } catch (error) {
            console.error('로그아웃 오류', error);
        }
    };

    return (
        <AuthContext.Provider value={{users, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
};
