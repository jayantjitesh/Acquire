package acquire.player.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents all the possible hotel labels
 *
 */
public enum Label  {
	


	AMERICAN(getPriceB()),
	CONTINENTAL(getPriceC()),
	FESTIVAL(getPriceB()),
	IMPERIAL(getPriceB()),
	SACKSON(getPriceA()),
	TOWER(getPriceC()),
	WORLDWIDE(getPriceA());
	
	Label(Map<Integer, Integer> marketPrice) {
		this.marketPrice = marketPrice;
	}
	
	private Map<Integer, Integer> marketPrice;
	
	private static Map<Integer, Integer> getPriceA () {
		
		HashMap <Integer, Integer> priceA = new HashMap<Integer, Integer>();
		
		priceA.put(2,200);
		priceA.put(3,300);
		priceA.put(4,400);
		priceA.put(5,500);
		priceA.put(6,600);
		priceA.put(11,700);
		priceA.put(21,800);
		priceA.put(31,900);
		priceA.put(41,1000);
		
		return priceA;
		
	}
	
	
	private static Map<Integer, Integer> getPriceB () {
		
		HashMap <Integer, Integer> priceA = new HashMap<Integer, Integer>();
		
		priceA.put(0,200);
		priceA.put(2,300);
		priceA.put(3,400);
		priceA.put(4,500);
		priceA.put(5,600);
		priceA.put(6,700);
		priceA.put(11,800);
		priceA.put(21,900);
		priceA.put(31,1000);
		priceA.put(41,1100);
		
		return priceA;
		
	}
	
	private static Map<Integer, Integer> getPriceC () {
		
		Map <Integer, Integer> priceA = new HashMap<Integer, Integer>();
		
		priceA.put(0,300);
		priceA.put(2,400);
		priceA.put(3,500);
		priceA.put(4,600);
		priceA.put(5,700);
		priceA.put(6,800);
		priceA.put(11,900);
		priceA.put(21,1000);
		priceA.put(31,1100);
		priceA.put(41,1200);
		
		return priceA;
		
	}

	public int getMarketValue (int numTiles) throws CantBuyHotelException {
		
		int tempPrice = 0;
		
		List <Integer> keys = new ArrayList<Integer>(marketPrice.keySet());
		
		Collections.sort(keys);
		
		for (int key : keys) {
		    if (numTiles <= key) {
		    	if (keys.indexOf(key) == 0 && numTiles != key)
		    		throw new CantBuyHotelException();
		    	 tempPrice = marketPrice.get(key); 
		    	break;
		    }  
		}
		
		return tempPrice;
	}

	
}
