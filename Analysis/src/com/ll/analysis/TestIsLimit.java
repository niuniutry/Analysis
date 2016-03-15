package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.StockChoose;
import com.htsc.hub.db.model.StockChooseExample;

public class TestIsLimit {

	public static void isDailyLimit() {
		StockChooseExample example = new StockChooseExample();
		example.setOrderByClause("date");

		List<StockChoose> result = DataBaseFacade.getInstance()
				.<StockChoose> getRowList("stock_choose.selectByExample",
						example, null);

		for (StockChoose stock : result) {
			String code = stock.getCode();
			String date = stock.getDate();
			System.out.println("股票:" + code + "日期：" + date);
			Boolean isDailyLimit = Strategy_1_v2.dailyLimit(code, date);
			System.out.println("日期:" + isDailyLimit);
		}
	}

	public static void main(String[] args) {
		isDailyLimit();
	}

}
