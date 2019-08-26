/** 
 * Date:2015年7月31日下午2:29:07 
 * 
*/
package com.zengshi.ecp.frame.sequence;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zengshi.paas.utils.CacheUtil;
import com.db.sequence.Sequence;

/** 
 * Description: <br>
 * Date:2015年7月31日下午2:29:07  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class PaasSequence extends Sequence{

private final Lock lock = new ReentrantLock();
    
    private String sequenceName;
    private PaasSequenceCache sequence;
    private PaasSequenceServiceImpl sequenceService;
    
    private long max=-1;
    private long start=1;
    private long range=1000;
    
    public void init(){
        try {
            this.lock.lock();
            if(!CacheUtil.exists(sequenceName)){
                sequence = (PaasSequenceCache) sequenceService.getSequenceCache(sequenceName);
                if(null==sequence){
                    return;
                }
                range=sequence.getRange();
                CacheUtil.addItemInt(sequenceName, Long.valueOf(sequence.getCurrent()));
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Long nextValue() {
        long value=-1;
        try {
            this.lock.lock();
            if (CacheUtil.exists(sequenceName)) {

                long seqNo = CacheUtil.getIncrement(sequenceName);
                if (max > -1 && seqNo > max) {
                    value = seqNo % (max + 1);
                } else {
                    value = seqNo;
                }
                if(value%range==0){
                    sequenceService.updateSequence(sequenceName);
                }
            } else {
                sequence = (PaasSequenceCache) sequenceService.getSequenceCache(sequenceName);
                if (null == sequence) {
                    return null;
                }
                CacheUtil.addItemInt(sequenceName, Long.valueOf(sequence.getCurrent()));
                value = sequence.getCurrent();
            }
        } finally {
            this.lock.unlock();
        }
        
        if(value == -1) {
            return null;
        }
        return value;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public PaasSequenceServiceImpl getSequenceService() {
        return sequenceService;
    }

    public void setSequenceService(PaasSequenceServiceImpl sequenceService) {
        this.sequenceService = sequenceService;
    }
    
    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
    
}

