package com.ys.sbbs.service;

import java.util.List;

import com.ys.sbbs.entity.Reply;

public interface ReplyServiceMySQL {
	List<Reply> getReplyList(int bid);
	
	void insertReply(Reply reply);
	
	void deleteReply(String rid);	// mysql
	
	void deleteConfirm(String rid);	// mysql
	
}
