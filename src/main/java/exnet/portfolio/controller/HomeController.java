package exnet.portfolio.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@EnableAutoConfiguration
public class HomeController {

    // @Autowired
    // private IThirdpartyService thirdpartyService;

    @RequestMapping("/hello")
    @ResponseBody
    String hello() {
        return "hello !";
    }

    // @RequestMapping(value="/auth/thirdparty", method = RequestMethod.POST)
    // @ResponseBody
    // ResponseDto authThirdParty(@RequestBody AuthThirdPartyDto request) {
    //     return thirdpartyService.validAuthCode(request);
    // }
}