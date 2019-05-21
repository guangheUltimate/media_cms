package io.guangsoft.web.modules.sms.entity;

import io.guangsoft.web.common.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 */

@TableName("sms_template")
@SuppressWarnings("serial")
public class SmsTemplate extends DataEntity<String> {

    /**id*/
    @TableId(value = "id", type = IdType.UUID)
	private String id;
    /**模版名称*/
    @TableField(value = "name")
	private String name;
    /**模版编码*/
    @TableField(value = "code")
	private String code;
    /**业务类型*/
    @TableField(value = "business_type")
	private String businessType;
    /**模版内容*/
    @TableField(value = "template_content")
	private String templateContent;
	
	/**
	 * 获取  id
	 *@return String  id
	 */
	public String getId(){
		return this.id;
	}

	/**
	 * 设置  id
	 *@param id  id
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 * 获取  name
	 *@return String  模版名称
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * 设置  name
	 *@param name  模版名称
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 获取  code
	 *@return String  模版编码
	 */
	public String getCode(){
		return this.code;
	}

	/**
	 * 设置  code
	 *@param code  模版编码
	 */
	public void setCode(String code){
		this.code = code;
	}
	/**
	 * 获取  businessType
	 *@return String  业务类型
	 */
	public String getBusinessType(){
		return this.businessType;
	}

	/**
	 * 设置  businessType
	 *@param businessType  业务类型
	 */
	public void setBusinessType(String businessType){
		this.businessType = businessType;
	}
	/**
	 * 获取  templateContent
	 *@return String  模版内容
	 */
	public String getTemplateContent(){
		return this.templateContent;
	}

	/**
	 * 设置  templateContent
	 *@param templateContent  模版内容
	 */
	public void setTemplateContent(String templateContent){
		this.templateContent = templateContent;
	}
}