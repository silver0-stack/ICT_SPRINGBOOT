package org.myweb.first.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.board.model.dto.Reply;
import org.myweb.first.board.model.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    // 댓글, 대댓글 등록 페이지로 이동 처리용
    @RequestMapping("breplyform.do")
    public ModelAndView moveReplyPage(ModelAndView mv, @RequestParam("bnum") int boardNum,
                                      @RequestParam("page") int currentPage) {

        mv.addObject("bnum", boardNum);
        mv.addObject("currentPage", currentPage);
        mv.setViewName("board/boardReplyForm");

        return mv;
    }



    // 게시 댓글, 대댓글 등록 요청 처리용 (파일 업로드 기능 없음)
//    @RequestMapping(value = "breply.do", method = RequestMethod.POST)
//    public String replyInsertMethod(Reply reply, Model model, @RequestParam("bnum") int bnum,
//                                    @RequestParam("page") int page) {
//        // 1. 새로 등록할 댓글은 원글을 조회해 옴 또는 등록할 대댓글은 참조하는 댓글을 조회해 옴
//        Reply origin = replyService.selectBoard(bnum);
//
//        // 2. 새로 등록할 댓글 또는 대댓글의 레벨을 지정함
//        reply.setBoardLev(origin.getBoardLev() + 1);
//
//        // 3. 참조 원글 번호(boardRef) 지정함
//        reply.setBoardRef(origin.getBoardRef());
//
//        // 4. 새로 등록할 reply 이 대댓글(boardLev : 3)이면, 참조 댓글번호(boardReplyRef) 지정함
//        if (reply.getBoardLev() == 3) {
//            // 참조댓글번호 지정함
//            reply.setBoardReplyRef(origin.getBoardReplyRef());
//        }
//
//        // 5. 최근 등록 댓글 | 대댓글의 순번을 1로 지정함
//        reply.setBoardReplySeq(1);
//        // 6. 기존 같은 레벨 & 같은 원글|댓글에 기록된 글은 순번은 1증가시킴
//        boardService.updateReplySeq(reply);
//
//        if (boardService.insertReply(reply) > 0) {
//            return "redirect:blist.do?page=" + page;
//        } else {
//            model.addAttribute("message", bnum + "번 글에 대한 댓글 | 대댓글 등록 실패!");
//            return "common/error";
//        }
//    } // breply.do
//
//
//
//
//    // 댓글, 대댓글 수정 요청 처리용
//    @RequestMapping(value = "breplyupdate.do", method = RequestMethod.POST)
//    public String replyUpdateMethod(Board reply, @RequestParam("page") int currentPage, Model model) {
//
//        if (boardService.updateReply(reply) > 0) {
//            // 댓글, 대댓글 수정 성공시 다시 상세보기가 수정된 내용을 보여지게 처리
//            model.addAttribute("bnum", reply.getBoardNum());
//            model.addAttribute("page", currentPage);
//
//            return "redirect:bdetail.do";
//        } else {
//            model.addAttribute("message", reply.getBoardNum() + "번 글 수정 실패!");
//            return "common/error";
//        }
//    }

}
