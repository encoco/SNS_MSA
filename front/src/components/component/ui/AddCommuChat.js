import React, {useRef, useState} from 'react';
import api from "../../../api";

function AddCommuChat({Open, Close}) {
    const [img, setImg] = useState(null);
    const [profilePicPreview, setProfilePicPreview] = useState('');
    const [title, setTitle] = useState(null);
    const [description, setDescription] = useState(null);
    const fileInputRef = useRef(null);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImg(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setProfilePicPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleClose = () => {
        Close();
    };

    const handleOverlayClick = () => {
        Close();
    };

    const handleModalContentClick = (e) => {
        e.stopPropagation();
    };

    const handleClearImage = () => {
        setImg(null);
        setProfilePicPreview('');
        if (fileInputRef.current) {
            fileInputRef.current.value = "";  // 입력란 초기화
        }
    };
    if (!Open) {
        return null;
    }

    const handleSave = async () => {

        const formData = new FormData(); // FormData 객체 생성

        formData.append('description', description); // 글 내용 추가
        formData.append('title', title);
        if (img) {
            formData.append('img', img);
        }

        try {
            // 새 게시물 생성 요청
            await api.post('/chats/CreateCommChat', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });
            alert('채팅방 생성 완료');
            Close();
        } catch (error) {
            console.log(error);
            alert('다시 시도해주세요.');
        }
    };


    return (
        <div className="fixed inset-0 bg-gray-700 bg-opacity-50 flex justify-center items-center z-50"
             onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full m-4 z-50 relative"
                 onClick={handleModalContentClick}>
                <h2 className="text-xl font-bold mb-4">오픈채팅 만들기</h2>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        방 이름
                    </label>
                    <input
                        type="text"
                        placeholder="방 이름"
                        className="border border-gray-300 p-2 w-full"
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        방 소개
                    </label>
                    <input
                        type="text"
                        placeholder="방 소개"
                        className="border border-gray-300 p-2 w-full"
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        설명 사진
                    </label>
                    <input
                        type="file"
                        className="border border-gray-300 p-2 w-full"
                        onChange={handleFileChange}
                        ref={fileInputRef}  // ref 연결
                        accept="image/*"
                    />
                    {profilePicPreview && (
                        <div className="relative mt-4 inline-block">
                            <img src={profilePicPreview} alt="프로필 미리보기" className="w-32 h-32 rounded-full shadow-lg"/>
                            <button
                                onClick={handleClearImage}
                                className="absolute top-0 right-0 bg-red-500 hover:bg-red-700 text-white font-bold p-1 rounded-full shadow transition duration-150 ease-in-out transform hover:-translate-y-1 hover:scale-110"
                                style={{marginTop: '-8px', marginRight: '-8px'}}
                            >
                                <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                                    <path
                                        d="M14.348 14.849a1 1 0 000-1.414L10.707 10l3.641-3.641a1 1 0 10-1.414-1.414L9.293 8.586 5.652 4.945A1 1 0 104.238 6.359l3.641 3.641-3.641 3.641a1 1 0 101.414 1.414l3.641-3.641 3.641 3.641a1 1 0 001.414 0z"/>
                                </svg>
                            </button>
                        </div>
                    )}

                </div>
                <button onClick={handleSave}
                        className="bg-black hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                    저장
                </button>
                <button onClick={handleClose}
                        className="absolute top-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
            </div>
        </div>
    );
}

export default AddCommuChat;