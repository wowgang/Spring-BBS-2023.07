package com.ys.sbbs.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ys.sbbs.entity.User;

@Mapper
public interface UserDaoOracle {

	@Select("select count(uname) from users where isDeleted=0")
	int getUserCount();
	
	
	@Select("select * from users where \"uid\"=#{uid}")
	User getUser(String uid);

//	@Select("select * from users where isDeleted=0 order by regDate desc, \"uid\""
//			+ " limit 10 offset #{offset}") // 이와 같이 하면 오류가 나서 아래와같이 코드를 바꿔줌
	// Select 실행 순서
		// FROM - WHERE - GROUP BY - HAVING - SELECT COLUMN - ORDER BY
	
	@Select("select * from ("
			+ "	  select rownum as rnum, a.* from ("
			+ " 	select * from users"
			+ "    	where isDeleted=0 order by regDate desc, \"uid\") a"
			+ "	  where rownum <= #{maxrow})"
			+ " where rnum > #{offset}")
	List<User> getUserList(int maxrow, int offset);
	
	// ORA-01400: NULL을 안에 삽입할 수 없습니다
	// jdbcType=VARCHAR SQL 로 넘어간 parameter 에 null 이 셋팅됨.
	/*
	 * 방법 :
   		1) Cause 를 따라 ServiceImpl 에서 실행한 SQL 문의 파라미터 디버깅. 
   		2) Insert 문이었는데, 다른 SQL 문 살펴보니 varchar 일 경우 JdbcType 을 지정해주고 있음.  
    	해결 : 파라미터가 varchar 일 경우 #{name, jdbcType=VARCHAR} 추가 
	 * 
	 */
	
	@Insert("insert into users values (#{uid}, #{pwd}, #{uname}, #{email},"
			+ " default, default, #{profile, jdbcType=VARCHAR}, #{addr, jdbcType=VARCHAR})")
	void insertUser(User user);
	
	@Update("update users set pwd=#{pwd},"
			+ " uname=#{uname},email=#{email},\"profile\"=#{profile, jdbcType=VARCHAR},addr=#{addr, jdbcType=VARCHAR}"
			+ " where \"uid\"=#{uid}")
//	@Update("update users set pwd=#{pwd}, uname=#{uname}, email=#{email},"
//			+ " \"profile\"=#{profile}, addr=#{addr} where \"uid\"=#{uid}")
	void updateUser(String pwd, String uname, String email, String profile, String addr, String uid);
	
	@Update("update users set isDeleted=1 where \"uid\"=#{uid}")
	void deleteUser(String uid);
}
