package com.example.bob_friend_android.model

data class Comment (
        private var commentId: Long,
        private var boardId: Long,
        var profileImg: String? = "",
        var userName: String = "",
        var content: String = "",
        var timestamp: String = "",
        private var report: Long? = 0,
        private var recomment: Long? = 0, //대댓글일시, 댓글 아이디를 가지고 있음 -> null이면 댓글, 값이 있다면 대댓글
        var typeFlag: Int
){
    companion object {
        const val COMMENT_TYPE = 0
        const val RECOMMENT_TYPE = 1
    }

    override fun toString(): String {
        return "Comment(commentId=$commentId, boardId=$boardId, profileImg=$profileImg, userName=$userName, content=$content, timestamp=$timestamp, report=$report, recomment=$recomment, typeFlag=$typeFlag)"
    }
}