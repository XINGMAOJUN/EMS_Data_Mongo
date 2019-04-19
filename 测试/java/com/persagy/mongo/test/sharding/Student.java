package com.persagy.mongo.test.sharding;

import java.util.Date;

import com.persagy.mongo.core.annotation.Dimension;
import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.annotation.Indexed;
import com.persagy.mongo.core.annotation.Month;
import com.persagy.mongo.core.enumeration.IndexDirection;
import com.persagy.mongo.core.enumeration.MongoDimension;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

@Entity(value = "student")
@Month(column = "ctime")
@Dimension(dimension = MongoDimension.Building)
public class Student extends MongoBusinessObject {

	private static final long serialVersionUID = -8992600413734560479L;
	@Indexed(value = IndexDirection.ASC, name = "ASD")
	private Long id;
	private String name;
	private Boolean sex;
	private Integer age;
	private String address;
	private Date ctime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSex() {
		return sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

}