import React, {useState, useEffect} from 'react';
import ImageSlider from './ImageSlider'; // ImageSlider 컴포넌트를 import
import api from "../../api";
import {useParams} from "react-router-dom";
import {CardTitle, CardHeader, CardContent, CardFooter, Card} from "./ui/card";
import Sidebar from "./ui/Sidebar";
import DropdownMenu from './ui/DropdownMenu';
import Comment from './ui/Comment';
import EditProfile from './ui/editProfile.js';
import Share from './ui/share';
import {BrowserView, MobileView} from 'react-device-detect';
import {Link} from "react-router-dom";

function UserPage() {
    const [showTopBtn, setShowTopBtn] = useState(false);
    const [posts, setPosts] = useState([]);
    const [likesCount, setLikesCount] = useState([]);
    const {userId} = useParams();
    const [userInfo, setUserInfo] = useState(null);
    const [isFollowing, setIsFollowing] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [editProfile, setEditProfile] = useState(false);
    const [currentComments, setCurrentComments] = useState([]);
    const [showShareModal, setShowShareModal] = useState(false);
    const [currentPost, setCurrentPost] = useState();
    const [nickname, setNickname] = useState(''); // 초기 상태를 빈 문자열로 설정
    const [profile, setProfile] = useState('');
    const [userLikes, setUserLikes] = useState(new Set());
    const [isLoading, setisLoading] = useState(true);
    const [id,setId] = useState(0);

    useEffect(() => {
        // userInfo에서 nickname을 추출하여 상태에 저장
        const userInfoJSON = localStorage.getItem('nickname');
        if (userInfoJSON) {
            const userInfo = JSON.parse(userInfoJSON);
            setNickname(userInfo.nickname); // nickname 상태 업데이트
            setProfile(userInfo);
            setId(userInfo.id);
        }
    }, []);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                // userId를 사용하여 사용자의 게시물을 가져옴
                const response = await api.get(`/boards/userPosts?userId=${userId}`, {
                    withCredentials: true,
                });
                setUserInfo(response.data.userInfo);
                setPosts(response.data.posts);
                setIsFollowing(response.data.follow);
                setisLoading(false);

                const likesCount = response.data.likes.reduce((acc, like) => {
                    acc[like.board_id] = (acc[like.board_id] || 0) + 1;
                    return acc;
                }, {});

                setLikesCount(likesCount);

                // 사용자가 좋아요를 누른 게시물 ID를 Set으로 저장
                const userLikes = new Set(response.data.likes.filter(like => like.id === id).map(like => like.board_id));
                setUserLikes(userLikes);
            } catch (error) {
                console.log(error);
            }
        };
        fetchPosts();
    }, [userId,id]);

    useEffect(() => {
        const handleScroll = () => {
            if (window.scrollY > 300) {
                setShowTopBtn(true);
            } else {
                setShowTopBtn(false);
            }
        };

        window.addEventListener("scroll", handleScroll);
        // 컴포넌트 언마운트 시 이벤트 리스너 제거
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const handleFollow = async () => {
        try {
            const response = await api.post(`/users/follow/${userId}`, {}, {
                withCredentials: true,
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                },
            });

            if (response.status === 200) {
                const result = response.data;
                setIsFollowing(result === "followed");
            } else {
                console.error('Follow action failed:', response);
            }
        } catch (error) {
            console.error('Follow action error:', error);
        }
    };

    // 맨 위로 스크롤하는 함수
    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth' // 부드러운 스크롤
        });
    };

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

    const handleEditProfile = () => {
        console.log("pro : ", profile);
        setEditProfile(true);
    };

    return (
        <div className='app'>
            <BrowserView>
                <div className="grid min-h-screen w-full grid-cols-[280px_1fr] flex min-h-screen bg-gray-100 ">
                    <Sidebar/>
                    <div className="flex flex-col ml-10 mr-10">
                        {userInfo && (
                            <div className="flex flex-col  mb-5 mt-6">
                                <div className="flex items-center">
                                    <img
                                        alt="Profile"
                                        className="w-8 h-8 rounded-full mr-2  z-1"
                                        src={userInfo.img || "/placeholder.svg"}
                                    />
                                    <h2 className="text-2xl">{userInfo.nickname}</h2>
                                    {nickname !== userInfo.nickname ? (
                                        <button
                                            onClick={handleFollow}
                                            className={`ml-5 px-3 py-1 text-sm font-medium rounded transition duration-150 ease-in-out ${isFollowing ? 'bg-red-500 hover:bg-red-700 text-white' : 'bg-black hover:bg-green-600 text-white'}`}
                                        >
                                            {isFollowing ? '언팔로우' : '팔로우'}
                                        </button>
                                    ) : (
                                        <button onClick={handleEditProfile}
                                                className="ml-5 px-3 py-1 text-sm font-medium rounded transition duration-150 ease-in-out bg-black hover:bg-green-600 text-white">
                                            내 정보 수정
                                        </button>
                                    )}
                                </div>
                                <div className="text-left my-4">
                                    <p className="mt-5 text-sm text-gray-600 mx-2">{profile.state_message || ""}</p> {/* 상태 메시지 추가 */}
                                </div>

                                <hr className="mt-4 mb-2 border-gray-300 dark:border-gray-700"/>
                            </div>
                        )}

                        {isLoading ? (
                            <div className="col-span-2 flex justify-center items-center h-full">
                                <p className="text-xl">로딩 중...</p>
                            </div>
                        ) : (!posts || posts.length === 0) ? (
                            <div className="col-span-2 flex justify-center items-center h-full">
                                <p className="text-xl">작성한 글이 없습니다.</p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-2 gap-4">
                                {posts.map((post) => (
                                    <Card key={post.board_id} className="w-full">
                                        <CardHeader>
                                            <div className="flex items-center">
                                                <img
                                                    alt="Profile"
                                                    className="w-8 h-8 rounded-full mr-2"
                                                    src={userInfo.img || "/placeholder.svg"}
                                                />
                                                <div className="flex-grow">
                                                    <CardTitle>{post.nickname}</CardTitle>
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
                                                    style={{color: userLikes.has(post.board_id) ? "red" : "inherit"}}
                                                    onClick={() => LikeHandler(post.board_id, post.id)}
                                                >
                                                    {likesCount[post.board_id] > 0 ? `${likesCount[post.board_id]} ❤` : '0 ❤'}
                                                </button>
                                                <button
                                                    className="w-16 h-8"
                                                    onClick={() => {
                                                        setCurrentComments(post.board_id);
                                                        setShowModal(true);
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
                                ))}
                            </div>
                        )}
                    </div>

                    <Comment isOpen={showModal} onClose={() => setShowModal(false)} boardId={currentComments}/>
                    <Share isOpen={showShareModal} onClose={() => setShowShareModal(false)} post={currentPost}/>
                    <EditProfile Open={editProfile} Close={() => setEditProfile(false)} userInfo={profile}/>

                    {showTopBtn && (
                        <button
                            className="fixed bottom-10 right-10 bg-white hover:bg-gray-100 text-gray-900 font-bold rounded-full h-12 w-12 flex justify-center items-center border-4 border-gray-900 cursor-pointer"
                            onClick={scrollToTop}
                            style={{
                                width: '50px',
                                height: '50px',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center'
                            }}
                        >
                            ▲
                        </button>
                    )}
                </div>
            </BrowserView>
            <MobileView>
                <div>
                    <Sidebar/>
                    <div className="flex flex-col">
                        {userInfo && (
                            <div className="flex flex-col  mb-5 mt-6">
                                <div className="flex items-center">
                                    <img alt="Profile" className="w-8 h-8 rounded-full mr-2 z-0 "
                                         src={userInfo.img || "/placeholder.svg"}/>
                                    <h2 className="text-2xl">{userInfo.nickname}</h2>
                                    {nickname !== userInfo.nickname ? (
                                        <button
                                            onClick={handleFollow}
                                            className={`ml-5 px-3 py-1 text-sm font-medium rounded transition duration-150 ease-in-out ${isFollowing ? 'bg-red-500 hover:bg-red-700 text-white' : 'bg-black hover:bg-green-600 text-white'}`}
                                        >
                                            {isFollowing ? '언팔로우' : '팔로우'}
                                        </button>
                                    ) : (
                                        <button onClick={handleEditProfile}
                                                className="ml-5 px-3 py-1 text-sm font-medium rounded transition duration-150 ease-in-out bg-black hover:bg-green-600 text-white">
                                            내 정보 수정
                                        </button>
                                    )}
                                </div>
                                <div className="text-left my-4">
                                    <p className="mt-5 text-sm text-gray-600 mx-2">{profile.state_message || ""}</p> {/* 상태 메시지 추가 */}
                                </div>

                                <hr className="mt-4 mb-2 border-gray-300 dark:border-gray-700"/>
                            </div>
                        )}


                        <div className="flex-grow mb-20">
                            <div className="p-4">
                                {Array.isArray(posts) && posts.map((post) => (
                                    <div key={post.board_id}
                                         className="rounded-xl bg-white p-4 border border-gray-100 dark:border-gray-800 mb-4 relative">
                                        <div className="flex justify-between items-start mb-2">
                                            <div className="flex items-center space-x-2">
                                                <img
                                                    alt="Avatar" className="rounded-full "
                                                    src={userInfo.img || "/placeholder.svg"}
                                                    style={{aspectRatio: "1 / 1", objectFit: "cover", zIndex: "0"}}
                                                    width="40"/>

                                                <div className="grid gap-1">
                                                    <Link to={`/UserPage/${post.id}`} key={post.id}
                                                          className="font-semibold">{post.nickname}</Link>
                                                    <div
                                                        className="text-xs text-gray-500 dark:text-gray-400">{post.date}</div>
                                                </div>
                                            </div>
                                            <DropdownMenu post={post}/>
                                        </div>
                                        {post.imgpath && <ImageSlider imgpath={post.imgpath}/>}
                                        {post.video && <video src={post.video} controls/>}
                                        <div className="line-clamp-3">
                                            <p>{post.content}</p>
                                        </div>
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
                                                }}>
                                                댓글
                                            </button>
                                            <button className="w-16 h-8" onClick={() => {
                                                setCurrentPost(post);
                                                setShowShareModal(true);
                                            }}>공유
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    <Comment isOpen={showModal} onClose={() => setShowModal(false)} boardId={currentComments}/>
                    <Share isOpen={showShareModal} onClose={() => setShowShareModal(false)} post={currentPost}/>
                    <EditProfile Open={editProfile} Close={() => setEditProfile(false)} userInfo={profile}/>

                    {showTopBtn && (
                        <button
                            className="fixed bottom-20 right-10 bg-white hover:bg-gray-100 text-gray-900 font-bold rounded-full h-12 w-12 flex justify-center items-center border-4 border-gray-900 cursor-pointer"
                            onClick={scrollToTop}
                            style={{
                                width: '50px',
                                height: '50px',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center'
                            }}
                        >
                            ▲
                        </button>
                    )}
                </div>
            </MobileView>
        </div>
    )
}

export default UserPage;