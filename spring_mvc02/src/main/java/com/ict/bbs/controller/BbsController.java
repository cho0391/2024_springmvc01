package com.ict.bbs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ict.bbs.service.BbsService;
import com.ict.bbs.vo.BbsVO;
import com.ict.bbs.vo.CommVO;
import com.ict.common.Paging;

@Controller
public class BbsController {

	@Autowired
	private BbsService bbsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private Paging paging;

	@RequestMapping("/bbs")
	public ModelAndView getBbsList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("bbs/list");

		// 페이지 기법 이전(GuestBook)

		/*
		 * List<BbsVO> list = bbsService.getBbsList(); if (list != null) {
		 * mv.addObject("list", list); return mv; } return new
		 * ModelAndView("bbs/error");
		 */

		// 페이지 기법 이후()

		// 1. 전체 게시물의 수를 구한다
		int count = bbsService.getTotalCount();
		// 확인
		// System.out.println("count : " + count);

		// 페이징에 총 게시물수를 넣어준다.
		paging.setTotalRecord(count);

		// 2. 1.을 이용해 전체 페이지의 수를 구한다
		// 총 게시물 수가 NumPerPage 보다 작을 경우는 무조건 1페이지 (추가로 넘어갈 페이지가 없다는 말)
		if (paging.getTotalRecord() <= paging.getNumPerPage()) {
			paging.setTotalPage(1);
		} else {
			paging.setTotalPage(paging.getTotalRecord() / paging.getNumPerPage());
			if (paging.getTotalRecord() % paging.getNumPerPage() != 0) {
				paging.setTotalPage(paging.getTotalPage() + 1);
			}
		}

		// 3. 파라미터에서 넘어오는 cPage(내가 보고싶은 페이지 번호)를 구하자
		String cPage = request.getParameter("cPage");

		// 만약에 cPage가 null 이면 무조건 1page 이다.
		if (cPage == null) {
			paging.setNowPage(1);
		} else {
			paging.setNowPage(Integer.parseInt(cPage));
		}

		// 4. cPage를 기준으로 begin, end, beginBlock, endBlock 을 구하자
		// 오라클인 경우 begin과 end를 구해야한다.
		// MySQL 이나 MariaDB 는 limit, offset 을 사용한다.
		// 공식) offset = limit * (현재페이지 -1)
		// limit = numPerPage
		// SELECT * FROM bbs_t order by b_idx desc limit 6 offset 0 | 1 | 3 ;

		paging.setOffset(paging.getNumPerPage() * (paging.getNowPage() - 1));

		// 시작블록과 끝블록 구하기
		paging.setBeginBlock(
				(int) (((paging.getNowPage() - 1) / paging.getPagePerBlock()) * paging.getPagePerBlock() + 1));

		paging.setEndBlock(paging.getBeginBlock() + paging.getPagePerBlock() - 1);

		// 주의사항
		// endBlock(3, 6, 9...) 실제 가지고 있는 총 페이지는 20개일 경우 4페이지 까지
		if (paging.getEndBlock() > paging.getTotalPage()) {
			paging.setEndBlock(paging.getTotalPage());
		}

		// DB 갔다오기
		List<BbsVO> list = bbsService.getBbsList(paging.getOffset(), paging.getNumPerPage());
		mv.addObject("list", list);
		mv.addObject("paging", paging);
		return mv;
	}

	@GetMapping("/bbs_write")
	public ModelAndView getBbsWrite() {
		ModelAndView mv = new ModelAndView("bbs/write");
		return mv;
	}

	@PostMapping("/bbs_write_ok")
	public ModelAndView getBbsWriteOk(BbsVO bvo, HttpServletRequest request) {
		try {

			ModelAndView mv = new ModelAndView("redirect:/bbs");

			String path = request.getSession().getServletContext().getRealPath("/resources/upload");
			MultipartFile file = bvo.getFile_name();

			if (file.isEmpty()) {
				bvo.setF_name("");
			} else {

				UUID uuid = UUID.randomUUID();
				String f_name = uuid.toString() + "_" + file.getOriginalFilename();
				bvo.setF_name(f_name);

				// 업로드
				file.transferTo(new File(path, f_name));
			}

			// 비밀번호 암호화
			String pwd = passwordEncoder.encode(bvo.getPwd());
			bvo.setPwd(pwd);

			int result = bbsService.getBbsInsert(bvo);
			if (result > 0) {
				return mv;
			}
			return new ModelAndView("bbs/error");

		} catch (Exception e) {
			System.out.println(e);
			return new ModelAndView("bbs/error");
		}
	}

	@GetMapping("/bbs_detail")
	public ModelAndView getBbsDetail(@RequestParam("b_idx") String b_idx, @ModelAttribute("cPage") String cPage) {
		ModelAndView mv = new ModelAndView("bbs/detail");

		// 조회수 증가
		int result = bbsService.getHitUpdate(b_idx);

		// 상세보기
		BbsVO bvo = bbsService.getBbsDetail(b_idx);

		// 댓글 리스트 가져오기 (원글과 관련된)
		List<CommVO> clist = bbsService.getCommentList(b_idx);

		mv.addObject("bvo", bvo);
		mv.addObject("clist", clist);

		return mv;
	}

	@GetMapping("/bbs_down")
	public void bbsDown(HttpServletRequest request, HttpServletResponse response) {
		try {
			String f_name = request.getParameter("f_name");
			String path = request.getSession().getServletContext().getRealPath("/resources/upload/" + f_name);
			String r_path = URLEncoder.encode(path, "UTF-8");
			// 브라우저 설정
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=" + r_path);

			// 실제 가져오기
			File file = new File(new String(path.getBytes(), "UTF-8"));
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();

			FileCopyUtils.copy(in, out);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@PostMapping("/comment_insert")
	public ModelAndView getCommentInsert(CommVO cvo, @ModelAttribute("b_idx") String b_idx,
			@ModelAttribute("cPage") String cPage) {
		ModelAndView mv = new ModelAndView("redirect:/bbs_detail");
		int result = bbsService.getCommentInsert(cvo);
		return mv;
	}

	@PostMapping("/comment_delete")
	public ModelAndView getCommentDelete(@RequestParam("c_idx") String c_idx, @ModelAttribute("b_idx") String b_idx,
			@ModelAttribute("cPage") String cPage) {
		ModelAndView mv = new ModelAndView("redirect:/bbs_detail");
		int result = bbsService.getCommentDelete(c_idx);
		return mv;
	}

	@PostMapping("/bbs_delete")
	public ModelAndView getBbsDelete(@ModelAttribute("b_idx") String b_idx, @ModelAttribute("cPage") String cPage) {
		return new ModelAndView("bbs/delete");
	}

	@PostMapping("/bbs_delete_ok")
	public ModelAndView getBbsDeleteOk(@RequestParam("pwd") String pwd, @ModelAttribute("b_idx") String b_idx,
			@ModelAttribute("cPage") String cPage) {
		ModelAndView mv = new ModelAndView();

		// 비밀번호 체크
		BbsVO bvo = bbsService.getBbsDetail(b_idx);
		String dbpwd = bvo.getPwd();
		if (passwordEncoder.matches(pwd, dbpwd)) {
			// 원글 삭제
			// active 컬럼을 0 -> 1 로 변경하자.
			int result = bbsService.getBbsDelete(b_idx);
			if (result > 0) {
				mv.setViewName("redirect:/bbs");
				return mv;
			}
		} else {
			// 비밀번호가 틀리다.
			mv.setViewName("bbs/delete");
			mv.addObject("pwdchk", "fail");
			return mv;
		}

		return new ModelAndView("bbs/error");
	}

	@PostMapping("/bbs_update")
	public ModelAndView getBbsUpdate(@ModelAttribute("b_idx") String b_idx, @ModelAttribute("cPage") String cPage) {
		ModelAndView mv = new ModelAndView("bbs/update");
		// DB 에서 b_idx를 이용해서 정보 가져오기
		BbsVO bvo = bbsService.getBbsDetail(b_idx);
		if (bvo != null) {
			mv.addObject("bvo", bvo);
			return mv;
		}

		return null;
	}

	@PostMapping("/bbs_update_ok")
	public ModelAndView getBbsUpdateOK(BbsVO bvo, HttpServletRequest request, 
			@ModelAttribute("cPage") String cPage,
			@ModelAttribute("b_idx") String b_idx) {

		ModelAndView mv = new ModelAndView();

		// 비밀번호 체크
		BbsVO bvo2 = bbsService.getBbsDetail(b_idx);
		String dbpwd = bvo2.getPwd();
		if (passwordEncoder.matches(bvo.getPwd(), dbpwd)) {
			// 원글 수정
			try {
				String path = request.getSession().getServletContext().getRealPath("/resources/upload");
				MultipartFile file = bvo.getFile_name();
				String old_f_name = bvo.getOld_f_name();
				
				if(file.isEmpty()) {
					bvo.setF_name(old_f_name);
				}else {
					UUID uuid = UUID.randomUUID();
					String f_name = uuid.toString()+"_"+file.getOriginalFilename();
					bvo.setF_name(f_name);
					
					// 실제업로드
					file.transferTo(new File(path, f_name));
				}
				
				int result = bbsService.getBbsUpdate(bvo);
				if(result > 0) {
					mv.setViewName("redirect:/bbs_detail");
					return mv;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			
		} else {
			// 비밀번호가 틀리다.
			mv.setViewName("bbs/update");
			mv.addObject("pwdchk", "fail");
			mv.addObject("bvo", bvo);
			return mv;
		}

		return new ModelAndView("bbs/error");

	}
}
	