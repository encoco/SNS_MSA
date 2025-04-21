import React, {useState} from 'react';
import api from "../../api";
import Modal from 'react-modal';
import {useNavigate} from "react-router-dom";
import {useAuth} from '../../contexts/AuthContext'; // 경로는 실제 구조에 맞게 조정해야 함

function Setting({isOpen, onRequestClose, onClose}) {
    // State to manage the validity of the new password
    const [passwordValidity, setPasswordValidity] = useState({
        isValid: false,
        password: '',
    });
    const navigate = useNavigate();
    const [password, setPassword] = useState('');
    const [changepassword, setChangePassword] = useState('');
    const {logout} = useAuth();
    // State to manage if the confirmation password matches the new password
    const [confirmPassword, setConfirmPassword] = useState('');

    // Function to validate the password
    const validatePassword = (password) => {
        const regex = /^(?=.*[a-z])(?=.*[0-9])(?=.*[\W_]).{8,20}$/;
        setPasswordValidity({
            isValid: regex.test(password),
            password,
        });
    };

    // Function to check if the confirmed password matches the new password
    const handleConfirmPassword = (e) => {
        setConfirmPassword(e.target.value);
    };

    const handleChangePassword = async () => {
        if (!passwordMatch || !passwordValidity) {
            alert("비밀번호 조건을 확인해주세요");
            return;
        }

        try {
            const response = await api.post(`/users/checkPassword`, {password: password, changePassword: changepassword}, {
                withCredentials: true,
            });
            alert("변경되었습니다. 다시 로그인 해 주세요.");
            handleLogout();
        } catch (error) {
            alert("기존 비밀번호를 확인해주세요. ");
        }
    }

    const handleLogout = async () => {
        try {
            logout();
            navigate('/');
        } catch (error) {
            console.log(error);
            alert('다시 시도해주세요.');
        }
    };
    const handleleave = async () => {
        try {
            if (window.confirm("정말 탈퇴하시겠습니까?")) {
                const response = await api.post(`/users/DeleteUser`, {
                    withCredentials: true,
                });
                alert("이용해주셔서 감사합니다.");
                handleLogout();
            } else return;
        } catch (error) {
            alert("다시 시도해주세요.");
        }
    }
    const passwordMatch = confirmPassword === passwordValidity.password && passwordValidity.isValid;

    return (
        <Modal
            isOpen={isOpen}
            className="fixed inset-0 bg-opacity-50 flex justify-center items-center z-10"
            overlayClassName="fixed inset-0 bg-black bg-opacity-5"
            onRequestClose={() => {
                onRequestClose();
                onClose(); // onClose 함수 호출
            }}
            style={{zIndex: 12}}
        >
            <div className="w-[75vw] max-w-sm mx-auto relative bg-white p-5 rounded-lg">
                <h2 className="text-xl font-semibold mb-4">환경 설정</h2>
                <form>
                    <h3 className="text-lg font-semibold mb-2">비밀번호 변경</h3>
                    <label htmlFor="current-password" className="block mb-2">
                        현재 비밀번호
                        <input
                            type="password"
                            id="current-password"
                            name="current-password"
                            className="border border-gray-300 rounded-md p-2 mt-1 ml-4"
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>
                    <label htmlFor="new-password" className="block mb-2">
                        새 비밀번호
                        <input
                            type="password"
                            id="new-password"
                            name="new-password"
                            className="border border-gray-300 rounded-md p-2 mt-1 ml-4 md:ml-8"
                            style={{
                                borderColor: passwordValidity.isValid ? 'green' : 'red',
                                outlineColor: passwordValidity.isValid ? 'green' : 'red' // 추가된 부분
                            }}
                            onChange={(e) => {
                                validatePassword(e.target.value);
                                setChangePassword(e.target.value);
                            }}
                        /><br/>
                        <i className="text-sm mt-3">
                            *비밀번호는 8자 이상 20자 미만이며, 소문자, 숫자, 특수기호를 각각 하나 이상 포함해야 합니다.
                        </i>
                    </label>
                    <label htmlFor="confirm-password" className="block mb-2">
                        비밀번호 확인
                        <input
                            type="password"
                            id="confirm-password"
                            name="confirm-password"
                            className="border border-gray-300 rounded-md p-2 mt-1 ml-4"
                            style={{
                                borderColor: passwordMatch ? 'green' : 'red',
                                outlineColor: passwordMatch ? 'green' : 'red'
                            }}
                            onChange={handleConfirmPassword}
                        />
                    </label>
                    <button type="button" className="bg-black text-white py-2 px-4 rounded-md mt-4"
                            onClick={handleChangePassword}>비밀번호 변경
                    </button>
                </form>
                <div className="mt-6">
                    <h3 className="text-lg font-semibold mb-2">회원 탈퇴</h3>
                    <p>회원 탈퇴를 원하시면 아래 버튼을 클릭하세요.</p>
                    <button type="button" className="bg-black text-white py-2 px-4 rounded-md mt-4"
                            onClick={handleleave}>회원 탈퇴
                    </button>
                </div>
                <button onClick={onRequestClose}
                        className="absolute top-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
            </div>
        </Modal>
    );
}

export default Setting;
