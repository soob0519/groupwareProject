package com.groupware.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.groupware.service.FavorService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/favor")
public class FavorController {

    private final FavorService favorService;

    public FavorController(FavorService favorService) {
        this.favorService = favorService;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggleFavorite(HttpSession session, @RequestParam int fempno) {
        // 테스트~
    	Integer empno = (Integer) session.getAttribute("empno");
        if(empno == null) empno = 2;

        boolean isFav = favorService.isFavorite(empno, fempno);
        if(isFav) {
            favorService.removeFavorite(empno, fempno);
            return "removed";
        } else {
            favorService.addFavorite(empno, fempno);
            return "added";
        }
    }

    /** 내가 즐겨찾기한 사원번호 리스트 반환 */
    @GetMapping("/list")
    public ResponseEntity<List<Integer>> getMyFavorites(HttpSession session) {
        // int empno = (int) session.getAttribute("empno");
        int empno = 2;

        List<Integer> favs = favorService.getFavorites(empno);
        return ResponseEntity.ok(favs);
    }
}