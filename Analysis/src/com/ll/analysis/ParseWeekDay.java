package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.StockDate;
import com.htsc.hub.db.model.StockDateExample;
import com.ll.util.Util;

public class ParseWeekDay {
	
	public static void main(String[] args){
		new ParseWeekDay().parse();
	}
	
	public void parse(){
		StockDateExample example = new StockDateExample();
		
		List<StockDate> result = DataBaseFacade.getInstance().<StockDate>getRowList(
				"stock_date.selectByExample", example,  null);
		
		for(StockDate stockDate:result){
		  stockDate.setDay(Util.getDayOfWeek(stockDate.getDate()));
		  DataBaseFacade.getInstance().updateRow("stock_date.updateByPrimaryKeySelective", stockDate);
		}
	}
	
	
}
