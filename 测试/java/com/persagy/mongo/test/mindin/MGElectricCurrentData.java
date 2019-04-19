package com.persagy.mongo.test.mindin;

import java.util.Date;

import com.persagy.mongo.core.annotation.Dimension;
import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.annotation.Field;
import com.persagy.mongo.core.annotation.Index;
import com.persagy.mongo.core.annotation.Indexes;
import com.persagy.mongo.core.enumeration.IndexType;
import com.persagy.mongo.core.enumeration.MongoDimension;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

@Entity(value = "electriccurrentdata")
@Dimension(dimension = MongoDimension.Building)
@Indexes({ @Index(name = "electriccurrentdata_index", fields = { 
		@Field(value = "sign", type = IndexType.ASC),
		@Field(value = "funcid", type = IndexType.ASC),
		@Field(value = "receivetime", type = IndexType.ASC)  },unique=true) })
public class MGElectricCurrentData extends MongoBusinessObject {

	private static final long serialVersionUID = -8992600413734560479L;

	private String sign;
	private Integer funcid;
	private Date receivetime;
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
	public Date getReceivetime() {
		return receivetime;
	}
	public void setReceivetime(Date receivetime) {
		this.receivetime = receivetime;
	}
	public Double getData() {
		return data;
	}
	public void setData(Double data) {
		this.data = data;
	}

}