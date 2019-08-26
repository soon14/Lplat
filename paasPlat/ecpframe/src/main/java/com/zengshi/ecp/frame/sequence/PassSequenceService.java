package com.zengshi.ecp.frame.sequence;

import com.db.sequence.SequenceService;

/**
 * Sequence服务接口类
 * @date 2014年6月23日 上午9:59:37 
 * @version V1.0
 */
public interface PassSequenceService extends SequenceService {
	
    int updateSequence(String sequenceName);
}
