package com.example.bob_friend_android.model

data class Comment (
        var id: Long? = 0,
//        var boardId: Long,
//        var profileImg: String? = "",
        var author: User?,
        var content: String? = "",
        var replies: List<Comment>? = null, //대댓글일시, 댓글 아이디를 가지고 있음 -> null이면 댓글, 값이 있다면 대댓글
        private var report: Long? = 0,
        var createdAt: String? = "",
        var typeFlag: Int? = 0
)
{
    companion object {
        const val COMMENT_TYPE = 0
        const val RECOMMENT_TYPE = 1
    }

    override fun toString(): String {
        return "Comment(commentId=$id," +
//                " boardId=$boardId," +
//                " profileImg=$profileImg," +
//                " userName=$userName" +
                ", content=$content, createdAt=$createdAt, report=$report, recomment=$replies" +
//                ", typeFlag=$typeFlag" +
                ")"
    }
}