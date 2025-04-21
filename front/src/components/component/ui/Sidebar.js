import {Avatar, AvatarImage} from "./avatar"
import {Link, useNavigate} from "react-router-dom";
import {useAuth} from '../../../contexts/AuthContext'; // 경로는 실제 구조에 맞게 조정해야 함
import React, {useEffect, useState} from 'react';
import {BrowserView, MobileView} from "react-device-detect";
import BoardWrite from '../BoardWrite';
import Alarm from "./alarm.js";
import Setting from '../Setting.js'; // setting.js 모달 import
import api from "../../../api";


// Define the Sidebar component
function Sidebar() {
    const navigate = useNavigate();
    const {logout} = useAuth();
    const [nickname, setNickname] = useState(''); // 초기 상태를 빈 문자열로 설정
    const [img, setImg] = useState('');
    const [id, setId] = useState('');
    const[follower, setFollower] = useState(0);
    const[following, setFollowing] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [alarmModal, setAlarmModal] = useState(false);
    const [settingModal, setSettingModal] = useState(false);
    const [newAlarm, setNewAlarm] = useState(false);

    useEffect(() => {
        const userInfoJSON = localStorage.getItem('nickname');
        if (userInfoJSON) {
            const userInfo = JSON.parse(userInfoJSON);
            setNickname(userInfo.nickname); // nickname 상태 업데이트
            setImg(userInfo.img);
            setId(userInfo.id);
        }
    }, []);

    useEffect(() => {
        const Alarm = async () => {
            try {
                // userId를 사용하여 사용자의 게시물을 가져옴
                const response = await api.get(`/alarms/CheckNewAlarm`, {
                    withCredentials: true,
                });
                setNewAlarm(response.data);
            } catch (error) {
                console.log(error);
            }
        };
        Alarm();
    }, []);

    useEffect(() => {
        const fetchFollowCounts = async () => {
            if (!id) return;
            try {
                const response = await api.get(`/users/follow/count/${id}`, {
                    withCredentials: true,
                });
                const { followerCount, followingCount } = response.data;
                setFollower(followerCount);
                setFollowing(followingCount);
            } catch (error) {
                console.error("팔로우 수 가져오기 실패", error);
            }
        };

        fetchFollowCounts();
    }, [id]);
    const handleLogout = async () => {
        try {
            logout();
            navigate('/');
        } catch (error) {
            console.log(error);
            alert('다시 시도해주세요.');
        }
    };

    const openAlarmModal = () => setAlarmModal(true);
    const closeAlarmModal = () => setAlarmModal(false);

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    const openSettingModal = () => setSettingModal(true); // 환경 설정 모달 열기
    const closeSettingModal = () => setSettingModal(false); // 환경 설정 모달 닫기

    return (
        <>
            <BrowserView>
                <div className="w-70 h-full bg-white p-5">
                    <div className="flex flex-col items-center">
                        <Avatar>
                            <div className="w-25 h-25 rounded-full overflow-hidden">
                                <AvatarImage alt="User Avatar" className="w-full h-full object-cover"
                                             src={img || "/placeholder.svg?height=100&width=100"}/>
                            </div>
                        </Avatar>
                        <h2 className="mt-4 font-semibold text-xl">{nickname}</h2>
                        <p className="text-sm text-gray-600">팔로워: {follower} · 팔로잉: {following}</p>
                    </div>
                    <div className="mt-10 flex flex-col">
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded mb-2" to="/index">홈</Link>
                        <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded mb-2"
                              onClick={openModal}>글쓰기</Link>
                        <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded mb-2"
                              to={`/UserPage/${id}`}>마이페이지</Link>
                        <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded mb-2" to="/Message">메세지</Link>
                        <button
                            className={`p-2 mb-2 rounded flex justify-start items-center ${newAlarm ? 'text-red-600' : 'text-gray-600'} hover:bg-gray-200`}
                            onClick={() => {
                                setNewAlarm(false);
                                openAlarmModal();
                            }}
                        >
                            {newAlarm && <span className="w-2 h-2 bg-red-600 rounded-full mr-2"></span>}
                            알림
                        </button>
                        <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded mb-2" onClick={openSettingModal}>환경
                            설정</Link>
                        <button className="text-gray-600 hover:bg-gray-200 p-2 mb-2 rounded flex justify-start"
                                onClick={handleLogout}>로그아웃
                        </button>
                    </div>
                </div>
                <BoardWrite isOpen={isModalOpen} onRequestClose={closeModal} onClose={closeModal}/>
                {alarmModal && <Alarm isOpen={alarmModal} onClose={closeAlarmModal}/>}
                <Setting isOpen={settingModal} onRequestClose={closeSettingModal} onClose={closeSettingModal}/>
            </BrowserView>


            <MobileView>
                { /* 모바일 화면에서는 네비게이션 바를 렌더링 */}
                <div className="fixed bottom-0 left-0 w-full bg-white border-t border-gray-200 flex justify-around p-4"
                     style={{zIndex: '2'}}>
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm" to="/index">홈</Link>
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm" onClick={openModal}>글쓰기</Link>
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm"
                          to={`/UserPage/${id}`}>마이페이지</Link>
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm" to="/Message">메세지</Link>
                    <Link className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm" onClick={openSettingModal}>환경
                        설정</Link>
                    <button className="text-gray-600 hover:bg-gray-200 p-2 rounded text-sm"
                            onClick={handleLogout}>로그아웃
                    </button>
                </div>
                <BoardWrite isOpen={isModalOpen} onRequestClose={closeModal} onClose={closeModal}/>
                <Setting isOpen={settingModal} onRequestClose={closeSettingModal} onClose={closeSettingModal}/>
            </MobileView>
        </>
    );

}

// Export the Sidebar component
export default Sidebar;