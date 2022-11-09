package com.wko.dothings.common.exception;


import com.wko.dothings.common.base.ErrorCode;
import com.wko.dothings.common.base.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.validation.ConstraintViolationException;
import java.util.StringJoiner;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 拦截参自定义异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Response doCustomException(CustomException e) {
        log.info("出现了异常：" + e.getMessage());
        return Response.error(e.getCode(), e.getMsg());
    }

    /**
     * post方式提交json数据,参数校验失败后,会抛出一个MethodArgumentNotValidException
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        mv.addObject("code", 87);
        mv.addObject("msg", "参数校验异常");
        return mv;
    }

    /**
     * get方式提交参数,参数校验失败后,会抛出一个ConstraintViolationException
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        mv.addObject("code", 87);
        mv.addObject("msg", "参数校验异常");
        return mv;
    }
}
