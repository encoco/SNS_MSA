import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter} from 'react-router-dom';
import {AuthProvider} from './contexts/AuthContext'; // AuthProvider 임포트

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
        <AuthProvider> {/* AuthProvider로 모든 컴포넌트를 감싸기 */}
            <App/>
        </AuthProvider>
    </BrowserRouter>
);

reportWebVitals();