package com.enci.ecp.bizprocess.product.rule;

public class PremiumCalcBom {
	
	/** 险种代码 */
	private String productCode;
	/** 被保险人性别
	 * F-男
	 * M-女
	 */
	private String insuredSex;
	/** 被保险人生日; yyyy-MM-dd */
	private String insuredBirthday;
	/** 保险期限 */
	private String insPeriod;
	/** 
	 * 保险期限单位
	 * D-天
	 * W-周
	 * M-月
	 * Y-年
	 * A-岁
	 */
	private String insPeriodUnit;
	/** 交费方式
	 * U-不定期
	 * T-趸交
	 * M-月交
	 * Q-季交
	 * H-半年交
	 * Y-年交
	 */
	private String payMode;
	/** 交费期限 */
	private String payPeriod;
	/** 基本保额 */
	private Double amount;
	/** 折扣比率（浮动费率） */
	private Double discountRate = 1.0;
	/** 投保份数 */
	private Integer applyNum = 1;
	
	/** 基本保费 */
	private Double premium;
	
	public Double getTotalPremium() {
		if (premium != null && applyNum != null) {
			return premium * applyNum;
		}
		
		return null;
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getInsuredSex() {
		return insuredSex;
	}
	public void setInsuredSex(String insuredSex) {
		this.insuredSex = insuredSex;
	}
	public String getInsuredBirthday() {
		return this.insuredBirthday;
	}
	public void setInsuredBirthday(String insuredBirthday) {
		this.insuredBirthday = insuredBirthday;
	}
	public String getInsPeriod() {
		return insPeriod;
	}
	public void setInsPeriod(String insPeriod) {
		this.insPeriod = insPeriod;
	}
	public String getInsPeriodUnit() {
		return insPeriodUnit;
	}
	public void setInsPeriodUnit(String insPeriodUnit) {
		this.insPeriodUnit = insPeriodUnit;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getPayPeriod() {
		return payPeriod;
	}
	public void setPayPeriod(String payPeriod) {
		this.payPeriod = payPeriod;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getDiscountRate() {
		return discountRate;
	}
	public void setDiscountRate(Double discountRate) {
		this.discountRate = discountRate;
	}
	public Integer getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(Integer applyNum) {
		this.applyNum = applyNum;
	}
	public Double getPremium() {
		return premium;
	}
	public void setPremium(Double premium) {
		this.premium = premium;
	}
	public void setPremium(Object premium) {
		if (premium != null) {
			this.premium = new Double(premium.toString());
		}
	}
}