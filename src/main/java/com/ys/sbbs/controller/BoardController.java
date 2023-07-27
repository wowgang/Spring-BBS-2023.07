package com.ys.sbbs.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ys.sbbs.entity.Board;
import com.ys.sbbs.entity.Reply;
import com.ys.sbbs.service.BoardService;
import com.ys.sbbs.service.ReplyService;
import com.ys.sbbs.utility.JsonUtil;

@Controller
@RequestMapping("/board")

public class BoardController {
	// 타입이 인터페이스타입 /인터타입으로 구현한 객체는 autowired로 
	@Autowired private BoardService boardService;
	@Autowired private ReplyService replyService;
	
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	@GetMapping("/list")
	// html => href="/sbbs/board/list?p=${page}&f=${field}&q=${query}">${page}
	// 			http://localhost:8080/sbbs/board/list?p=1&f=&q=
	
	
	public String list(@RequestParam(name="p", defaultValue="1") int page, 
						@RequestParam(name="f", defaultValue="title") String field,
						@RequestParam(name="q", defaultValue="") String query,
						HttpSession session, Model model){
		List<Board> list = boardService.listBoard(field, query, page);
		
		int totalBoardCount = boardService.getBoardCount(field, query); 
		int totalPages = (int) Math.ceil(totalBoardCount / (double) BoardService.LIST_PER_PAGE);
		int startPage = (int) Math.ceil((page-0.5)/BoardService.PAGE_PER_SCREEN -1) * BoardService.PAGE_PER_SCREEN + 1;
		int endPage = Math.min(totalPages, startPage + BoardService.PAGE_PER_SCREEN - 1);
		List<String> pageList = new ArrayList<String>();
		for (int i = startPage; i <= endPage; i++)
			pageList.add(String.valueOf(i));
		
		session.setAttribute("currentBoardPage", page);
		model.addAttribute("boardList", list);
		model.addAttribute("field", field);
		model.addAttribute("query", query);
		model.addAttribute("today", LocalDate.now().toString());
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("pageList", pageList);
		return "board/list";
	}
	
	// html => href="/sbbs/board/detail/${board.bid}/${board.uid}">${board.title}
	// href="/sbbs/board/detail/15/james
	@GetMapping("/detail/{bid}/{uid}")
	public String detail(@PathVariable int bid, @PathVariable String uid, String option,
			HttpSession session, Model model) {
		// 본인이 조회한 경우 또는 댓글 작성후에는 조회수를 증가시키지 않음
		String sessionUid = (String) session.getAttribute("uid");
		if (!uid.equals(sessionUid) && (option==null || option.equals(""))  ) // sessionUid로그인 한사람 따라서 본인이 아니면
			boardService.increaseViewCount(bid);
		
		Board board = boardService.getBoard(bid);
		String jsonFiles = board.getFiles();
		if (!(jsonFiles == null || jsonFiles.equals(""))) {
			JsonUtil ju = new JsonUtil();
			List<String> fileList = ju.jsonToList(board.getFiles());
			model.addAttribute("fileList", fileList);
		}
		model.addAttribute("board", board);
		List<Reply> replyList = replyService.getReplyList(bid);
		model.addAttribute("replyList", replyList);
		return "board/detail";
	}
	
	@GetMapping("/write")
	public String writeForm() {
		return "board/write";
	}
	
	@PostMapping("/write")
	public String writeProc(MultipartHttpServletRequest req, HttpSession session) {
		String title = req.getParameter("title");
		String content = req.getParameter("content");
		List<MultipartFile> uploadfileList = req.getFiles("files");
		
		List<String> fileList = new ArrayList<>();
		
		for (MultipartFile part: uploadfileList) {
			if (part.getContentType().contains("octet-stream"))	// 첨부 파일이 없는 경우 application/octet-stream
				continue;
			String filename = part.getOriginalFilename();
			String uploadPath = uploadDir + "upload/" + filename;
			try {
				part.transferTo(new File(uploadPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileList.add(filename);
		}
		// filelist를 json으로 바꿔줘야함.
		JsonUtil ju = new JsonUtil();
		String files = ju.listToJson(fileList);
		
		String sessionUid = (String) session.getAttribute("uid");
		Board board = new Board(sessionUid, title, content, files);
		boardService.insertBoard(board);
		return "redirect:/board/list?p=1&f=&q=";
	}
	
	@GetMapping("/delete/{bid}")
	public String delete(@PathVariable int bid, Model model) {
		model.addAttribute("bid", bid);
		return "board/delete";
		
	}
	
	@GetMapping("/deleteConfirm/{bid}")
	public String deleteConfirm(@PathVariable int bid, HttpSession session) {
		boardService.deleteBoard(bid);
		return "redirect:/board/list?p=" + session.getAttribute("currentBoardPage") + "&f=&q=";
	}
	
	@GetMapping("/update/{bid}")
	public String update(@PathVariable int bid, HttpSession session, Model model) {
		Board board = boardService.getBoard(bid);
		model.addAttribute("board", board);
		JsonUtil ju = new JsonUtil();
		List<String> fileList = ju.jsonToList(board.getFiles());
		model.addAttribute("fileList", fileList);
		fileList = ju.jsonToList(board.getFiles());
		session.setAttribute("fileList", fileList);
		return "board/update";
	}
	
	@PostMapping("/update")
	public String updateProc(MultipartHttpServletRequest req, HttpSession session, Model model) {
		int bid = Integer.parseInt(req.getParameter("bid"));
		String title = req.getParameter("title");
		String content = req.getParameter("content");
		
		List<String> fileList = (List<String>) session.getAttribute("fileList");
		if (fileList != null && fileList.size() > 0) { 
			String[] delFiles =  req.getParameterValues("delFile");
			if (delFiles != null && delFiles.length > 0) {
				for (String delFile: delFiles) {
					fileList.remove(delFile);	// fileList에서 삭제
					File df = new File(boardService.UPLOAD_PATH + delFile);	// 실제 파일 삭제
					df.delete();
				}
			}
		} else {
			fileList = new ArrayList<String>();
		}
		
		List<Part> fileParts;
		try {
			fileParts = (List<Part>) req.getParts();
			for (Part part: fileParts) {
				String filename = part.getSubmittedFileName();
				if (filename == null || filename.equals(""))
					continue;
				part.write(boardService.UPLOAD_PATH + filename);
				fileList.add(filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		// filelist를 json으로 바꿔줘야함.
		JsonUtil ju = new JsonUtil();
		String files = ju.listToJson(fileList);
		
		
		Board board = new Board(bid, title, content, files);
		boardService.updateBoard(board);
		String sessionUid = (String) session.getAttribute("uid");
//		return "redirect:/board/list?p=1&f=&q=";
		return "redirect:/board/detail/" + bid + "/" + sessionUid;
//		return "redirect:/board/detail";
	}
	
}
