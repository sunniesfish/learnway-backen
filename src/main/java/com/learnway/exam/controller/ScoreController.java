package com.learnway.exam.controller;

import com.learnway.exam.service.ScoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;
}
