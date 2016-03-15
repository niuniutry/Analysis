package com.ll.analysis;

import java.util.List;
import java.util.Scanner;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.StockQuotation;
import com.htsc.hub.db.model.StockQuotationExample;

public class Strategy_1 {
	
	//选股
	public void getStock(){
		StockQuotationExample example = new StockQuotationExample();
		example.or().andPChangeGreaterThanOrEqualTo(9.94).andDateEqualTo("2016-01-04");
		
		List<StockQuotation> result = DataBaseFacade.getInstance().<StockQuotation>getRowList(
				"stock_quotation.selectByExample", example,  null);
		
		System.out.println("【1】股票池数量："+result.size());
		System.out.println("【2】选择股票：");
		for(StockQuotation stock: result){
			System.out.println(stock.getCode() + ":" + stock.getName());
		}
		
		System.out.println("请输入：");
		String code1 = getScan();
		System.out.println("选择股票1："+code1);
		String code2 = getScan();
		System.out.println("选择股票2："+code2);
		
		System.out.println("【3】计算买入数量：");
		String date = "2016-01-05";
		float openPrice1 = getOpenPrice(code1,date);
		float openPrice2 = getOpenPrice(code2,date);
		int amount1 = getAmount(openPrice1);
		int amount2 = getAmount(openPrice2);
		System.out.println("股票1买入数量："+amount1);
		System.out.println("股票2买入数量："+amount2);
		
		System.out.println("【4】资金总额：");
		System.out.println("【5】总收益：");
		
	}
	
	public static void main(String[] args){
		new Strategy_1().getStock();
		//new Strategy_1().getScan();
		//new Strategy_1().getAmount(12.1f);
	}
	
	//获取输入
	public String getScan(){
		Scanner scan = new Scanner(System.in);
		return scan.nextLine();
	}
	
	//获取开盘价
	public float getOpenPrice(String code, String date){
		StockQuotationExample example = new StockQuotationExample();
		example.or().andCodeEqualTo(code).andDateEqualTo(date);
		
		StockQuotation result = DataBaseFacade.getInstance().<StockQuotation>getRow(
				"stock_quotation.selectByExample", example,  null);
		
		return result.getOpen();
	}
	
	//计算买入数量
	public int getAmount(float open){
		int amount = (int)((20000/open)/100)*100;
		System.out.println("amount："+amount);
		return amount;
	}
	
	//计算总收益
	public float getTotalProfit(float initAccount, float totalAccount){
		return totalAccount/initAccount;
	}
	
	//计算资金总额
	/*public float getTotalAccount(){
		return scan.nextLine();
	}*/

}
