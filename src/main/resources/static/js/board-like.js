function handleLike(boardId, isLiked) {
    if (isLiked) {
        const confirmCancel = confirm("해당 게시글의 좋아요를 취소 하시겠습니까?");
        if (!confirmCancel) return false;
    }

    document.getElementById('like-form-' + boardId).submit();
    return false; // 링크 이동 막기
}

function handleDeleteReply(replyId) {
    const confirmed = confirm("댓글을 삭제하시겠습니까?");
    if (confirmed) {
        document.getElementById('delete-reply-form-' + replyId).submit();
    }
    return false;
}

function handleReplyLike(replyId, isLiked) {
    if (isLiked) {
        const confirmCancel = confirm("해당 댓글의 좋아요를 취소 하시겠습니까?");
        if (!confirmCancel) return false;
    }
    document.getElementById('reply-like-form-' + replyId).submit();
    return false; // a 태그의 기본 이동 막기
}