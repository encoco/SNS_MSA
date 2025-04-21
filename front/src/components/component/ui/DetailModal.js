import React, {useEffect, useState} from 'react';
import api from "../../../api";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "./card";
import ImageSlider from '../ImageSlider';
import DropdownMenu from './DropdownMenu';
import Comment from './Comment';
import {Link} from "react-router-dom";
import Share from './share';


const DetailModal = ({isOpen, onClose, boardId}) => {
    const [post, setPost] = useState('');
    const [likesCount, setLikesCount] = useState([]);
    const [userLikes, setUserLikes] = useState(new Set());
    const [currentComments, setCurrentComments] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [showShareModal, setShowShareModal] = useState(false);
    const [currentPost, setCurrentPost] = useState();

    useEffect(() => {
        if (!isOpen) {
            return;
        }
        const fetchPosts = async () => {
            try {
                const response = await api.get(`/boards/findPost?boardId=${boardId}`, {
                    withCredentials: true,
                });
                console.log(response.data);
                setPost(response.data.posts);

                const likesCount = response.data.likes.reduce((acc, like) => {
                    acc[like.board_id] = (acc[like.board_id] || 0) + 1;
                    return acc;
                }, {});

                setLikesCount(likesCount);
                // 사용자가 좋아요를 누른 게시물 ID를 Set으로 저장
                const userLikes = new Set(response.data.likes.filter(like => like.id === parseInt(14)).map(like => like.board_id));
                setUserLikes(userLikes);
            } catch (error) {
                console.error(error);
            }
        };
        fetchPosts();
    }, [isOpen, boardId]);

    const handleOverlayClick = () => onClose();
    const handleModalContentClick = (e) => e.stopPropagation();

    const LikeHandler = async (boardId, writerId) => {
        try {
            const response = await api.get(`/boards/boardLike?boardId=${boardId}&writerId=${writerId}`, {
                withCredentials: true,
            });
            if (response.data === "success") {
                setLikesCount(prevLikesCount => ({
                    ...prevLikesCount,
                    [boardId]: Math.max((prevLikesCount[boardId] || 0) + 1, 0)
                }));
                setUserLikes(prevLikes => new Set(prevLikes).add(boardId)); // 좋아요 추가
            } else if (response.data === "fail") {
                setLikesCount(prevLikesCount => ({
                    ...prevLikesCount,
                    [boardId]: Math.max((prevLikesCount[boardId] || 0) - 1, 0)
                }));
                setUserLikes(prevLikes => {
                    const newLikes = new Set(prevLikes);
                    newLikes.delete(boardId); // 좋아요 삭제
                    return newLikes;
                });
            }
        } catch (error) {
            console.log(error);
            alert('다시 시도해주세요');
        }
    };

    if (!isOpen) {
        return null;
    }

    return (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-50 flex justify-center items-center z-50"
             onClick={handleOverlayClick}>
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full m-4 relative"
                 onClick={handleModalContentClick}>
                <button onClick={onClose} className="absolute top-2 right-2 text-2xl font-bold text-gray-800">×</button>
                {post ? (
                    <Card key={post.board_id} className="w-full">
                        <CardHeader>
                            <div className="flex items-center">
                                <img
                                    alt="Profile"
                                    className="w-8 h-8 rounded-full mr-2"
                                    src={post.img || "/placeholder.svg"}
                                />
                                <div className="flex-grow"> {/* 이 부분이 추가됨 */}
                                    <CardTitle>
                                        <Link to={`/UserPage/${post.id}`}>
                                            <div className="font-semibold">{post.nickname}</div>
                                        </Link>
                                    </CardTitle>
                                </div>
                                <DropdownMenu post={post}/>
                            </div>
                        </CardHeader>
                        <CardContent>
                            {post.imgpath && <ImageSlider imgpath={post.imgpath}/>}
                            {post.video && <video src={post.video} controls/>}
                            <p className="mt-2">{post.content}</p>
                        </CardContent>
                        <CardFooter className="flex justify-between text-sm">
                            <div className="flex space-x-4 flex-wrap">
                                <button
                                    className="w-10 h-8"
                                    style={{color: userLikes.has(post.board_id) ? "red" : "inherit"}} // 좋아요 상태에 따라 색상 변경
                                    onClick={() => LikeHandler(post.board_id, post.id)}
                                >
                                    {likesCount[post.board_id] > 0 ? `${likesCount[post.board_id]} ❤` : '0 ❤'}
                                </button>
                                <button
                                    className="w-16 h-8"
                                    onClick={() => {
                                        setCurrentComments(post.board_id);
                                        setShowModal(true); // 모달 보이기
                                    }}
                                >
                                    댓글
                                </button>
                                <button className="w-16 h-8" onClick={() => {
                                    setCurrentPost(post);
                                    setShowShareModal(true);
                                }}>공유
                                </button>
                            </div>
                        </CardFooter>
                    </Card>
                ) : (
                    <p>Loading...</p> // Display a loading text or spinner
                )}
            </div>
            <Share isOpen={showShareModal} onClose={() => setShowShareModal(false)} post={currentPost}/>
            <Comment isOpen={showModal} onClose={() => setShowModal(false)} boardId={currentComments}/>
        </div>
    );
};

export default DetailModal;
