/*LoginPage*/

/*import React, { useState } from 'react';*/
import {useNavigate} from 'react-router-dom'; // useNavigate 훅 임포트
import {Label} from "./ui/label"
import {Input} from "./ui/input"
import {Button} from "./ui/button"
import axios from 'axios';
import React, {useEffect, useState} from 'react';

function Signup() {

    const navigate = useNavigate();
    const [username, setusername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordCheck, setPasswordCheck] = useState('');
    const [email, setEmail] = useState('');
    const [Nick, setNick] = useState('');
    const [phone, setPhone] = useState('');
    const [passwordValid, setPasswordValid] = useState(false);

    useEffect(() => {
        const passwordRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])(?=.{8,20}$)/;
        setPasswordValid(passwordRegex.test(password));
    }, [password]);

    const validateForm = () => {
        // 필수 입력 값 확인
        if (!username || !password || !passwordCheck || !email || !phone) {
            alert('모든 필드를 입력해주세요.');
            return false;
        }
        // 비밀번호 일치 확인
        if (password !== passwordCheck) {
            alert('비밀번호가 일치하지 않습니다.');
            return false;
        }
        const passwordRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])(?=.{8,20})/;
        if (!passwordRegex.test(password)) {
            alert('비밀번호는 8자 이상 20자 미만이며, 소문자, 숫자, 특수기호를 각각 하나 이상 포함해야 합니다.');
            return false;
        }
        // 이메일 형식 검증
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert('유효하지 않은 이메일 형식입니다.');
            return false;
        }
        // 전화번호 형식 검증 (예시: 단순 숫자와 하이픈만 허용)
        const phoneRegex = /^[0-9\-]+$/;
        if (!phoneRegex.test(phone)) {
            alert('유효하지 않은 전화번호 형식입니다.');
            return false;
        }
        return true;
    };

    const checkIdDuplicate = async () => {
        try {
            //const response = await axios.post('http://localhost:8000/api/users/checkId', {
            const response = await axios.post('/api/users/checkId', {
                username: username,
                nickname: Nick
            });
            if (response.data.isDuplicate) {
                alert(response.data.message);
                return true; // 중복된 ID가 있음
            }
            return false; // 중복된 ID가 없음
        } catch (error) {
            if (error.response && error.response.data.isDuplicate) {
                alert(error.response.data.message);
                console.log(error);
                return true;
            } else {
                alert('다시 시도해주세요.');
                return true;
            }
        }
    };

    const handleSignup = async () => {
        if (!validateForm()) return; // 폼 검증 실패 시 중단
        const isDuplicate = await checkIdDuplicate(); // ID 중복 검사
        if (isDuplicate) return; // ID가 중복되었으면 여기서 처리 중단
        // 비밀번호 확인 로직 추가
        try {
            await axios.post('http://localhost:8000/api/users/Signup', {
                //await axios.post('http://13.125.161.122:8080/api/Signup', {
                username: username,
                password: password,
                nickname: Nick,
                email: email,
                phone: phone,
            });
            alert('회원가입 성공하셨습니다!');
            navigate('/'); // 회원가입 성공 후 로그인 페이지로 이동
        } catch (error) {
            console.error('다시 시도해주세요', error);
        }
    };

    return (
        <div className="flex flex-col gap-6 p-4 sm:p-6 items-center justify-center min-h-screen">
            <div className="absolute top-10 left-24">
                <Button onClick={() => navigate(-1)}>뒤로가기</Button>
            </div>
            <div className="w-full max-w-[400px]">
                <div className="text-center py-6">
                    <h1 className="text-3xl font-bold">회원가입</h1>
                    <p className="text-gray-500 dark:text-gray-400"></p>
                </div>
                <div className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="username">아이디</Label>
                        <Input id="username" placeholder="아이디를 입력해주세요." value={username}
                               onChange={(e) => setusername(e.target.value)}/>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="Nick">닉네임</Label>
                        <Input id="Nick" placeholder="닉네임을 입력해주세요." value={Nick}
                               onChange={(e) => setNick(e.target.value)}/>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="password">비밀번호</Label>
                        <Input id="password" type="password" placeholder="비밀번호를 입력해주세요." value={password}
                               onChange={(e) => setPassword(e.target.value)}/>
                        <i className={`password-icon ${passwordValid ? 'text-green-500' : 'text-red-500'} text-sm mt-3`}>
                            비밀번호는 8자 이상 20자 미만이며, 소문자, 숫자, 특수기호를 각각 하나 이상 포함해야 합니다.
                        </i>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="passwordck">비밀번호 확인</Label>
                        <Input id="passwordck" type="password" placeholder="비밀번호를 확인해주세요." value={passwordCheck}
                               onChange={(e) => setPasswordCheck(e.target.value)}/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="phone">휴대폰 번호</Label>
                        <Input id="phone" value={phone} placeholder="'-'을 빼고 입력해주세요."
                               onChange={(e) => setPhone(e.target.value)}/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="email">이메일</Label>
                        <Input id="email" type="email" placeholder="이메일을 입력해주세요." value={email}
                               onChange={(e) => setEmail(e.target.value)}/>
                    </div>

                    <Button className="w-full" onClick={handleSignup}>회원가입</Button>
                </div>
            </div>
        </div>
    );
}

export default Signup;