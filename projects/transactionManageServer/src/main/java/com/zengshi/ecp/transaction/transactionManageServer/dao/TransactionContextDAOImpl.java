package com.zengshi.ecp.transaction.transactionManageServer.dao;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.distribute.tx.common.TransactionContext;
import com.distribute.tx.server.dao.TransactionContextDAO;

@Repository("transactionContextDAO")
public class TransactionContextDAOImpl implements TransactionContextDAO {

	@Resource(name="sqlSession")
	private SqlSessionTemplate sqlSession;
	
	@Override
	public TransactionContext getTransactionContextById(Long paramLong) {
		
		return sqlSession.selectOne("com.distribute.tx.common.TransactionContextMapper.queryById", paramLong);
	}

	@Override
	public boolean insert(TransactionContext paramTransactionContext) {
		sqlSession.insert("com.distribute.tx.common.TransactionContextMapper.insert",paramTransactionContext);
		return true;
	}
	@Override
	public int update(TransactionContext paramTransactionContext) {
		int res = sqlSession.update("com.distribute.tx.common.TransactionContextMapper.update",
					paramTransactionContext);
		return res;
	}

	@Override
	public List<TransactionContext> queryAbnormalTransaction(long paramLong) {
		List<TransactionContext> list = sqlSession.selectList(
					"com.distribute.tx.common.TransactionContextMapper.queryAbnormalTransaction", paramLong);
		return list; 
	}

}
