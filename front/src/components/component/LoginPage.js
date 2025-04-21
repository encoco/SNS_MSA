import React, {useState, useEffect} from 'react';
import {useNavigate, Link} from 'react-router-dom'; // useNavigate 훅 임포트
import {Label} from "./ui/label"
import {Input} from "./ui/input"
import {Button} from "./ui/button"
import axios from 'axios';
import {webSocketService} from "services/WebSocketService";

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();


    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const tokensJson = queryParams.get('tokensJson');

        if (tokensJson) {
            // 문자열 형태의 JSON을 JavaScript 객체로 파싱
            const tokenObj = JSON.parse(tokensJson);
            localStorage.setItem('userInfo', tokenObj.accessToken);
            localStorage.setItem('nickname', JSON.stringify(tokenObj.nickname));
            navigate('/index');
        }
    }, []);

    const handleLogin = async () => {
        try {
            const params = new URLSearchParams();
            params.append('username', username);
            params.append('password', password);
            const response = await axios.post('http://localhost:8000/api/users/Login', params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                withCredentials: true
            });
            localStorage.setItem('userInfo', response.data.accessToken);
            localStorage.setItem('nickname', JSON.stringify(response.data.nickname));

            webSocketService.connect(response.data.accessToken, () => {
                console.log("WebSocket 연결 완료");
            });

            alert('로그인되었습니다.');
            navigate('/index');
        } catch (error) {
            if (error.response) {
                const status = error.response.status;
                if (status === 401 || status === 403) {
                    alert('계정 정보를 확인해주세요.');
                } else {
                    alert('다시 시도해주세요.');
                }
            } else if (error.request) {
                alert('서버와의 연결에 문제가 발생했습니다.');
            } else {
                alert('로그인 중 문제가 발생했습니다. 다시 시도해주세요.');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // 기본 제출 동작 방지
        await handleLogin(); // 로그인 처리
    };


    return (
        <div className="flex flex-col gap-6 p-4 sm:p-6 items-center justify-center min-h-screen">
            <div className="w-full max-w-[400px]">

                <div className="text-center py-6">
                    <h1 className="text-3xl font-bold">grooo</h1>
                    <p className="text-gray-500 dark:text-gray-400">환영합니다!</p>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="username">아이디</Label>
                            <Input id="username" placeholder="아이디를 입력해주세요." value={username}
                                   onChange={(e) => setUsername(e.target.value)}/>
                        </div>
                        <div className="space-y-2">
                            <Label htmlFor="password">비밀번호</Label>
                            <Input id="password" type="password" placeholder="비밀번호를 입력해주세요." value={password}
                                   onChange={(e) => setPassword(e.target.value)}/>
                        </div>
                        <div className="mt-4 space-y-2 text-center">
                        </div>
                        <div style={{textAlign: 'center'}}>
                            <Link className="text-sm underline" to="/Signup">회원가입</Link>
                        </div>
                        <Button type="submit" className="w-full mb-2" variant="outline" style={{
                            backgroundColor: 'black',
                            borderColor: 'black',
                            borderWidth: '2px',
                            color: 'white'
                        }}>로그인</Button>
                    </div>
                </form>
                <br/>

                <div className="space-y-4">
                    <Button className="w-full" style={{backgroundColor: '#03C75A'}} variant="outline"
                            onClick={() => window.location.href = 'http://www.grooo.kro.kr/oauth2/authorization/naver'}>
                        NAVER
                    </Button>
                    <Button className="w-full" variant="outline"
                            onClick={() => window.location.href = 'http://www.grooo.kro.kr/oauth2/authorization/google'}>
                        GOOGLE
                    </Button>
                    <Button className="w-full" style={{backgroundColor: '#FEE500'}} variant="outline"
                            onClick={() => window.location.href = 'http://www.grooo.kro.kr/oauth2/authorization/kakao'}>
                        KAKAO
                    </Button>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;