package com.ll.analysis;

import java.math.BigDecimal;
import java.util.List;

import com.htsc.hub.db.DataBaseFacade;
import com.htsc.hub.db.model.StockAssets;
import com.htsc.hub.db.model.StockAssetsExample;
import com.htsc.hub.db.model.StockChoose;
import com.htsc.hub.db.model.StockChooseExample;
import com.htsc.hub.db.model.StockDate;
import com.htsc.hub.db.model.StockDateExample;
import com.htsc.hub.db.model.StockQuotation;
import com.htsc.hub.db.model.StockQuotationExample;
import com.htsc.hub.db.model.StockShareholding;
import com.htsc.hub.db.model.StockShareholdingExample;

public class Strategy_1_v2 {
	
	private static final float initAssets = 80000f;
	
	//ѡ��
	public static void sellStock(String date){
		//�ֲֹ�����
		StockShareholdingExample stockShareholdingExample = new StockShareholdingExample();
		stockShareholdingExample.setOrderByClause("id");
		List<StockShareholding> sellResult = DataBaseFacade.getInstance().<StockShareholding>getRowList(
				"stock_shareholding.selectByExample", stockShareholdingExample,  null);
		
		for(StockShareholding stockShareholding:sellResult){
			DataBaseFacade.getInstance().deleteRow("stock_shareholding.deleteByPrimaryKey", stockShareholding.getId());
			System.out.println("[2]������Ʊ:"+ stockShareholding.getCode());
			
			//�鿪�̼ۣ������������ֽ��ʲ�
			float openPrice = getOpenPrice(stockShareholding.getCode(), date);
			System.out.println("[2]�۸�:"+ openPrice);
			System.out.println("[2]����:"+ stockShareholding.getAmount());
			System.out.println("[2]�ܶ�:"+ openPrice*stockShareholding.getAmount());
			System.out.println("[2]ӯ��:"+ stockShareholding.getAmount()*(openPrice-stockShareholding.getPrice()));
			updateCashAssets(openPrice* stockShareholding.getAmount());

			getTotalProfit(date);
		}
	}
	
	public static void buyStock(String date){
		StockChooseExample example = new StockChooseExample();
		example.or().andDateEqualTo(date);
		example.setOrderByClause("id");
		
		//ѡ�ɡ����򡢸��³ֱֲ�
		List<StockChoose> result = DataBaseFacade.getInstance().<StockChoose>getRowList(
				"stock_choose.selectByExample", example,  null);
		
		for(StockChoose stock: result){
			String code = stock.getCode();
			float openPrice = getOpenPrice(code,date);
			if(openPrice!=0){
				int amount = getAmount(openPrice);
				System.out.println("[1]�����Ʊ:" + stock.getCode());
				System.out.println("[1]�۸�:" + openPrice);
				System.out.println("[1]����:" + amount);
				System.out.println("[1]���:" + openPrice*amount);
				
				//���³ֱֲ�
				StockShareholding shareHolding = new StockShareholding();
				shareHolding.setDate(date);
				shareHolding.setCode(code);
				shareHolding.setAmount(amount);
				shareHolding.setPrice(openPrice);
				
				DataBaseFacade.getInstance().insertRow(
						"stock_shareholding.insertSelective", shareHolding);
				
				updateCashAssets(openPrice*amount*(-1));
				getTotalProfit(date);
			}
		}
	}
	
	//�����ֽ��ʲ�
	public static void updateCashAssets(float changeAmount){
		StockAssetsExample assetsExample = new StockAssetsExample();
		StockAssets result = DataBaseFacade.getInstance().<StockAssets>getRow(
				"stock_assets.selectByExample", assetsExample,  null);
		
		StockAssets assets = new StockAssets();
		assets.setCashAssets(result.getCashAssets() + changeAmount);
		assets.setId(result.getId());
		DataBaseFacade.getInstance().<StockAssets>getRow(
				"stock_assets.updateByPrimaryKeySelective", assets, null);
	}
	
	public static void main(String[] args){
		//��ȡ������
		StockDateExample example = new StockDateExample();
		example.setOrderByClause("date");
		List<StockDate> result = DataBaseFacade.getInstance().<StockDate>getRowList(
				"stock_date.selectByExample", example,  null);
		
		for(StockDate stockDate: result){
			sellStock(stockDate.getDate());
			buyStock(stockDate.getDate());
		}
	}
	
	//��ȡ���̼�
	public static float getOpenPrice(String code, String date){
		StockQuotationExample example = new StockQuotationExample();
		example.or().andCodeEqualTo(code).andDateEqualTo(date);
		
		StockQuotation result = DataBaseFacade.getInstance().<StockQuotation>getRow(
				"stock_quotation.selectByExample", example,  null);
		
		float open = 0f;
		if(result!=null){
			return open = result.getOpen();
		}
		
		return open;
	}
	
	//������������
	public static int getAmount(float open){
		int amount = (int)((20000/open)/100)*100;
		return amount;
	}
	
	//��������
	public static float getTotalProfit(String date){
		StockAssetsExample assetsExample = new StockAssetsExample();
		StockAssets result = DataBaseFacade.getInstance().<StockAssets>getRow(
				"stock_assets.selectByExample", assetsExample,  null);
		
		float stockAssets = getStockAssets(date);
		float profit = (result.getCashAssets()+ stockAssets)/initAssets;
		System.out.println("[3]�ֽ��ʲ���"+result.getCashAssets());
		System.out.println("[3]�����ʣ�"+profit);
		return profit;
	}
	
	//�����Ʊ��ֵ
	public static float getStockAssets(String date){
		StockShareholdingExample example = new StockShareholdingExample();
		example.or().andDateEqualTo(date);
		
		List<StockShareholding> result = DataBaseFacade.getInstance().<StockShareholding>getRowList(
				"stock_shareholding.selectByExample", example,  null);
		
		float stockAssets = 0f;
		for(StockShareholding stockShareholding : result){
			stockAssets += stockShareholding.getAmount() * stockShareholding.getPrice();
		}
		
		return stockAssets;
	}
	
	//�Ƿ�һ����ͣ
	public static boolean dailyLimit(String code,String date){
		boolean result = false;
		StockQuotationExample example = new StockQuotationExample();
		example.or().andCodeEqualTo(code).andDateEqualTo(date);
		
		StockQuotation stock = DataBaseFacade.getInstance().<StockQuotation>getRow(
				"stock_quotation.selectByExample", example,  null);
		
		if(stock!=null){
			if(stock.getOpen()==stock.getClose()&&stock.getpChange()>=9.94){
				result = true;
			}
		}
		return result;
	}
	
	//�Ƿ���ͣ
	public static boolean limit(String code,String date){
		boolean result = false;
		StockQuotationExample example = new StockQuotationExample();
		example.or().andCodeEqualTo(code).andDateEqualTo(date);
		
		StockQuotation stock = DataBaseFacade.getInstance().<StockQuotation>getRow(
				"stock_quotation.selectByExample", example,  null);
		
		if(stock!=null){
			if(stock.getpChange()>=9.94){
				result = true;
			}
		}
		return result;
	}
	
	public static float format(float num) {
		BigDecimal b = new BigDecimal(num);
		num = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return num;
	}
	
	public static String getNextTransDay(String date) {
		StockDateExample example = new StockDateExample();
		example.or().andDateGreaterThan(date);
		example.setOrderByClause("date");
		
		List<StockDate> result = DataBaseFacade.getInstance().<StockDate>getRowList(
				"stock_date.selectByExample", example,  null);
		return result.get(0).getDate();
	}

}
