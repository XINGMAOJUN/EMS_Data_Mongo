package com.persagy.mongo.test;

import java.util.Date;

import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.annotation.Field;
import com.persagy.mongo.core.annotation.Index;
import com.persagy.mongo.core.annotation.Indexed;
import com.persagy.mongo.core.annotation.Indexes;
import com.persagy.mongo.core.enumeration.IndexDirection;
import com.persagy.mongo.core.enumeration.IndexType;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

@Entity(value = "user")
@Indexes({ @Index(name = "a_b", fields = { @Field(value = "id", type = IndexType.ASC),
		@Field(value = "age", type = IndexType.ASC) } ,unique=true) })
public class User extends MongoBusinessObject {

	private static final long serialVersionUID = -2647521395610332475L;

	private Long id;
	//@Indexed(value = IndexDirection.ASC, name = "ASD")
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