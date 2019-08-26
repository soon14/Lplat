/** 
 * Date:2015年7月31日上午10:24:26 
 * 
*/
package com.zengshi.ecp.frame.sequence;

import com.db.sequence.SequenceCache;

/** 
 * Description: <br>
 * Date:2015年7月31日上午10:24:26  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class PaasSequenceCache extends SequenceCache{
    
    private long range=1000;
    

    public PaasSequenceCache(long start, long end, long range) {
        super(start, end);
        this.range=range;
    }
    
    
    public long getRange() {
        return range;
    }
    /**
     * 
     * setMax:最大值，大于-1,循环取值. <br/> 
     * 
     * @param max 
     * @since JDK 1.6
     */
    public void setRange(long range) {
        this.range = range;
    }

}

