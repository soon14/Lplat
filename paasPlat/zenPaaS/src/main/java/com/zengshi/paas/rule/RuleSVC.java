package com.zengshi.paas.rule;

/**
 * 规则服务接口类
 *
 */
public interface RuleSVC {
	public void executeRule(Object...objects);
	public void executeRuleById(String ruleId, Object...objects);
}
