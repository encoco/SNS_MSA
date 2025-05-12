import React, {useState} from 'react';
import api from "../../../api";
import BoardWrite from '../BoardWrite';

function DropdownMenu({post}) {
    const [isOpen, setIsOpen] = useState(false);
    const userNickname = JSON.parse(localStorage.getItem("nickname")).nickname;
    const toggleDropdown = () => setIsOpen(!isOpen);
    const [showModal, setShowModal] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false); // 수정 모달 상태 추가

    const editPost = (post) => {
        setIsEditModalOpen(true); // 수정 모달 열기
        setIsOpen(false); // 드롭다운 메뉴 닫기
    };

    const deletePost = (post) => {
        setShowModal(true);
    };

    const confirmDelete = async () => {
        try {
            const board_id = post.board_id;
            await api.post(`/boards/boardDelete`, board_id, {
                withCredentials: true,
            });
            alert("게시물이 삭제되었습니다.");
            setShowModal(false);
            window.location.reload();
        } catch (error) {
            console.log(error);
            alert('게시물 삭제에 실패했습니다.');
            window.location.reload();
        }
    };

    // 조건부 렌더링 로직
    if (post.nickname !== userNickname) {
        return null;
    }
    return (
        <div className="relative">
            <button onClick={toggleDropdown} className="p-2">
                <MoreHorizontalIcon className="h-5 w-5 text-gray-700"/>
            </button>
            {isOpen && (
                <div className="absolute right-0 mt-2 py-2 w-48 bg-white rounded-md shadow-xl z-20">
                    <button onClick={() => editPost(post)}
                            className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left">수정
                    </button>
                    <button onClick={() => deletePost(post)}
                            className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left">삭제
                    </button>
                </div>
            )}
            {showModal && (
                <div className="fixed inset-0 flex items-center justify-center z-50">
                    <div className="absolute inset-0 bg-gray-500 opacity-50"></div>
                    <div className="bg-white p-8 rounded-md shadow-md z-50">
                        <p className="text-lg font-semibold">게시물 삭제</p>
                        <p className="text-gray-700 mt-2">정말로 이 게시물을 삭제하시겠습니까?</p>
                        <div className="mt-4 flex justify-end">
                            <button onClick={() => setShowModal(false)} className="text-gray-500 mr-4">취소</button>
                            <button onClick={confirmDelete} className="bg-red-500 text-white px-4 py-2 rounded-md">삭제
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {/* BoardWrite 컴포넌트를 수정 모달로 사용 */}
            <BoardWrite
                isOpen={isEditModalOpen}
                onClose={() => setIsEditModalOpen(false)}
                onRequestClose={() => setIsEditModalOpen(false)}
                post={post}
            />
        </div>
    );
}

export default DropdownMenu;

function MoreHorizontalIcon(props) {
    return (
        <svg
            {...props}
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <circle cx="12" cy="12" r="1"/>
            <circle cx="19" cy="12" r="1"/>
            <circle cx="5" cy="12" r="1"/>
        </svg>
    )
}