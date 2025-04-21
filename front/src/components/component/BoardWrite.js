import {useNavigate, useLocation} from 'react-router-dom';
import styled from 'styled-components';
import {Button} from "./ui/button";
import api from "../../api";
import {CardTitle, CardHeader, CardContent, CardFooter, Card} from "./ui/card";
import React, {useState, useEffect} from 'react';
import Modal from 'react-modal';
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';

Modal.setAppElement('#root');

const StyledTextarea = styled.textarea`
  border: 1px solid #ccc;
  padding: 8px;
  font-size: 16px;
  border-radius: 8px;
  resize: none;
  width: 100%;
  height: 260px;
  overflow-y: auto;
`;

const SortableItem = SortableElement(({value, onRemove}) => (
    <div className="relative inline-block mr-4 mt-4">
        <img src={value.url} alt={`미리보기 ${value.name}`} className="w-24 h-24 rounded-md object-cover"/>
        <button
            onClick={onRemove}
            className="absolute top-0 right-0 bg-red-600 text-white rounded-full p-1 text-xs"
        >
            &times;
        </button>
    </div>
));

const SortableList = SortableContainer(({items, onRemove}) => {
    return (
        <div>
            {items.map((value, index) => (
                <SortableItem key={`item-${value.name}`} index={index} value={value} onRemove={() => onRemove(index)}/>
            ))}
        </div>
    );
});

function BoardWrite({isOpen, onClose, onRequestClose, post}) {
    const navigate = useNavigate();
    const location = useLocation();
    const [content, setContent] = useState('');
    const [images, setImages] = useState([]);
    const [mergedImages, setMergedImages] = useState([]);
    const fileInputRef = React.useRef(null);
    const fromPath = location.state?.from || '/';

    useEffect(() => {
        if (post) {
            setContent(post.content || '');
            const existingImages = post.imgpath ? post.imgpath.split('|').map((url, index) => ({
                url,
                name: `existing-${index}`,
                isExisting: true
            })) : [];
            setMergedImages(existingImages);
        } else {
            setContent('');
        }
    }, [post]);

    const handleFileChange = (event) => {
        const files = Array.from(event.target.files);
        const newImagePreviews = files.map(file => ({
            url: URL.createObjectURL(file),
            name: file.name,
            file,
            isExisting: false,
        }));
        setImages(prevImages => [...prevImages, ...files]);
        setMergedImages(prevMergedImages => [...prevMergedImages, ...newImagePreviews]);
    };

    const handleRemoveImage = (index) => {
        const newMergedImages = [...mergedImages];
        if (!newMergedImages[index].isExisting) {
            const newImages = [...images];
            newImages.splice(newImages.findIndex(img => img.name === newMergedImages[index].name), 1);
            setImages(newImages);
        }
        newMergedImages.splice(index, 1);
        setMergedImages(newMergedImages);
    };

    const onSortEnd = ({oldIndex, newIndex}) => {
        setMergedImages(arrayMove(mergedImages, oldIndex, newIndex));
    };

    const displayFileInfo = () => {
        return (
            <SortableList items={mergedImages} onSortEnd={onSortEnd} onRemove={handleRemoveImage} axis="xy"/>
        );
    };

    const handleContentChange = (event) => {
        setContent(event.target.value);
    };

    const handleClose = () => {
        onClose();
    };

    const Write = async () => {
        const formData = new FormData();

        formData.append('content', content);
        formData.append('nickname', localStorage.getItem("nickname"));

        mergedImages.forEach((image, index) => {
            if (!image.isExisting) {
                formData.append(`img`, image.file);
            }
        });

        try {
            if (post) {
                formData.append('board_id', post.board_id);
                const existingImagePaths = mergedImages.filter(img => img.isExisting).map(img => img.url).join('|');
                formData.append('imgpath', existingImagePaths);
                if (content.length === 0 && mergedImages.length === 0) {
                    alert("내용을 입력해주세요");
                    return;
                }
                await api.post(`/boards/boardUpdate`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                    withCredentials: true,
                });
                alert('게시물이 수정되었습니다.');
                navigate("/");
            } else {
                if (content.length === 0 && mergedImages.length === 0) {
                    alert("내용을 입력해주세요");
                    return;
                }
                await api.post('/boards/boardWrite', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                    withCredentials: true,
                });
                alert('게시물이 업로드 되었습니다.');
            }
            navigate(fromPath);
        } catch (error) {
            console.log(error);
            alert('글쓰기에 실패했습니다. 다시 시도해주세요.');
        }
    };

    const handleButtonClick = () => {
        fileInputRef.current.click();
    };

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={() => {
                onRequestClose();
                onClose();
            }}
            className="fixed inset-0 bg-opacity-50 flex justify-center items-center z-10"
            overlayClassName="fixed inset-0 bg-black bg-opacity-5 "
            style={{zIndex: 12}}
        >
            <Card className="w-[90vw] max-w-2xl h-[50vh] min-h-[620px] mx-auto relative" style={{zIndex: 50}}>
                <CardHeader>
                    <CardTitle className="text-xl">
                        {post ? '게시글 수정하기' : '새 게시물 만들기'}
                        <button onClick={handleClose}
                                className="absolute top-2 mr-2 right-2 text-2xl font-bold text-gray-800">&times;</button>
                    </CardTitle>
                </CardHeader>
                <CardContent className="grid gap-4">
                    <StyledTextarea className="flex-1" placeholder="게시물 내용을 작성해주세요." value={content}
                                    onChange={handleContentChange}/>
                    <div className="flex flex-col gap-2">
                        <input type="file" name="img" id="img" ref={fileInputRef} onChange={handleFileChange} multiple
                               style={{display: 'none'}}/>
                        <Button size="sm" onClick={handleButtonClick}>
                            <UploadIcon className="h-4 w-4 mr-2"/>
                            Add images
                        </Button>
                        {displayFileInfo()}
                    </div>
                    <div className="grid gap-4">
                        <Card className="rounded-none shadow-none border-0">
                        </Card>
                    </div>
                </CardContent>
                <CardFooter>
                    <Button onClick={Write}>{post ? '수정하기' : '업로드'}</Button>
                </CardFooter>
            </Card>
        </Modal>
    );
}

function UploadIcon(props) {
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
            strokeLinejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17 8 12 3 7 8"/>
            <line x1="12" x2="12" y1="3" y2="15"/>
        </svg>
    );
}

export default BoardWrite;
