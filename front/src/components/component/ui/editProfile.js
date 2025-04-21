import React, {useEffect, useState} from 'react';
import api from "../../../api";

function EditProfile({Open, Close, userInfo}) {
    const [profilePic, setProfilePic] = useState(userInfo.img);
    const [profilePicPreview, setProfilePicPreview] = useState('');
    const [nickname, setNickname] = useState("");
    const [statusMessage, setStatusMessage] = useState("");
    const [newNickname, setNewNickname] = useState(userInfo.nickname);
    useEffect(() => {
        setNickname(userInfo.nickname);
        setNewNickname(userInfo.nickname);
        setStatusMessage(userInfo.state_message);
        if (userInfo.img) {
            setProfilePicPreview(userInfo.img);
        }

    }, [userInfo]);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setProfilePic(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setProfilePicPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleClose = () => {
        setProfilePicPreview('');
        setNewNickname(userInfo.nickname);
        Close();
    };

    const handleOverlayClick = () => {
        setProfilePicPreview('');
        setNewNickname(userInfo.nickname);
        Close();
    };

    const handleModalContentClick = (e) => {
        e.stopPropagation();
    };

    if (!Open) {
        return null;
    }

    const handleSave = async () => {
        const formData = new FormData();
        formData.append('nickname', newNickname);
        formData.append('state_message', statusMessage);
        formData.append('original', nickname);
        if (profilePic) {
            formData.append('imgpath', profilePic);
        }
        if( nickname === newNickname ) {
            alert("같은 이름으로 변경할 수 없습니다.");
            return false;
        }
        try {
            const response = await api.post(`/users/WriteProfile`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                withCredentials: true,
            });
            setProfilePicPreview('');
            localStorage.setItem('nickname', JSON.stringify(response.data));
            alert('프로필이 성공적으로 업데이트되었습니다.');
            window.location.reload();
            Close();
        } catch (error) {
            console.error('프로필 업데이트 실패:', error.response);
            alert('프로필 업데이트에 실패했습니다.');
        }
    };


    return (
        <div className="fixed inset-0 bg-gray-40 bg-opacity-50 flex justify-center items-center z-50"
             onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full m-4 z-50 relative"
                 onClick={handleModalContentClick}>
                <h2 className="text-xl font-bold mb-4">내정보수정</h2>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        프로필 사진
                    </label>
                    <input
                        type="file"
                        className="border border-gray-300 p-2 w-full"
                        onChange={handleFileChange}
                        accept="image/*"
                    />
                    {profilePicPreview && (
                        <img src={profilePicPreview} alt="프로필 미리보기" className="mt-4 w-32 h-32 rounded-full"/>
                    )}
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        닉네임
                    </label>
                    <input
                        type="text"
                        placeholder="닉네임"
                        className="border border-gray-300 p-2 w-full"
                        value={newNickname}
                        onChange={(e) => setNewNickname(e.target.value)}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        상태 메시지
                    </label>
                    <input
                        type="text"
                        placeholder="상태 메시지"
                        className="border border-gray-300 p-2 w-full"
                        value={statusMessage || ''}
                        onChange={(e) => setStatusMessage(e.target.value)}
                    />
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

export default EditProfile;