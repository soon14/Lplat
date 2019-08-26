/** 
 * Date:2016-2-21下午4:56:42 
 * 
 */ 
package com.zengshi.butterfly.app.model;

/**
 * Description: <br>
 * Date:2016-2-21下午4:56:42  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class AppDatapackage implements IAppDatapackage {
    
    private IHeader header;
    
    private IBody body;
    
    public AppDatapackage() {
        super();
    }

    public AppDatapackage(IHeader header) {
        super();
        this.header = header;
    }

    public AppDatapackage(IHeader header, IBody body) {
        super();
        this.header = header;
        this.body = body;
    }
    
    /** 
     * TODO 简单描述该方法的实现功能（可选）. 
     * @see com.zengshi.butterfly.app.model.IAppDatapackage#getHeader() 
     */
    @Override
    public IHeader getHeader() {
        return this.header;
    }

    /** 
     * TODO 简单描述该方法的实现功能（可选）. 
     * @see com.zengshi.butterfly.app.model.IAppDatapackage#setHeader(com.zengshi.butterfly.app.model.IHeader) 
     */
    @Override
    public void setHeader(IHeader header) {
        this.header = header;
    }

    /** 
     * TODO 简单描述该方法的实现功能（可选）. 
     * @see com.zengshi.butterfly.app.model.IAppDatapackage#getBody() 
     */
    @Override
    public IBody getBody() {
        return this.body;
    }

    /** 
     * TODO 简单描述该方法的实现功能（可选）. 
     * @see com.zengshi.butterfly.app.model.IAppDatapackage#setBody(com.zengshi.butterfly.app.model.IBody) 
     */
    @Override
    public void setBody(IBody body) {
        this.body = body;
    }

}

