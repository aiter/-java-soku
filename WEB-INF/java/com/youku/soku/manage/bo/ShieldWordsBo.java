package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

import com.youku.soku.manage.entity.HitRolesConstants;
import com.youku.soku.manage.torque.DeterminerWordRelation;
import com.youku.soku.manage.torque.ShieldChannel;
import com.youku.soku.manage.torque.ShieldWordRelation;

public class ShieldWordsBo {
	
	/** The value for the id field */
    private int id;
    
    private String word;

    /** The value for the excluding field */
    private String excluding;

    /** The value for the type field */
    private int type;
    
    /** The value for the youkuEffect field */
    private int youkuEffect;

    /** The value for the othresEffect field */
    private int othersEffect;

    /** The value for the hitRole field */
    private int hitRole;

    /** The value for the fkShieldCategoryId field */
    private int fkShieldCategoryId;

    /** The value for the startTime field */
    private Date startTime;

    /** The value for the expireTime field */
    private Date expireTime;

    /** The value for the remark field */
    private String remark;

    /** The value for the updateTime field */
    private Date updateTime;

    /** The value for the createTime field */
    private Date createTime;
    
    private String modifier;

    private List<ShieldWordRelation> shieldWordRelationList;
    
    private List<DeterminerWordRelation> determinerWordRelation;
    
    private List<ShieldChannel> shieldChannelList;
    
    private List shieldChannelIdList;
    
    private int wordSiteCategory;
    
    private int wordSiteLevel;
    
    private String hitRoleStr;
    
    private String wordCategoryStr;
    
    private String shieldChannelStr;
    
    private String determinWordInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExcluding() {
		return excluding;
	}

	public void setExcluding(String excluding) {
		this.excluding = excluding;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHitRole() {
		return hitRole;
	}

	public void setHitRole(int hitRole) {
		this.hitRole = hitRole;
	}

	public int getFkShieldCategoryId() {
		return fkShieldCategoryId;
	}

	public void setFkShieldCategoryId(int fkShieldCategoryId) {
		this.fkShieldCategoryId = fkShieldCategoryId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<ShieldWordRelation> getShieldWordRelationList() {
		return shieldWordRelationList;
	}

	public void setShieldWordRelationList(
			List<ShieldWordRelation> shieldWordRelationList) {
		this.shieldWordRelationList = shieldWordRelationList;
	}

	public List<DeterminerWordRelation> getDeterminerWordRelation() {
		return determinerWordRelation;
	}

	public void setDeterminerWordRelation(
			List<DeterminerWordRelation> determinerWordRelation) {
		this.determinerWordRelation = determinerWordRelation;
	}

	public List<ShieldChannel> getShieldChannelList() {
		return shieldChannelList;
	}

	public void setShieldChannelList(List<ShieldChannel> shieldChannelList) {
		this.shieldChannelList = shieldChannelList;
	}

	public int getWordSiteCategory() {
		return wordSiteCategory;
	}

	public void setWordSiteCategory(int wordSiteCategory) {
		this.wordSiteCategory = wordSiteCategory;
	}

	public int getWordSiteLevel() {
		return wordSiteLevel;
	}

	public void setWordSiteLevel(int wordSiteLevel) {
		this.wordSiteLevel = wordSiteLevel;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getHitRoleStr() {
		return HitRolesConstants.HITROLEMAP.get(hitRole);
	}

	public void setHitRoleStr(String hitRoleStr) {
		this.hitRoleStr = hitRoleStr;
	}

	public String getWordCategoryStr() {
		return wordCategoryStr;
	}

	public void setWordCategoryStr(String wordCategoryStr) {
		this.wordCategoryStr = wordCategoryStr;
	}

	public List getShieldChannelIdList() {
		return shieldChannelIdList;
	}

	public void setShieldChannelIdList(List shieldChannelIdList) {
		this.shieldChannelIdList = shieldChannelIdList;
	}

	public String getShieldChannelStr() {
		return shieldChannelStr;
	}

	public void setShieldChannelStr(String shieldChannelStr) {
		this.shieldChannelStr = shieldChannelStr;
	}

	public String getDeterminWordInfo() {
		return determinWordInfo;
	}

	public void setDeterminWordInfo(String determinWordInfo) {
		this.determinWordInfo = determinWordInfo;
	}

	public int getYoukuEffect() {
		return youkuEffect;
	}

	public void setYoukuEffect(int youkuEffect) {
		this.youkuEffect = youkuEffect;
	}


	public int getOthersEffect() {
		return othersEffect;
	}

	public void setOthersEffect(int othersEffect) {
		this.othersEffect = othersEffect;
	}

	@Override
	public String toString() {
		return "ShieldWordsBo [createTime=" + createTime
				+ ", determinWordInfo=" + determinWordInfo
				+ ", determinerWordRelation=" + determinerWordRelation
				+ ", excluding=" + excluding + ", expireTime=" + expireTime
				+ ", fkShieldCategoryId=" + fkShieldCategoryId + ", hitRole="
				+ hitRole + ", hitRoleStr=" + hitRoleStr + ", id=" + id
				+ ", modifier=" + modifier + ", othersEffect=" + othersEffect
				+ ", remark=" + remark + ", shieldChannelIdList="
				+ shieldChannelIdList + ", shieldChannelList="
				+ shieldChannelList + ", shieldChannelStr=" + shieldChannelStr
				+ ", shieldWordRelationList=" + shieldWordRelationList
				+ ", startTime=" + startTime + ", type=" + type
				+ ", updateTime=" + updateTime + ", word=" + word
				+ ", wordCategoryStr=" + wordCategoryStr
				+ ", wordSiteCategory=" + wordSiteCategory + ", wordSiteLevel="
				+ wordSiteLevel + ", youkuEffect=" + youkuEffect + "]";
	}

	
	
    
}
