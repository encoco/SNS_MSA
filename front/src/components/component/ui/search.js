import React, {useEffect, useState} from 'react';
import api from "../../../api";

function Search({isOpen, onClose, onRoomCreated, roomList, RoomSelectChange}) {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);


    const handleSelect = (user) => {
        setSelectedUsers(prevUsers => {
            if (prevUsers.includes(user.id)) {
                return prevUsers.filter(id => id !== user.id);
            } else {
                return [...prevUsers, user.id];
            }
        });
    };

    useEffect(() => {
        const fetchSearchResults = async () => {
            try {
                const response = await api.get(`/users/search`, {
                    params: {
                        searchTerm: searchTerm
                    },
                    withCredentials: true
                });
                setSearchResults(response.data);
            } catch (error) {
                console.error('Error fetching search results:', error);
            }
        };

        if (searchTerm && searchTerm.trim() !== '') {
            fetchSearchResults();
        } else {
            setSearchResults([]);
        }
    }, [searchTerm]);

    if (!isOpen) {
        return null;
    }

    const handleOverlayClick = () => {
        setSearchResults(null);
        setSearchTerm('');
        setSelectedUsers([]);
        onClose(); // 모달 닫는 로직
    };

    const handleModalContentClick = (e) => {
        e.stopPropagation();
    };

    const requestData = {
        id: selectedUsers
    };

    const handleMake = async () => {
        try {
            if (selectedUsers.length === 0) {
                alert('유저를 선택해주세요');
                return;
            }
            const response = await api.post(`/chats/rooms`, requestData, {
                withCredentials: true,
            });
            if (response.data["1"]) {		//있을때
                RoomSelectChange(response.data["1"]);
                console.log("있음 : " , response.data["1"]);
                handleOverlayClick(); // 모달 닫기
            } else if (response.data["0"]) {//없을때
                onRoomCreated(response.data["0"]);
                console.log("없음 : " ,response.data["0"]);
                handleOverlayClick(); // 모달 닫기
            } else {
                alert('다시 시도해주세요.');
            }

        } catch (error) {
            console.log(error);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
             onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full m-4 z-50 relative overflow-hidden"
                 onClick={handleModalContentClick}>
                <h2 className="text-xl font-bold mb-4">채팅방 생성</h2>
                <div className="mb-4 flex">
                    <input
                        type="text"
                        placeholder="유저 검색..."
                        className="border border-gray-300 p-2 flex-grow mr-2"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button onClick={handleMake}
                            className="bg-black hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                        생성
                    </button>
                </div>
                <div className="overflow-y-auto max-h-96">
                    {searchResults && searchResults.length > 0 && (
                        <div className="bg-white shadow-md rounded-md z-10">
                            {searchResults.map(user => (
                                <div key={user.id} className="block">
                                    <div className="flex items-center space-x-4 p-2 hover:bg-gray-200 cursor-pointer">
                                        <button
                                            onClick={() => handleSelect(user)}
                                            className={`flex items-center space-x-3 p-2 rounded-lg transition-colors w-full text-left 
                                            ${selectedUsers.includes(user.id) ? 'bg-gray-200' : 'hover:bg-gray-20 dark:hover:bg-gray-100'}`}
                                        >
                                            <img
                                                alt={user.nickname}
                                                className="w-8 h-8 rounded-full"
                                                src={user.img || "/placeholder.svg"}
                                            />
                                            <span>{user.nickname}</span>
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <button onClick={onClose}
                        className="absolute top-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
            </div>
        </div>
    );
}

export default Search;
