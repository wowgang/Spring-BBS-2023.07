package com.ys.sbbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.sbbs.dao.ReplyDaoMySQL;
import com.ys.sbbs.entity.Reply;

// add unimplemented  methods
//@Service
public class ReplyServiceMySQLImpl implements ReplyServiceMySQL{
	@Autowired private ReplyDaoMySQL replyDao;
	
	@Override
	public List<Reply> getReplyList(int bid) {
		List<Reply> list = replyDao.getReplyList(bid);
		return list;
	}

	@Override
	public void insertReply(Reply reply) {
		replyDao.insertReply(reply);
		
	}

	@Override
	public void deleteReply(String rid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConfirm(String rid) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void increaseReplyCount(int bid) {
//		replyDao.insertReply(null);
//		
//	}

}
