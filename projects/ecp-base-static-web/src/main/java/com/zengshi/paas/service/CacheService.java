package com.zengshi.paas.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zengshi.paas.utils.ImageUtil;

@Path("/cacheService")
public class CacheService {
	
	@GET
	@Path(value = "/removeCacheHtml/{productId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeCacheHtml(@PathParam("productId")String productId) {
		boolean relsut = ImageUtil.removeCacheHtml(productId);
		return relsut?"true":"false";
	}
}
