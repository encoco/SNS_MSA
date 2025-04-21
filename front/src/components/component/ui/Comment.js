import React, {useEffect, useState} from 'react';
import api from "../../../api";
import {Link} from "react-router-dom";

function Comment({isOpen, onClose, boardId}) {
    const [commentText, setCommentText] = useState('');
    const [commentText2, setCommentText2] = useState('');
    const [activeDropdown, setActiveDropdown] = useState(null);
    const [editCommentId, setEditCommentId] = useState(null);
    const [originalCommentText, setOriginalCommentText] = useState('');
    const [comments, setComments] = useState([]);
    const [profile, setProfile] = useState('');

    useEffect(() => {
        if (isOpen) {
            fetchComments();
        }
        if (!profile) {
            const userInfoJSON = localStorage.getItem('nickname');
            if (userInfoJSON) {
                const userInfo = JSON.parse(userInfoJSON);
                setProfile(userInfo);
            }
        }
    }, [isOpen]);

    const fetchComments = async () => {
        try {
            const response = await api.get(`/boards/getComments`, {
                params: {boardId},
                withCredentials: true,
            });
            setComments(response.data);
        } catch (error) {
            console.error('Error fetching comments:', error);
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const formattedDate = date.toLocaleDateString('en-CA'); // YYYY-MM-DD format

        const options = {
            hour: 'numeric',
            minute: 'numeric',
            hour12: true
        };
        const formattedTime = new Intl.DateTimeFormat('en-US', options).format(date);

        return `${formattedDate} ${formattedTime}`;
    };

    const handleClose = () => {
        onClose();
        setCommentText('');
    };

    const handleOverlayClick = () => {
        onClose();
    };

    const handleModalContentClick = (e) => {
        e.stopPropagation();
    };

    const handleCommentWrite = async () => {
        if (commentText.length === 0) {
            alert("내용을 입력해주세요.");
            return 0;
        }
        if (isOpen) {
            const requestData = {
                comment: commentText,
                board_id: boardId
            };
            console.log(requestData);
            try {
                const response = await api.post(`/boards/CommentWrite`, requestData, {
                    withCredentials: true,
                });
                if (response.data === "success") {
                    alert('댓글 작성 성공');
                    setCommentText('');
                    fetchComments();  // 댓글 작성 후 댓글 목록 새로고침
                } else {
                    alert('댓글 작성 실패');
                }
            } catch (error) {
                console.log(error);
                alert('댓글 작성 중 오류가 발생했습니다.');
            }
        }
    };

    const toggleDropdown = (commentId) => {
        setActiveDropdown(activeDropdown === commentId ? null : commentId);
    };

    const handleEdit = (commentId, commentText2) => {
        setEditCommentId(commentId);
        setOriginalCommentText(commentText2);
        setCommentText2(commentText2); // 수정할 댓글 내용을 input에 표시
        setActiveDropdown(null);
    };

    const handleEditConfirm = async (comment) => {
        try {
            comment.comment = commentText2;
            if (comment.comment.length === 0) {
                alert("내용을 입력해주세요.");
                return;
            }
            const response = await api.post(`/boards/EditComment`, comment, {
                withCredentials: true,
            });

            if (response.data === "success") {
                alert('댓글 수정 성공');
                setEditCommentId(null);
                fetchComments();  // 댓글 수정 후 댓글 목록 새로고침
            } else {
                alert('댓글 수정 실패');
            }
        } catch (error) {
            console.log(error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        }
    };

    const handleEditCancel = () => {
        setEditCommentId(null);
        setCommentText2(originalCommentText); // 이전에 저장한 댓글 내용으로 복구
    };

    const handleDelete = async (comment) => {
        try {
            const response = await api.post(`/boards/DeleteComment`, comment, {
                withCredentials: true,
            });
            if (response.data === "success") {
                alert('댓글 삭제 성공');
                fetchComments();  // 댓글 삭제 후 댓글 목록 새로고침
            } else {
                alert('댓글 삭제 실패');
            }
        } catch (error) {
            console.log(error);
            alert('댓글 삭제 중 오류가 발생했습니다.');
        }
        setActiveDropdown(null);
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleCommentWrite();
        }
    };

    if (!isOpen) {
        return null;
    }

    return (
        <div className="fixed inset-0 bg-opacity-50 flex justify-center items-center z-50" onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-3xl w-full m-4 z-50 relative overflow-hidden"
                 onClick={handleModalContentClick}>
                <h2 className="text-xl font-bold mb-4">댓글</h2>
                <div className="mb-4 flex">
                    <input
                        type="text"
                        placeholder="댓글 추가..."
                        className="border border-gray-300 p-2 flex-grow mr-2"
                        value={commentText}
                        onChange={(e) => setCommentText(e.target.value)}
                        onKeyPress={handleKeyPress}
                    />
                    <button onClick={handleCommentWrite}
                            className="bg-black hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                        댓글 달기
                    </button>
                </div>
                <div className="overflow-y-auto max-h-96">
                    <ul>
                        {comments && Array.isArray(comments) && comments.map(comment => (
                            <li key={comment.comment_id} className="mt-1 flex items-center relative">
                                <img src={comment.profile_img ? comment.profile_img : "/placeholder.svg"} alt="Profile"
                                     className="w-10 h-10 rounded-full mr-3"/>
                                <div className="flex flex-col flex-grow">
                                    {editCommentId === comment.comment_id ? (
                                        <input
                                            type="text"
                                            value={commentText2}
                                            onChange={(e) => setCommentText2(e.target.value)}
                                            className="border border-gray-300 p-2 flex-grow mr-2"
                                        />
                                    ) : (
                                        <>
                                            <div className="flex items-center mb-1">
                                                <Link to={`/UserPage/${comment.id}`}>
                                                    <div className="font-semibold"><strong>{comment.nickname}</strong>
                                                    </div>
                                                </Link>

                                            </div>
                                            <span className="text-gray-600 ">{comment.comment}</span>
                                            <div className="flex items-center text-sm text-gray-500">
                                                <span>{formatDate(comment.date)}</span>
                                            </div>
                                        </>
                                    )}
                                </div>
                                {editCommentId === comment.comment_id ? (
                                    <>
                                        <button onClick={() => handleEditConfirm(comment)}
                                                className="ml-2 text-gray-500">
                                            확인
                                        </button>
                                        <button onClick={() => handleEditCancel()} className="ml-2 text-gray-500">
                                            취소
                                        </button>
                                    </>
                                ) : (
                                    <button onClick={() => toggleDropdown(comment.comment_id)}
                                            className="mr-4 text-gray-500">
                                        ⋮
                                    </button>
                                )}
                                <div onClick={() => setActiveDropdown(null)}>
                                    {comment.id === profile.id && activeDropdown === comment.comment_id && (
                                        <div
                                            className="absolute right-0 mt-2 w-32 bg-white border border-gray-200 rounded shadow-lg z-10">
                                            <ul>
                                                <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                                                    onClick={() => handleEdit(comment.comment_id, comment.comment)}>수정
                                                </li>
                                                <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                                                    onClick={() => handleDelete(comment)}>삭제
                                                </li>
                                            </ul>
                                        </div>
                                    )}
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
                <button onClick={handleClose}
                        className="absolute top-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
            </div>
        </div>
    );
}

export default Comment;