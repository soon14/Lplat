package com.zengshi.ecp.transaction.transactionManageServer.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.distribute.tx.common.Transaction;
import com.distribute.tx.server.dao.TransactionDAO;

@Repository("transactionDAO")
public class TransactionDAOImpl implements TransactionDAO {

	@Resource(name="sqlSession")
	private SqlSessionTemplate sqlSession;
	
	@Override
	public Transaction getTransactionById(String paramString) {

		Transaction tran = sqlSession.selectOne("com.distribute.tx.common.TransactionMapper.queryById",
					paramString);

		return tran;
	}

	@Override
	public boolean insert(Transaction paramTransaction) {
		sqlSession.insert("com.distribute.tx.common.TransactionMapper.insert",paramTransaction);
		return true;
	}

	@Override
	public int update(Transaction paramTransaction) {
		int res = sqlSession.update("com.distribute.tx.common.TransactionMapper.update",
					paramTransaction);
		
		return res;
	}

}
