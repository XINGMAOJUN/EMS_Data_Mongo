package com.persagy.mongo.test.mindin;

import java.util.Date;

import com.persagy.mongo.core.annotation.Dimension;
import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.annotation.Field;
import com.persagy.mongo.core.annotation.Index;
import com.persagy.mongo.core.annotation.Indexes;
import com.persagy.mongo.core.annotation.Month;
import com.persagy.mongo.core.enumeration.IndexType;
import com.persagy.mongo.core.enumeration.MongoDimension;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

@Entity(value = "statdata")
@Month(column = "counttime")
@Dimension(dimension = MongoDimension.Building)
@Indexes({ @Index(name = "statdata_index", fields = { 
		@Field(value = "sign", type = IndexType.ASC),
		@Field(value = "funcid", type = IndexType.ASC),
		@Field(value = "timetype", type = IndexType.ASC) ,
		@Field(value = "valuetype", type = IndexType.ASC) ,
		@Field(value = "counttime", type = IndexType.ASC)  },unique=true) })
public class MGStatData extends MongoBusinessObject {

	private static final long serialVersionUID = -2866081248181336334L;
	
	private String sign;
	private Integer funcid;
	private Integer timetype;
	private Integer valuetype;
	private Date counttime;
	private Date receivetime;
	private Integer datacount;
	private Double data;
	
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Integer getFuncid() {
		return funcid;
	}
	public void setFuncid(Integer funcid) {
		this.funcid = funcid;
	}
	public Integer getTimetype() {
		return timetype;
	}
	public void setTimetype(Integer timetype) {
		this.timetype = timetype;
	}
	public Integer getValuetype() {
		return valuetype;
	}
	public void setValuetype(Integer valuetype) {
		this.valuetype = valuetype;
	}
	public Date getCounttime() {
		return counttime;
	}
	public void setCounttime(Date counttime) {
		this.counttime = counttime;
	}
	public Date getReceivetime() {
		return receivetime;
	}
	public void setReceivetime(Date receivetime) {
		this.receivetime = receivetime;
	}
	public Integer getDatacount() {
		return datacount;
	}
	public void setDatacount(Integer datacount) {
		this.datacount = datacount;
	}
	public Double getData() {
		return data;
	}
	public void setData(Double data) {
		this.data = data;
	}

	
}