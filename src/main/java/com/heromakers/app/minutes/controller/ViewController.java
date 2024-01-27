package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.model.*;
import com.heromakers.app.minutes.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@Controller
@Slf4j
@ApiIgnore
public class ViewController {
    @Autowired
    private CodeService codeService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private TalkService talkService;

    @GetMapping("/admin")
    public String adminIndex(Model model) {
        return "redirect:/admin/notice";
    }

    @GetMapping("/admin/login")
    public String login(Model model) {
        model.addAttribute("menuTitle", "로그인");
        return "login";
    }

    // Code
    @GetMapping("/admin/code")
    public String codeList(Model model) {
        model.addAttribute("menuTitle", "코드 관리");
        return "code/list";
    }

    @GetMapping("/admin/code/add")
    public String addCode(Model model) {
        model.addAttribute("menuTitle", "코드 추가");
        model.addAttribute("pageType", "insert");
        return "code/edit";
    }

    @GetMapping("/admin/code/{codeId}")
    public String editCode(Model model, @PathVariable Integer codeId) {
        model.addAttribute("menuTitle", "코드 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("codeId", codeId);
        return "code/edit";
    }

    // Account
    @GetMapping("/admin/account")
    public String accountList(Model model) {
        model.addAttribute("menuTitle", "계정 관리");
        return "account/list";
    }

    @GetMapping("/admin/account/add")
    public String addAccount(Model model) {
        model.addAttribute("menuTitle", "계정 추가");
        model.addAttribute("pageType", "insert");
        return "account/edit";
    }

    @GetMapping("/admin/account/{accountId}")
    public String editAccount(Model model, @PathVariable Integer accountId) {
        model.addAttribute("menuTitle", "계정 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("accountId", accountId);
        return "account/edit";
    }

    // Notice
    @GetMapping("/admin/notice")
    public String noticeList(Model model) {
        model.addAttribute("menuTitle", "게시판 관리");
        model.addAttribute("noticeKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "NOTICE_KIND")));
        return "notice/list";
    }

    @GetMapping("/admin/notice/add")
    public String addNotice(Model model) {
        model.addAttribute("menuTitle", "게시판 추가");
        model.addAttribute("pageType", "insert");
        model.addAttribute("noticeKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "NOTICE_KIND")));
        model.addAttribute("noticeModel", new NoticeModel());
        return "notice/edit";
    }

    @GetMapping("/admin/notice/{noticeId}")
    public String editNotice(Model model, @PathVariable Integer noticeId) {
        model.addAttribute("menuTitle", "게시판 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("noticeKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "NOTICE_KIND")));
        model.addAttribute("noticeModel", noticeService.getNoticeInfo(noticeId));
        model.addAttribute("noticeId", noticeId);
        return "notice/edit";
    }

    // InfoPopup
    @GetMapping("/admin/popup")
    public String popupList(Model model) {
        model.addAttribute("menuTitle", "알림팝업 관리");
        return "popup/list";
    }

    @GetMapping("/admin/popup/add")
    public String addPopup(Model model) {
        model.addAttribute("menuTitle", "알림팝업 추가");
        model.addAttribute("pageType", "insert");
        return "popup/edit";
    }

    @GetMapping("/admin/popup/{seq}")
    public String editPopup(Model model, @PathVariable Integer seq) {
        model.addAttribute("menuTitle", "알림팝업 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("seq", seq);
        return "popup/edit";
    }

    // Talk
    @GetMapping("/admin/talk")
    public String talkList(Model model) {
        model.addAttribute("menuTitle", "사용자 작성글 관리");
        model.addAttribute("talkKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "TALK_KIND")));
        return "talk/list";
    }

    @GetMapping("/admin/talk/add")
    public String addTalk(Model model) {
        model.addAttribute("menuTitle", "사용자 작성글 추가");
        model.addAttribute("pageType", "insert");
        model.addAttribute("talkKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "TALK_KIND")));
        TalkModel talkModel = new TalkModel();
        talkModel.setWriter(new AccountModel());
        model.addAttribute("talkModel", talkModel);
        return "talk/edit";
    }

    @GetMapping("/admin/talk/{talkId}")
    public String editTalk(Model model, @PathVariable Integer talkId) {
        model.addAttribute("menuTitle", "사용자 작성글 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("talkKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "TALK_KIND")));
        model.addAttribute("talkModel", talkService.getTalkInfo(talkId));
        model.addAttribute("talkId", talkId);
        return "talk/edit";
    }

    // Comment
    @GetMapping("/admin/comment")
    public String commentList(Model model) {
        model.addAttribute("menuTitle", "댓글 관리");
        model.addAttribute("commentKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "COMMENT_PARENT")));
        return "comment/list";
    }

    @GetMapping("/admin/comment/add")
    public String addComment(Model model) {
        model.addAttribute("menuTitle", "댓글 추가");
        model.addAttribute("pageType", "insert");
        model.addAttribute("commentKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "COMMENT_PARENT")));
        return "comment/edit";
    }

    @GetMapping("/admin/comment/{commentId}")
    public String editComment(Model model, @PathVariable Integer commentId) {
        model.addAttribute("menuTitle", "댓글 편집");
        model.addAttribute("pageType", "update");
        model.addAttribute("commentKinds", codeService.getCodeList(Map.of("useFlag", 1, "parentCode", "COMMENT_PARENT")));
        model.addAttribute("commentId", commentId);
        return "comment/edit";
    }

}



