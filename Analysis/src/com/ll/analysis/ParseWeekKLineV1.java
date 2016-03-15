package com.ll.analysis;

import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.Stock;
import com.htsc.hub.db.model.StockExample;
import com.htsc.hub.db.model.StockQuotationQ;
import com.htsc.hub.db.model.StockQuotationQExample;
import com.htsc.hub.db.model.StockWeekDay;
import com.htsc.hub.db.model.StockWeekDayExample;
import com.htsc.hub.db.model.StockWeekQuotation;
import com.htsc.hub.db.model.StockWeekQuotationExample;
import com.htsc.hub.db.model.StockWeekQuotationQ;
import com.htsc.hub.db.model.StockWeekQuotationQExample;

public class ParseWeekKLineV1 {
	
	public static void main(String[] args){
		//new ParseWeekKLine().getWeekKLineData();
		new ParseWeekKLineV1().getData();
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
			stockWeekQuotationExample.or().andCodeEqualTo(stock.getCode());
				//.andDateGreaterThan("2016-02-12");
			stockWeekQuotationExample.setOrderByClause("date");
			List<StockWeekQuotation> stockWeekQuotationResult = DataBaseFacade.getInstance().<StockWeekQuotation>getRowList(
					"stock_week_quotation.selectByExample", stockWeekQuotationExample,  null);
			
			int seq = 0;
			for(int i=0;i<stockWeekQuotationResult.size();i++){
				StockWeekQuotation weekquotation = stockWeekQuotationResult.get(i);
				if(weekquotation.getOpen()>weekquotation.getClose()){
					//if(weekquotation.getClose()<weekquotation.getOpen()*50){
						seq++;
						if(seq>=4){
							float openPoint = stockWeekQuotationResult.get(i-seq+1).getOpen();
							float closePoint = stockWeekQuotationResult.get(i).getClose();
							if(closePoint<=openPoint*0.60){
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
	
	//获取全部个股的周线数据
	public void getAllWeekKLineData(){
		System.out.println("开始获取周线数据==================");
		StockExample stockExample = new StockExample();
		stockExample.setOrderByClause("code");
		
		List<Stock> stockResult = DataBaseFacade.getInstance().<Stock>getRowList(
				"stock.selectByExample", stockExample,  null);
		
		for(Stock stock : stockResult){
			System.out.println("stockCode:"+stock.getCode());
			saveWeekKLineData(stock.getCode());
		}
		System.out.println("结束获取周线数据==================");
	}
	
	
	//周线数据存入数据库
	public void saveWeekKLineData(String stockCode){
		StockWeekDayExample example = new StockWeekDayExample();
		List<StockWeekDay> result = DataBaseFacade.getInstance().<StockWeekDay>getRowList(
				"stock_week_day.selectByExample", example,  null);
		
		for(int i=0;i<result.size();i++){
			if(i!=result.size()-1){
				String firstTransDayOfWeek = result.get(i).getDate();
				String lastTransDayOfWeek = result.get(i+1).getDate();
				
				//获取一周的每日行情
				StockQuotationQExample dayQuotationQExample = new StockQuotationQExample();
				dayQuotationQExample.or().andDateGreaterThan(firstTransDayOfWeek)
						.andDateLessThanOrEqualTo(lastTransDayOfWeek).andCodeEqualTo(stockCode);
				dayQuotationQExample.setOrderByClause("date");
				
				List<StockQuotationQ> dayQuotationOfOneWeek = DataBaseFacade.getInstance().<StockQuotationQ>getRowList(
						"stock_quotation_q.selectByExample", dayQuotationQExample, null);

				
				if(dayQuotationOfOneWeek.size()>0){
					StockWeekQuotationQ weekQuotationQ = new StockWeekQuotationQ();
					weekQuotationQ.setOpen(dayQuotationOfOneWeek.get(0).getOpen());
					weekQuotationQ.setClose(dayQuotationOfOneWeek.get(dayQuotationOfOneWeek.size()-1).getClose());
					weekQuotationQ.setCode(stockCode);
					weekQuotationQ.setDate(lastTransDayOfWeek);
					
					StockWeekQuotationQExample weekQuotationQExample = new StockWeekQuotationQExample();
					weekQuotationQExample.or().andCodeEqualTo(stockCode).andDateEqualTo(lastTransDayOfWeek);
					List<StockQuotationQ> stockQuotationQList = DataBaseFacade.getInstance().<StockQuotationQ>getRowList(
							"stock_quotation_q.selectByExample", weekQuotationQExample, null);
					
					if(stockQuotationQList.size()==0)
						DataBaseFacade.getInstance().insertRow(
								"stock_week_quotation_q.insertSelective", weekQuotationQ);
						System.out.println(stockCode + ":" + lastTransDayOfWeek + "插入成功");
					}
				}
		}
	}
}
