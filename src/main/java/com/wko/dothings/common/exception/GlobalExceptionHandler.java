package com.wko.dothings.common.exception;


import com.wko.dothings.common.base.ErrorCode;
import com.wko.dothings.common.base.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 进行异常处理，处理Exception.class的异常
     * 然后返回的是json类型的数据
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response doAllException(Exception e) {
        log.error("系统出现异常：" + e.getMessage());
        return Response.error(ErrorCode.SYSTEM_ERROR);
    }


    /**
     * 拦截参数校验异常，这里为了举例处理特殊异常
     *
     * @param e ArithmeticException 算术运算异常
     */
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Response doException(Exception e) {
        log.error("算数异常：：" + e.getMessage());
        return Response.error(505, "算数异常处理");
    }

    /**
     * 拦截参自定义异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Response doCustomException(CustomException e) {
        log.info("出现了异常：" + e.getMessage());
        return Response.error(e.getCode(), e.getMsg());
    }
}
