import React, {useState, useEffect} from 'react';
import AddCommuChat from './ui/AddCommuChat.js';
import api from "../../api";

function Together({onJoinRoom}) {
    const [showModal, setShowModal] = useState(false);
    const [selectedPostId, setSelectedPostId] = useState(null);
    const [addCommu, setAddCommu] = useState(false);
    const [commuRoom, setCommuRoom] = useState(null);
    const [joinedRoomIds, setJoinedRoomIds] = useState(new Set());
    const [participantsCount, setParticipantsCount] = useState({});

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [allRooms, myRooms] = await Promise.all([
                    api.get(`/chats/open`, { withCredentials: true }),
                    api.get(`/chats/open/my`, { withCredentials: true })
                ]);

                setCommuRoom(allRooms.data === "채팅방 없음" ? [] : allRooms.data);
                const ids = new Set(myRooms.data.map(room => room.openChatId));
                setJoinedRoomIds(ids);
            } catch (error) {
                console.error("방 목록 불러오기 실패", error);
            }
        };
        fetchData();
    }, []);

    const handleJoin = async (room) => {
        if (joinedRoomIds.has(room.openChatId)) {
            console.log("이미 참여된 방입니다.");
            onJoinRoom(room); // 👉 그냥 포커싱만
            return;
        }

        const confirmJoin = window.confirm("정말 참여하시겠습니까?");
        if (!confirmJoin) return;

        try {
            await api.post(`/chats/open/join`, {
                openChatId: room.openChatId,
                id: room.id,
                title: room.title,
                description: room.description,
                imgpath: room.imgpath
            }, {
                withCredentials: true,
            });

            setJoinedRoomIds(prev => new Set(prev).add(room.openChatId)); // ✅ 새 방 추가
            onJoinRoom(room);
        } catch (error) {
            console.log("참여 실패", error);
        }
    };

    return (
        <div className="Together bg-gray-100/40 dark:bg-gray-800/40 p-4 flex flex-wrap">
            <div className="w-full relative mb-4">
                <button
                    onClick={() => setAddCommu(true)}
                    className="absolute right-4 top-0 flex items-center gap-2 bg-black text-white px-3 py-2 rounded-full text-sm hover:bg-gray-800 shadow-md transition"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" className="w-4 h-4" viewBox="0 0 24 24"
                         stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4"/>
                    </svg>
                    만들기
                </button>
            </div>

            {commuRoom && commuRoom.map(room => (
                <div key={room.openChatId} className="w-full md:w-1/2 mb-4 px-2">
                    <div className="post bg-white rounded-lg overflow-hidden shadow-md flex"
                         style={{minHeight: '250px', maxHeight: '300px'}}>
                        <div className="w-3/5 p-3 flex flex-col justify-between">
                            <div>
                                <p className="font-semibold text-lg mb-2">{room.title}</p>
                                {room.description}
                            </div>
                            <div className="flex items-center justify-between">
                                <div className="flex items-center">
                                    <svg className="h-5 w-5 rounded-full fill-current text-gray-400 mr-1"
                                         viewBox="0 0 24 24">
                                        <path
                                            d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                                    </svg>
                                    <span className="text-gray-600">{room.participantCount}</span>
                                </div>
                                <button onClick={() => handleJoin(room)}
                                        className="bg-black text-white font-bold py-1 px-2 rounded">
                                    참여
                                </button>
                            </div>
                        </div>
                        <div className="w-2/5 flex justify-center items-center p-3">
                            <img
                                onClick={() => {
                                    setSelectedPostId(room.openChatId);
                                    setShowModal(true);
                                }}
                                src={room.imgpath || "/placeholder.svg"}
                                alt="Chat Room"
                                className="h-full w-full object-cover"
                            />
                        </div>
                    </div>
                </div>
            ))}

            {showModal && (
                <div className="fixed inset-0 z-10 overflow-y-auto flex items-center justify-center p-4">
                    <div className="bg-white shadow-lg rounded-lg overflow-hidden">
                        <div className="p-4">
                            <img
                                src={commuRoom.find(room => room.openChatId === selectedPostId)?.imgpath || "/placeholder.svg"}
                                alt="Post Image"
                                className="max-w-full max-h-screen"
                            />
                        </div>
                        <div className="flex justify-center p-4">
                            <button onClick={() => setShowModal(false)}
                                    className="bg-gray-500 hover:bg-gray-400 text-white font-bold py-2 px-4 rounded">
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
            <AddCommuChat Open={addCommu} Close={() => setAddCommu(false)}/>
        </div>
    );
}

export default Together;