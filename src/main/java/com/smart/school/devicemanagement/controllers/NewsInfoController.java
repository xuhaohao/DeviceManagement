package com.smart.school.devicemanagement.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.smart.school.devicemanagement.app.domain.App_NewsInfoModel;
import com.smart.school.devicemanagement.auth.AuthHelper;
import com.smart.school.devicemanagement.auth.AuthPassport;
import com.smart.school.devicemanagement.common.BaseController;
import com.smart.school.devicemanagement.common.JSONHelper;
import com.smart.school.devicemanagement.common.ProjectContext;
import com.smart.school.devicemanagement.common.utilities.PageList;
import com.smart.school.devicemanagement.common.utilities.PageListUtil;
import com.smart.school.devicemanagement.models.NewsInfo;
import com.smart.school.devicemanagement.models.User;
import com.smart.school.devicemanagement.services.ICustomInfoService;
import com.smart.school.devicemanagement.services.INewsInfoService;
import com.smart.school.devicemanagement.web.domain.NewsInfoModel;

@Controller
@RequestMapping(value = "/newsInfo")
public class NewsInfoController extends BaseController{

	private static final Logger log = LoggerFactory.getLogger(NewsInfoController.class);
	
	@AuthPassport
	@RequestMapping(value="/list", method = {RequestMethod.GET})
    public String list(HttpServletRequest request, Model model, NewsInfo searchModel){ 	
		INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
    	model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

        model.addAttribute("searchModel", searchModel);
        int pageNo = ServletRequestUtils.getIntParameter(request, PageListUtil.PAGE_NO_NAME, PageListUtil.DEFAULT_PAGE_NO);
        int pageSize = ServletRequestUtils.getIntParameter(request, PageListUtil.PAGE_SIZE_NAME, PageListUtil.DEFAULT_PAGE_SIZE);     
        
        PageList<NewsInfo> pageListNews = null;
        if (StringUtils.isBlank(searchModel.getTitle())) {
        	pageListNews = newsInfoService.listPage(pageNo, pageSize , Order.desc("publicTime"));
        	
		}else {
			pageListNews =  newsInfoService.listPage( pageNo, pageSize ,Order.desc("publicTime") ,Restrictions.like("title", searchModel.getTitle(),MatchMode.ANYWHERE));
		}
        model.addAttribute("contentModel", pageListNews);
        return "newsInfo/list";
    }
	
	
	@AuthPassport
	@RequestMapping(value = "/add", method = {RequestMethod.GET})
	public String add(Model model){	
		
		if(!model.containsAttribute("contentModel")){
			NewsInfoModel newsInfoModel=new NewsInfoModel();
			newsInfoModel.setPk(UUID.randomUUID().toString());
			
			model.addAttribute("contentModel", newsInfoModel);
		}
		
        return "newsInfo/add";	
	}
	
	@AuthPassport
	@RequestMapping(value = "/add", method = {RequestMethod.POST})
	public String add(HttpServletRequest request, Model model,@Valid @ModelAttribute("contentModel") NewsInfoModel newsInfoModel, BindingResult result){	
		
		if(result.hasErrors())
            return add(model);
		INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
		
		if(newsInfoModel != null ){
			NewsInfo newsInfo = new NewsInfo();
			newsInfoModel.setPk(UUID.randomUUID().toString());
			
			BeanUtils.copyProperties(newsInfoModel, newsInfo);
			
			newsInfo.setPublicTime(Calendar.getInstance());
			newsInfo.setUser(AuthHelper.getCurrUser(request));
			
			newsInfoService.saveOrUpdate(newsInfo);
        }
		String returnUrl = ServletRequestUtils.getStringParameter(request, "returnUrl", null);
		if(returnUrl==null)
        	returnUrl="newsInfo/list";
    	return "redirect:"+returnUrl;     	
	}
	
	@AuthPassport
	@RequestMapping(value = "/edit/{pk}", method = {RequestMethod.GET})
	public String edit(HttpServletRequest request, Model model, @PathVariable(value="pk") String pk) {	
		INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
		if(!model.containsAttribute("contentModel")){
			NewsInfo newsInfo= newsInfoService.getByPk(pk);
			NewsInfoModel newsInfoModel = new NewsInfoModel();
			BeanUtils.copyProperties(newsInfo, newsInfoModel);
			model.addAttribute("contentModel", newsInfoModel);
		}
		
        return "newsInfo/edit";	
	}
	
	@AuthPassport
	@RequestMapping(value = "/edit/{pk}", method = {RequestMethod.POST})
    public String edit(HttpServletRequest request, Model model, @Valid @ModelAttribute("contentModel") NewsInfoModel editModel, @PathVariable(value="pk") String pk, BindingResult result)  {
        if(result.hasErrors())
            return edit(request, model, pk);
        INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
        
        String returnUrl = ServletRequestUtils.getStringParameter(request, "returnUrl", null);
        
        NewsInfo newsInfo = newsInfoService.getByPk(pk);
        if (newsInfo != null) {
        	BeanUtils.copyProperties(editModel, newsInfo);
            newsInfoService.saveOrUpdate(newsInfo);
		}
        if(returnUrl==null)
        	returnUrl="newsInfo/list";
    	return "redirect:"+returnUrl;      	
    }
	
	
	@AuthPassport
	@RequestMapping(value = "/delete/{pk}", method = {RequestMethod.GET})
	public String delete(HttpServletRequest request, Model model, @PathVariable(value="pk") String pk) {
		INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
		newsInfoService.deleteByPk(pk);
		String returnUrl = ServletRequestUtils.getStringParameter(request, "returnUrl", null);
		if(returnUrl==null)
        	returnUrl="newsInfo/list";
    	return "redirect:"+returnUrl;     	
	}
	
	
	@RequestMapping(value="/appNewsInfo", method = {RequestMethod.GET})
	public void appLogin(HttpServletRequest request,HttpServletResponse response) throws IOException{
		ICustomInfoService customInfoService = ProjectContext.getBean(ICustomInfoService.class);
		INewsInfoService newsInfoService = ProjectContext.getBean(INewsInfoService.class);
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		 PrintWriter pw = null;  
	        try {  
	        	String strName = request.getParameter("pk");
	        	
	        	List<NewsInfo> newsInfos = newsInfoService.getAll();
	        	List<App_NewsInfoModel> app_NewsInfoModels = new ArrayList<App_NewsInfoModel>();
	        	for (NewsInfo newsInfo : newsInfos) {
	        		App_NewsInfoModel app_NewsInfoModel = new App_NewsInfoModel();
	        		BeanUtils.copyProperties(newsInfo, app_NewsInfoModel);
	        		if (newsInfo.getUser() != null) {
	        			app_NewsInfoModel.setPublicUserId(newsInfo.getUser().getPk());
	        			app_NewsInfoModel.setPublicUserName(newsInfo.getUser().getStrName());
					}
	        		if (newsInfo.getPublicTime() != null) {
	        			String time=df.format(newsInfo.getPublicTime().getTime());
	        			app_NewsInfoModel.setStrPublicTime(time);
					}
	        		app_NewsInfoModels.add(app_NewsInfoModel);
				}
	        	
				response.setContentType("text/xml;charset=utf-8");   
	            response.setCharacterEncoding("UTF-8");  
	            response.setHeader("Cache-Control", "no-cache");  

		            pw = response.getWriter();  
		            String xmlContent = JSONHelper.toJSON(app_NewsInfoModels);
		            pw.print(xmlContent);  
		            pw.flush();  
	        }  
	        catch (Exception e) {  
	            log.error("",e);
	        }  
	        finally {  
	            if (pw != null)  
	                pw.close();  
	        }  
		
	}

}
