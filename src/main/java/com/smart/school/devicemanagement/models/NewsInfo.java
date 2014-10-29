package com.smart.school.devicemanagement.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "NewsInfo")
public class NewsInfo {

	private String pk;
	private SchoolInfo schoolInfo;
	private User user;
	private String title;
	private String content;
	private Date publicTime;
	
	public NewsInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Id
	@Column(name = "pk",  nullable = false, length = 36)
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="fk_schoolInfo")
	public SchoolInfo getSchoolInfo() {
		return schoolInfo;
	}
	public void setSchoolInfo(SchoolInfo schoolInfo) {
		this.schoolInfo = schoolInfo;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="fk_user")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Column(name = "title",  nullable = false, length = 36)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "content",  length = 2048)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "publicTime",  nullable = false)
	public Date getPublicTime() {
		return publicTime;
	}
	public void setPublicTime(Date publicTime) {
		this.publicTime = publicTime;
	}
	
	
}