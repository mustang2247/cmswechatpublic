package com.open.coinnews.web.controller.web;

import com.open.coinnews.app.dto.CateDto;
import com.open.coinnews.app.model.Article;
import com.open.coinnews.app.model.Comment;
import com.open.coinnews.app.model.Notice;
import com.open.coinnews.app.model.Tag;
import com.open.coinnews.app.service.*;
import com.open.coinnews.basic.tools.BaseSpecification;
import com.open.coinnews.basic.tools.PageableTools;
import com.open.coinnews.basic.tools.SearchCriteria;
import com.open.coinnews.basic.tools.SortTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共的Controller
 */
@RestController
@RequestMapping(value = "public")
public class PublicController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    public ICommentService commentService;

    /** 获取标签 */
    @RequestMapping(value = "listTags", method = RequestMethod.GET)
    public List<Tag> listTags() {
        return tagService.findAll();
    }

    /** 栏目列表 */
    @RequestMapping(value = "listCates", method = RequestMethod.GET)
    public List<CateDto> listCates() {
        return articleService.queryCates();
    }

    /** 获取访问最最高的几条文章 */
    @RequestMapping(value = "hotArt", method = RequestMethod.GET)
    public Page<Article> hotArt(Integer length) {
        length = (length ==null || length<=0)?10:length; //默认为10

        Specifications<Article> spe = Specifications.where(new BaseSpecification<>(new SearchCriteria("isShow", BaseSpecification.EQUAL, 1)));
        Page<Article> artList = articleService.findAll(spe, PageableTools.basicPage(0, length, "desc", "readCount"));
        return artList;
    }

    /** 最新文章-获取最新发布的几条文章 */
    @RequestMapping(value = "newArt", method = RequestMethod.GET)
    public Page<Article> newArt(Integer length) {
        length = (length ==null || length<=0)?10:length; //默认为10

        Specifications<Article> spe = Specifications.where(new BaseSpecification<>(new SearchCriteria("isShow", BaseSpecification.EQUAL, 1)));
        Page<Article> artList = articleService.findAll(spe, PageableTools.basicPage(0, length, "desc", "createDate"));
        return artList;
    }

    /** 获取公告信息 */
    @RequestMapping(value = "listNotice", method = RequestMethod.GET)
    public List<Notice> listNotice() {
        Specifications<Notice> pars = Specifications.where(new BaseSpecification<Notice>(new SearchCriteria("isShow", "eq", 1)));
        List<Notice> res = noticeService.findAll(pars, SortTools.basicSort("asc", "orderNo"));
        return res;
    }

    /** 显示的文章条数 */
    @RequestMapping(value = "articleCount", method = RequestMethod.GET)
    public Long articleCount() {
        return articleService.queryCount();
    }

    /** 文章阅读总数 */
    @RequestMapping(value = "articleReadCount", method = RequestMethod.GET)
    public Long articleReadCount() {
        return articleService.queryReadCount();
    }

    /** 获取最新的几条点评 */
    @RequestMapping(value = "newComment", method = RequestMethod.GET)
    public Page<Comment> newComment(Integer length) {
        length = (length ==null || length<=0)?10:length; //默认为10
        Page<Comment> commentList = commentService.findAll(PageableTools.basicPage(0, length, "desc", "createDate"));
        return commentList;
    }
}
