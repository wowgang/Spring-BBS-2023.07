package com.ys.sbbs.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ys.sbbs.entity.Board;

//entity BoardService 인터페이스 만든 다음 mapper
// 나중에 oracle로 바꿔치기 가능하기 때문에 3개로 분리함
// @ org로 import
@Mapper
public interface BoardDaoMySQL {
	@Select("SELECT b.bid, b.uid, b.title, b.content, b.modTime, b.viewCount,"
			+ "	b.replyCount, b.files, u.uname "
			+ "	FROM board AS b JOIN users AS u ON b.uid=u.uid WHERE b.bid=#{bid}")
	Board getBoard(int bid);
	
	@Select("select count(bid) from board where isDeleted=0 AND ${field} LIKE #{query}")
	int getBoardCount(String field, String query);
	
	@Select("SELECT b.bid, b.uid, b.title, b.modTime, b.viewCount, b.replyCount, u.uname FROM board AS b "
			+ "	JOIN users AS u ON b.uid=u.uid " // + " 이부분 바로다음 블랭크 있어야함 JOIN앞에 블랭크필요 조심
			+ " WHERE b.isDeleted=0  AND ${field} LIKE #{query}"
			+ "	ORDER BY modTime DESC"
			+ "	LIMIT 10 OFFSET #{offset}")
	List<Board> listBoard(String field, String query, int offset); // MySQL에서는 page = offset  
	
	@Insert("insert into board values(default, #{uid}, #{title}, #{content},"
			+ " default, default, default, default, #{files})")
	void insertBoard(Board board);
	// ibatis가 getter에 가서 board에 있는것 가져옴
	
	@Update("update board set title=#{title}, content=#{content}, modTime=now(),"
			+ " files=#{files} where bid=#{bid}")
	void updateBoard(Board board);
	
	@Update("update board set isDeleted=1 where bid=#{bid}")
	void deleteBoard(int bid);
	
	// view,reply Count
	@Update("update board set ${field}=${field}+1 where bid=#{bid}")
	void increaseCount(String field, int bid);
	
	
	
	
}
