package com.ssafy.daero.sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.daero.sns.service.SnsService;
import com.ssafy.daero.sns.vo.ArticleListVo;
import com.ssafy.daero.sns.vo.ReplyVo;
import com.ssafy.daero.user.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/sns")
public class SnsController {
    private final String SUCCESS = "SUCCESS";
    private final String FAILURE = "FAILURE";
    private final SnsService snsService;
    private final JwtService jwtService;

    public SnsController(SnsService snsService, JwtService jwtService) {
        this.snsService = snsService;
        this.jwtService = jwtService;
    }

    @GetMapping("/article/{articleSeq}")
    public ResponseEntity<Map<String, Object>> articleDetail(@RequestHeader("jwt") String jwt, @PathVariable int articleSeq) throws JsonProcessingException {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if(Objects.equals(currentUser.get("user_seq"), "null")) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); }

        Map<String, Object> res = snsService.articleDetail(articleSeq, Integer.parseInt(currentUser.get("user_seq")));
        if (res.isEmpty()) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
        else {return new ResponseEntity<>(res, HttpStatus.ACCEPTED); }
    }

    @DeleteMapping("/article/{article_seq}")
    public ResponseEntity<String> deleteArticle(@RequestHeader("jwt") String jwt, @PathVariable int article_seq) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if(Objects.equals(currentUser.get("user_seq"), "null")) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }

        int res = snsService.deleteArticle(article_seq, Integer.parseInt(currentUser.get("user_seq")));
        if (res == 0) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else if (res == 1) { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
        else {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        }
    }

    @GetMapping("/article/{article_seq}/reply")
    public ResponseEntity<Map<String, Object>> replyList(@PathVariable int article_seq, @RequestParam(defaultValue = "1") String page) {
        Map<String, Object> res = snsService.replyList(article_seq, page);
        if (res == null) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
        else if (res.size() == 0) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        else { return new ResponseEntity<>(res, HttpStatus.OK); }
    }

    @PostMapping("/article/{article_seq}/reply")
    public ResponseEntity<String> createReply(@RequestHeader("jwt") String jwt, @PathVariable int article_seq, @RequestBody Map<String, String> req) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        ReplyVo replyVo = snsService.createReply(article_seq, Integer.parseInt(currentUser.get("user_seq")), req.get("content"));
        if (replyVo.getResult() == ReplyVo.ReplyResult.NO_SUCH_ARTICLE) { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
        else { return new ResponseEntity<>(SUCCESS, HttpStatus.CREATED); }

    }

    @PutMapping("/reply/{reply_seq}")
    public ResponseEntity<String> updateReply(@RequestHeader("jwt") String jwt, @PathVariable int reply_seq, @RequestBody Map<String, String> req) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        ReplyVo replyVo = snsService.updateReply(Integer.parseInt(currentUser.get("user_seq")), reply_seq, req.get("content"));
        if (replyVo.getResult() == ReplyVo.ReplyResult.NO_SUCH_REPLY) { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
        else if (replyVo.getResult() == ReplyVo.ReplyResult.UNAUTHORIZED ) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
    }

    @DeleteMapping("/reply/{reply_seq}")
    public ResponseEntity<String> deleteReply(@RequestHeader("jwt") String jwt, @PathVariable int reply_seq) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        ReplyVo replyVo = snsService.deleteReply(Integer.parseInt(currentUser.get("user_seq")), reply_seq);
        if (replyVo.getResult() == ReplyVo.ReplyResult.NO_SUCH_REPLY) { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
        else if (replyVo.getResult() == ReplyVo.ReplyResult.UNAUTHORIZED ) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
    }

    @GetMapping("/article/{article_seq}/reply/{reply_seq}")
    public ResponseEntity<Map<String, Object>> rereplyList(@PathVariable int article_seq, @PathVariable int reply_seq, @RequestParam(defaultValue = "1") String page) {
        Map<String, Object> res = snsService.rereplyList(reply_seq, page);
        if (res == null) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
        else if (res.size() == 0) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
        else { return new ResponseEntity<>(res, HttpStatus.CREATED); }
    }

    @PostMapping("/article/{article_seq}/reply/{reply_seq}/rereply")
    public ResponseEntity<String> createRereply(@RequestHeader("jwt") String jwt, @PathVariable int article_seq, @PathVariable int reply_seq, @RequestBody Map<String, String> req) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        ReplyVo replyVo = snsService.createRereply(article_seq, reply_seq, Integer.parseInt(currentUser.get("user_seq")), req.get("content"));
        if (replyVo.getResult() == ReplyVo.ReplyResult.NO_SUCH_REPLY) { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
        else { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
    }

    @PostMapping("/like")
    public ResponseEntity<String> likeArticle(@RequestParam("user") int user_seq, @RequestParam("article") int article_seq) {
        String res = snsService.likeArticle(user_seq, article_seq, 'l');
        if (Objects.equals(res, "SUCCESS")) { return new ResponseEntity<>(SUCCESS, HttpStatus.CREATED); }
        else if (Objects.equals(res, "NO_SUCH_USER")) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @DeleteMapping("/like")
    public ResponseEntity<String> unlikeArticle(@RequestParam("user") int user_seq, @RequestParam("article") int article_seq) {
        String res = snsService.likeArticle(user_seq, article_seq, 'u');
        if (Objects.equals(res, "SUCCESS")) { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
        else if (Objects.equals(res, "NO_SUCH_USER")) { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @GetMapping("/article/{article_seq}/likes")
    public ResponseEntity<Map<String, Object>> likeUserList(@PathVariable int article_seq, @RequestParam(defaultValue = "1") String page){
        Map<String, Object> res = snsService.likeUserList(article_seq, page);
        if (res == null) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
        else { return new ResponseEntity<>(res, HttpStatus.OK); }
    }

    @PostMapping("/article/{article_seq}/report")
    public ResponseEntity<String> reportArticle(@RequestHeader("jwt") String jwt, @PathVariable int article_seq, @RequestBody Map<String, Integer> req) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        String reported = snsService.reportArticle(article_seq, Integer.parseInt(currentUser.get("user_seq")), req.get("report_seq"));
        if (Objects.equals(reported, "ALREADY_REPORTED")) { return new ResponseEntity<>(FAILURE, HttpStatus.ALREADY_REPORTED); }
        else if (Objects.equals(reported, "SUCCESS")) { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @PostMapping("/reply/{reply_seq}/report")
    public ResponseEntity<String> reportReply(@RequestHeader("jwt") String jwt, @PathVariable int reply_seq, @RequestBody Map<String, Integer> req) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        String reported = snsService.reportReply(reply_seq, Integer.parseInt(currentUser.get("user_seq")), req.get("report_seq"));
        if (Objects.equals(reported, "ALREADY_REPORTED")) { return new ResponseEntity<>(FAILURE, HttpStatus.ALREADY_REPORTED); }
        else if (Objects.equals(reported, "SUCCESS")) { return new ResponseEntity<>(SUCCESS, HttpStatus.OK); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestHeader("jwt") String jwt, @RequestParam("follow-user") int user_seq) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        String follow = snsService.followUser(Integer.parseInt(currentUser.get("user_seq")), user_seq);
        if (follow == "NO_SUCH_USER") { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else if (follow == "SUCCESS") { return new ResponseEntity<>(SUCCESS, HttpStatus.CREATED); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @DeleteMapping("/follow")
    public ResponseEntity<String> unfollowUser(@RequestHeader("jwt") String jwt, @RequestParam("follow-user") int user_seq) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }
        String follow = snsService.unfollowUser(Integer.parseInt(currentUser.get("user_seq")), user_seq);
        if (follow == "NO_SUCH_USER") { return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED); }
        else if (follow == "SUCCESS") { return new ResponseEntity<>(SUCCESS, HttpStatus.CREATED); }
        else { return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST); }
    }

    @GetMapping("/user/{user_seq}/follower")
    public ResponseEntity<Map<String, Object>> followerList(@RequestHeader("jwt") String jwt, @PathVariable int user_seq, @RequestParam(defaultValue = "1") String page) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> followerList = snsService.followerList(Integer.parseInt(currentUser.get("user_seq")), user_seq, page);
        if (followerList == null) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
        else { return new ResponseEntity<>(followerList, HttpStatus.OK); }
    }

    @GetMapping("/user/{user_seq}/following")
    public ResponseEntity<Map<String, Object>> followingList(@RequestHeader("jwt") String jwt, @PathVariable int user_seq, @RequestParam(defaultValue = "1") String page) {
        Map<String, String> currentUser = jwtService.decodeJwt(jwt);
        if (Objects.equals(currentUser.get("user_seq"), "null")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> followingList = snsService.followingList(Integer.parseInt(currentUser.get("user_seq")), user_seq, page);
        if (followingList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(followingList, HttpStatus.OK);
        }
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> listGet(@RequestHeader("jwt") String jwt, @RequestParam("page") int page) {
        int userSeq = this.jwtService.getUserSeq(jwt);
        int totalPage = snsService.getTotalArticlePage(userSeq);
        if (page > totalPage) page = totalPage;
        ArrayList<ArticleListVo> articles =  this.snsService.articleList(userSeq, page);
        LinkedList<Map<String, Object>> result = new LinkedList<>();
        for (ArticleListVo article : articles) {
            Map<String, Object> map = new HashMap<>();
            map.put("article_seq", article.getArticleSeq());
            map.put("nickname", article.getNickname());
            map.put("user_seq", article.getUserSeq());
            map.put("profile_url", article.getProfileUrl());
            map.put("created_at", article.getCreatedAt());
            map.put("thumbnail_url", article.getThumbnailUrl());
            map.put("description", article.getDescription());
            map.put("title", article.getTitle());
            map.put("start_date", article.getStartDate());
            map.put("end_date", article.getEndDate());
            map.put("likes", article.getLikeCount());
            map.put("replies", article.getReplyCount());
            map.put("like_yn", article.getLikeYn());
            result.add(map);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total_page", totalPage);
        resultMap.put("page", page);
        resultMap.put("results", result);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
