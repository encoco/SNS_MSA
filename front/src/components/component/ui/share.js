import React, {useEffect, useState} from 'react';
import api from "../../../api";

function Share({isOpen, onClose, post}) {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);

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

    const handleSelect = (user) => {
        setSelectedUsers(prevUsers => {
            if (prevUsers.includes(user.id)) {
                return prevUsers.filter(id => id !== user.id);
            } else {
                return [...prevUsers, user.id];
            }
        });
    };

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
        board_id: post.board_id,
        Ids: selectedUsers
    };

    const handleMake = async () => {
        try {
            if (selectedUsers.length === 0) {
                alert('유저를 선택해주세요');
                return;
            }
            await api.post(`/chats/SharePost`, requestData, {
                withCredentials: true
            });
            alert('공유 완료');
            onClose();
        } catch (error) {
            console.log(error);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
             onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full m-4 z-50 relative overflow-hidden"
                 onClick={handleModalContentClick}>
                <h2 className="text-xl font-bold mb-4">공유하기</h2>
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
                        공유
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

export default Share;
