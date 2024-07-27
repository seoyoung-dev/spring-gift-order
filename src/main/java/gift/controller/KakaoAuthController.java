package gift.controller;

import gift.config.KakaoProperties;
import gift.service.KakaoAuthService;
import gift.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/oauth")
public class KakaoAuthController {

    private final KakaoProperties kakaoProperties;
    private final KakaoAuthService kakaoAuthService;
    private final MemberService memberService;

    @Autowired
    public KakaoAuthController(KakaoProperties kakaoProperties, KakaoAuthService kakaoAuthService, MemberService memberService) {
        this.kakaoProperties = kakaoProperties;
        this.kakaoAuthService = kakaoAuthService;
        this.memberService = memberService;
    }

    @GetMapping("/kakao")
    public ModelAndView redirectToKakaoLogin() {
        String url = kakaoProperties.authUrl() + "?response_type=code&client_id="
            + kakaoProperties.clientId() + "&redirect_uri=" + kakaoProperties.redirectUri();
        return new ModelAndView("redirect:" + url);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> loginWithKakao(@RequestParam String code) {
        String token = kakaoAuthService.getAccessToken(code);
        String kakaoUserId = kakaoAuthService.getKakaoUserId(token);
        String jwtToken = kakaoAuthService.registerKakaoMember(kakaoUserId, token);
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }
}