package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.Stock;
import com.htsc.hub.db.model.StockExample;

/*

 */
public class Strategy_2 {

	public void selectStock(String date) {

		// 获取所有的备选日期
		List<Stock> targetDayResult = getTargetDate();
		for (Stock stock : targetDayResult) {
			System.out.println("日期为：");
			StockExample example = new StockExample();

			// 查询股票池
			List<Stock> result = DataBaseFacade.getInstance()
					.<Stock> getRowList("stock.selectByExample", example, null);

			for (Stock stock1 : result) {

			}
		}

	}

	public List<Stock> getTargetDate() {
		StockExample example = new StockExample();
		List<Stock> result = DataBaseFacade.getInstance().<Stock> getRowList(
				"stock.selectByExample", example, null);

		return result;
	}

}
