package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.Stock;
import com.htsc.hub.db.model.StockExample;
import com.htsc.hub.db.model.StockQuotation;
import com.htsc.hub.db.model.StockQuotationExample;
import com.htsc.hub.db.model.StockWeekDay;
import com.htsc.hub.db.model.StockWeekDayExample;
import com.htsc.hub.db.model.StockWeekQuotation;
import com.htsc.hub.db.model.StockWeekQuotationExample;

public class ParseWeekKLine {
	
	public static void main(String[] args){
		//new ParseWeekKLine().parseWeekKLine();
		String date = "2015-02-17";
		String stockCode = "000001";
		//String firstTransDate = new ParseWeekKLine().getFirstTransDayOfWeek(stockCode,date);
		//System.out.print(firstTransDate);
		//new ParseWeekKLine().getWeekKLineData();
		new ParseWeekKLine().getData();
	}
	
	//选股
	public void getData(){
		System.out.println("开始==================");
		StockExample stockExample = new StockExample();
		stockExample.setOrderByClause("code");
		List<Stock> stockResult = DataBaseFacade.getInstance().<Stock>getRowList(
				"stock.selectByExample", stockExample,  null);
		
		for(Stock stock : stockResult){
			StockWeekQuotationExample stockWeekQuotationExample = new StockWeekQuotationExample();
			stockWeekQuotationExample.or().andCodeEqualTo(stock.getCode())
				.andDateGreaterThan("2016-02-12");
			stockWeekQuotationExample.setOrderByClause("date");
			List<StockWeekQuotation> stockWeekQuotationResult = DataBaseFacade.getInstance().<StockWeekQuotation>getRowList(
					"stock_week_quotation.selectByExample", stockWeekQuotationExample,  null);
			
			int seq = 0;
			for(int i=0;i<stockWeekQuotationResult.size();i++){
				StockWeekQuotation weekquotation = stockWeekQuotationResult.get(i);
				if(weekquotation.getOpen()>weekquotation.getClose()){
					//if(weekquotation.getClose()<weekquotation.getOpen()*50){
						seq++;
						if(seq>=3){
							float openPoint = stockWeekQuotationResult.get(i-seq+1).getOpen();
							float closePoint = stockWeekQuotationResult.get(i).getClose();
							if(closePoint<=openPoint*0.65){
								System.out.println("备选："+weekquotation.getCode() +"====日期：" +weekquotation.getDate());
							}
						}
					/*}else{
						seq = 0;
					}	*/
				}else{
					seq = 0;
				}
				
				if(i==stockWeekQuotationResult.size()-1){
					seq = 0;
				}
			}
		}
		System.out.println("结束==================");
	}
	
	//获取周线数据，写入数据库
	public void getWeekKLineData(){
		System.out.println("开始==================");
		StockExample stockExample = new StockExample();
		stockExample.setOrderByClause("code");
		//stockExample.or().andCodeGreaterThan("002697");
		//stockExample.or().andCodeGreaterThanOrEqualTo(("002137"));
		//stockExample.or().andCodeEqualTo(("002137"));
		List<Stock> stockResult = DataBaseFacade.getInstance().<Stock>getRowList(
				"stock.selectByExample", stockExample,  null);
		
		System.out.println("stockResult.size():"+stockResult.size());
		for(Stock stock : stockResult){
			System.out.println("stockCode:"+stock.getCode());
			saveWeekKLineData(stock.getCode());
		}
		System.out.println("结束==================");
	}
	
	public void saveWeekKLineData(String stockCode){
		//获取2015年每周的最后一个交易日（有bug，没有考虑到周最后一个交易日停牌的情况）
		/*StockWeekDayExample example = new StockWeekDayExample();
		List<StockWeekDay> result = DataBaseFacade.getInstance().<StockWeekDay>getRowList(
				"stock_week_day.selectByExample", example,  null);
		
		for(int i=0;i<result.size();i++){
			if(i!=0){
				StockWeekDay lastTransDayOfWeek = result.get(i);*/
				//String firstTransDayOfWeek = getFirstTransDayOfWeek(stockCode,lastTransDayOfWeek.getDate());
				String firstTransDayOfWeek = "2016-03-07";
				String lastTransDayOfWeek = "2016-03-11";
				//获取一周的每日行情
				StockQuotationExample dayQuotationExample = new StockQuotationExample();
				dayQuotationExample.or().andDateGreaterThanOrEqualTo(firstTransDayOfWeek)
						.andDateLessThanOrEqualTo(lastTransDayOfWeek).andCodeEqualTo(stockCode);
				dayQuotationExample.setOrderByClause("date");
				
				List<StockQuotation> dayQuotationOfOneWeek = DataBaseFacade.getInstance().<StockQuotation>getRowList(
						"stock_quotation.selectByExample", dayQuotationExample, null);
				
				if(dayQuotationOfOneWeek.size()>0){
					StockWeekQuotation weekQuotation = new StockWeekQuotation();
					weekQuotation.setOpen(dayQuotationOfOneWeek.get(0).getOpen());
					weekQuotation.setClose(dayQuotationOfOneWeek.get(dayQuotationOfOneWeek.size()-1).getClose());
					weekQuotation.setCode(stockCode);
					weekQuotation.setDate(lastTransDayOfWeek);
					
					DataBaseFacade.getInstance().insertRow(
							"stock_week_quotation.insertSelective", weekQuotation);
					System.out.println("插入成功");
					
				}
			//}
		//}
		
	}
	
	//获取指定日期（最后一个交易日）该周的第一个交易日
	public String getFirstTransDayOfWeek(String code,String date){
		//先获取上周最后一个交易日
		StockWeekDayExample example = new StockWeekDayExample();
		example.or().andDateLessThan(date);
		example.setOrderByClause("date desc");
		
		List<StockWeekDay> weekDayResult = DataBaseFacade.getInstance().<StockWeekDay>getRowList(
				"stock_week_day.selectByExample", example,  null);
		
		if(weekDayResult.size()>0){
			String lastDayOfLastWeek = weekDayResult.get(0).getDate();
			System.out.println(lastDayOfLastWeek);
			
			StockQuotationExample dayQuotationExample = new StockQuotationExample();
			dayQuotationExample.or().andCodeEqualTo(code).andDateGreaterThan(lastDayOfLastWeek)
					.andDateLessThanOrEqualTo(date);
			dayQuotationExample.setOrderByClause("date");
			
			List<StockQuotation> dayQuotationResult = DataBaseFacade.getInstance().<StockQuotation>getRowList(
					"stock_quotation.selectByExample", dayQuotationExample, null);
			
			if(dayQuotationResult.size()>0){
				return dayQuotationResult.get(0).getDate();
			}else{
				return date;
			}
		}else{
			return date;
		}
	}
	
	
}
