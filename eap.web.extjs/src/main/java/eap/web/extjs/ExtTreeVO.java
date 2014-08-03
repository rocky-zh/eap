package eap.web.extjs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class ExtTreeVO implements Serializable {
	
	public static final String ROOT_ID = "-1";
	
	private String id;
	private String code;
	private String text;
	private String cls;
	private String href;
	private String hrefTarget;
	private String icon;
	private String iconCls;
	private Boolean leaf = false;
	private Boolean expandable = true;
	private String qtip;
	private String qtitle;
	
	private Boolean allowDrag = true;
	private Boolean allowDrop = true;
	private Boolean checked;
	
	private List children = new ArrayList(); // ? extends ExtTreeVO
	
	private String parentId;
	private String parentCode;
	
	public ExtTreeVO addChild(Object child) {
		children.add(child);
		return this;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}

	public String getHrefTarget() {
		return hrefTarget;
	}
	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}

	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
		if (leaf != null && leaf.booleanValue()) {
			this.expandable = false;
		} else {
			this.expandable = true;
		}
	}

	public Boolean getExpandable() {
		return expandable;
	}
	public void setExpandable(Boolean expandable) {
		this.expandable = expandable;
		if (expandable != null && expandable.booleanValue()) {
			this.leaf = false;
		} else {
			this.leaf = true;
		}
	}

	public String getQtip() {
		return qtip;
	}
	public void setQtip(String qtip) {
		this.qtip = qtip;
	}
	
	public String getQtitle() {
		return qtitle;
	}
	public void setQtitle(String qtitle) {
		this.qtitle = qtitle;
	}
	
	public Boolean getAllowDrag() {
		return allowDrag;
	}
	public void setAllowDrag(Boolean allowDrag) {
		this.allowDrag = allowDrag;
	}
	
	public Boolean getAllowDrop() {
		return allowDrop;
	}
	public void setAllowDrop(Boolean allowDrop) {
		this.allowDrop = allowDrop;
	}
	
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}