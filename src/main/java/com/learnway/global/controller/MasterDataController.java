package com.learnway.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learnway.global.dto.MasterDataDTO;
import com.learnway.global.service.MasterDataService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// 기준 정보(Material/Subject/StudyWay)를 관리하는 컨트롤러
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class MasterDataController {

    private final MasterDataService masterDataService;
    // 기준 정보 추가 폼
    @GetMapping("/addMasterData")
    public String showForm(Model model, @ModelAttribute ("message") String message) {
        model.addAttribute("masterDataDTO", new MasterDataDTO());
        model.addAttribute("message", message);
        return "addMasterData"; // 폼을 보여줄 뷰 이름
    }

    // 기준 정보 추가
    @PostMapping("/addMasterData")
    public String addMasterData(@Valid @ModelAttribute MasterDataDTO masterDataDTO, BindingResult bindingResult, Model model) {
        //유효성 검사 실패 시 입력된 데이터 Model 에 담아서 현재 페이지 반환
        if (bindingResult.hasErrors()) {
            model.addAttribute("masterDataDTO", masterDataDTO);
            return "addMasterData";
        }

        masterDataService.addMasterData(masterDataDTO);
        return "redirect:/admin/addMasterData?message=success";
    }
}
