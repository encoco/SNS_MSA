import axios from 'axios'

let isRefreshing = false;
let refreshSubscribers = [];

function onRefreshed(newToken) {
    refreshSubscribers.forEach(callback => callback(newToken));
    refreshSubscribers = [];
}

function addRefreshSubscriber(callback) {
    refreshSubscribers.push(callback);
}

const api = axios.create({
    //baseURL: 'http://localhost:8000/api', Default

    //Docker 설정
    baseURL: '/api',
    withCredentials: true,
    // headers: {
    //     "Content-Type": "application/json"
    // }
});

api.interceptors.request.use(config => {   //맨처음 요청 보내는얘
    const token = localStorage.getItem('userInfo');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, error => Promise.reject(error));

api.interceptors.response.use( //맨처음 요청에서 오류나면 실행되는 얘
    response => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true; // 재시도 플래그를 설정하여 무한 반복 방지

            if (isRefreshing) {
                // 이미 리프레시 중이면 기다렸다가 토큰 받아서 요청 재시도
                return new Promise((resolve) => {
                    addRefreshSubscriber((token) => {
                        originalRequest.headers['Authorization'] = `Bearer ${token}`;
                        resolve(api(originalRequest));
                    });
                });
            }
            // 아직 리프레시 중이 아니면 → 리프레시 시작
            isRefreshing = true;

            try {
                const { data } = await axios.post('http://localhost:8000/api/auth/refresh', {}, { withCredentials: true });
                localStorage.setItem('userInfo', data);

                // 대기 중인 요청들에 새 토큰 전달
                onRefreshed(data);

                originalRequest.headers['Authorization'] = `Bearer ${data}`;
                return api(originalRequest);

            } catch (refreshError) {
                localStorage.removeItem('userInfo');
                alert("다시 로그인해주세요.");
                window.location.reload();
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);
export default api;