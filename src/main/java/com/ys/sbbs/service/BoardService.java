package com.ys.sbbs.service;

import java.util.List;

import com.ys.sbbs.entity.Board;
//controller과 boardDao에서 사용했던것 interface로 만들기
public interface BoardService {
	public static final int LIST_PER_PAGE = 10; // 한 페이지당 글 목록의 개수
	public static final int PAGE_PER_SCREEN = 10; // 한 화면에 표시되는 페이지 개수
	public static final String UPLOAD_PATH ="c:/Temp/upload/";
	
	Board getBoard(int bid);
	
	int getBoardCount(String field, String query);

	List<Board> listBoard(String field, String query, int page);
	
	void insertBoard(Board board);
	
	void updateBoard(Board board);
	
	void deleteBoard(int bid);

	void increaseViewCount(int bid);

	void increaseReplyCount(int bid);
	 
	
}
