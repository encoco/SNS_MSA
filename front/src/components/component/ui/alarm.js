import React, {useEffect, useState} from 'react';
import api from "../../../api";
import {Link} from "react-router-dom";
import DetailModal from './DetailModal';

function Alarm({isOpen, onClose}) {
    const [alarmData, setAlarmData] = useState([]);
    const [isModalOpen, setModalOpen] = useState(false);
    const [share_id, setShare_id] = useState('');

    useEffect(() => {
        const fetchAlarm = async () => {
            try {
                // userId를 사용하여 사용자의 게시물을 가져옴
                const response = await api.get(`/alarms/getAlarm`, {
                    withCredentials: true,
                });
                setAlarmData(response.data);
                console.log(response.data);
            } catch (error) {
                console.log(error);
            }
        };
        fetchAlarm();
    }, [isOpen]);

    if (!isOpen) {
        return null;
    }

    const openModal = (board_id) => {
        setShare_id(board_id);  // Set the share_board_id when opening the modal
        setModalOpen(true);  // Function to open the modal
    };

    const handleDeleteAlarm = async () => {
        try {
            const response = await api.post(`/alarms/delAllAlarm`, {
                withCredentials: true,
            });
            console.log(response.data);
            setAlarmData('');
        } catch (error) {
            console.log(error);
        }
    }
    return (
        <div className="fixed inset-0 bg-opacity-50 h-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-3xl w-full m-4 z-50 relative overflow-hidden">
                <div className="flex justify-start items-center mb-4">
                    <h2 className="text-xl font-bold">알림</h2>
                    <button onClick={handleDeleteAlarm} className="ml-5">모두 지우기</button>
                </div>
                <div className="overflow-y-auto max-h-96">
                    {alarmData && alarmData.map((alarm, index) => (
                        <div key={index} className="flex items-center mb-4">
                            <img src={alarm.profile_img ? alarm.profile_img : "/placeholder.svg"} alt="Profile"
                                 className="w-10 h-10 rounded-full mr-3"/>
                            <div className="flex-grow">
                                <div className="flex justify-between items-center">
                                    <Link to={`/UserPage/${alarm.sender_id}`}
                                          className="font-semibold"><strong>{alarm.nickname}</strong></Link>
                                    {alarm.board_id !== 0 && (
                                        <button
                                            onClick={() => openModal(alarm.board_id)}
                                            className="bg-black text-white px-4 py-2 rounded hover:bg-green-600 transition duration-150 ease-in-out"
                                        >
                                            보기
                                        </button>
                                    )}
                                </div>
                                <span className="text-gray-600 block">{alarm.content}</span>
                                <div className="text-sm text-gray-500">
                                    <span>{alarm.date}</span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
                <button onClick={onClose}
                        className="absolute top-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
            </div>
            <DetailModal isOpen={isModalOpen} onClose={() => setModalOpen(false)} boardId={share_id}/>
        </div>
    );
}

export default Alarm;