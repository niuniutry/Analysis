package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.Stock;
import com.htsc.hub.db.model.StockExample;

/*

 */
public class Strategy_2 {

	public void selectStock(String date) {

		// ��ȡ���еı�ѡ����
		List<Stock> targetDayResult = getTargetDate();
		for (Stock stock : targetDayResult) {
			System.out.println("����Ϊ��");
			StockExample example = new StockExample();

			// ��ѯ��Ʊ��
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
