package com.sap.cap.esmapi.exceptions.handlers;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHdlr
{

    @ExceptionHandler(EX_ESMAPI.class)
    public ModelAndView handleNotFound(Exception ex)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("error");
        System.out.println(ex.getLocalizedMessage());
		mv.addObject("formError",ex.getLocalizedMessage());
		return mv;
	}
    
}
