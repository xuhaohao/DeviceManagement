package com.smart.school.devicemanagement.web.domain;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

public class UserLoginModel implements java.io.Serializable{

	private String pk;
	@NotEmpty(message="用户名不能为空")
	private String strName;
	@NotEmpty(message="密码不能为空")
	private String psd;
	
	
	
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public String getPsd() {
		return psd;
	}
	public void setPsd(String psd) {
		this.psd = psd;
	}
	
	
}
