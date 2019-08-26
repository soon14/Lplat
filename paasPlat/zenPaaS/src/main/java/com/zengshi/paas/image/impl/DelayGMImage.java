package com.zengshi.paas.image.impl;

import java.io.File;

import com.zengshi.paas.AbstractConfigurationWatcher;
import com.zengshi.paas.image.IImage;
import com.zengshi.paas.message.MessageSender;
import com.zengshi.paas.utils.JSONValidator;

import net.sf.json.JSONObject;

public class DelayGMImage extends AbstractConfigurationWatcher implements IImage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6797632411452763525L;

	private MessageSender messageSender;

	private String confPath = "/com/zengshi/paas/storm/static/html/remove/conf";
	private String topic;

	@Override
	public boolean localImageExist(String localPath, String imageType) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getRomteImage(String imageId, String imageType) throws Exception {

	}

	@Override
	public String scaleImage(String uri, String imageName, int type, String imageSize, String imageType, boolean isExtent) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public String scaleWatermarkImage(String uri, String imageName, int type, String imageSize, String imageType, boolean isExtent) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSourceImagePath(String imageName, String imageType) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getGMMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getReservePath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportService(String imageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String saveToRomte(File image, String imageName, String imageType) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeCacheHtml(String productId) throws Exception {
		messageSender.sendMessage(productId, topic);
		return true;
	}

	@Override
	public void addImgText(String srcPath, String newPath) throws Exception {
	}

	@Override
	public void saveCacheImage(String imageName, String imageSize, String newPath, String imageType) throws Exception {
	}

	@Override
	public void removeLocalCutedImage(String path) throws Exception {
	}

	@Override
	public void process(String conf) {
		JSONObject json = JSONObject.fromObject(conf);
		if (JSONValidator.isChanged(json, "kafka.topic", topic)) {
			topic = json.getString("kafka.topic");
		}
	}

	@Override
	public String getConfPath() {
		return confPath;
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	@Override
	public void convertType(String srcImage, String descImage) throws Exception {
		
	}

	@Override
	public String getUpPath() {
		return null;
	}

	@Override
	public String getUpPathNew() {
		return null;
	}

	@Override
	public String getSupportType() {
		return null;
	}

	@Override
	public String getImageServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageUploadUrl() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String getDefaultImage() {
        return null;
    }
	
	
}
