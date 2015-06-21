package acquire.player.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import acquire.basic.HotelChain;
import acquire.basic.HotelChainI;
import acquire.basic.Action.PlacementType;
import acquire.basic.HotelChainI.HotelName;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.InspectResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.PossibleResult;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.exception.InvalidSharePriceException;
import acquire.protocol.description.ActionDescription;
import acquire.protocol.request.PurchaseRequest;
import acquire.state.money.BankerI;
import acquire.state.perspective.Perspective;
import acquire.state.player.PlayerStateI;

public class MyStrategy {

	public PossibleResult choosePlacement(
			Set<PossibleResult> validLocations,
			Perspective perspective) {
		PossibleResult chosenTile = null;
		int maxValue = 0;
		for (PossibleResult tile : validLocations) {
			int value = getValueAfterPlacement(tile, perspective);
			if (value > maxValue) {
				maxValue = value;
				chosenTile = tile;
			}
		}
		return chosenTile;
	}

	/**
	 * 
	 * @param placement
	 *            The Location that Player wish to place
	 * @param perspective
	 *            current game state
	 * @return the value (cash+stock) that player will have after placing loc
	 */
	private int getValueAfterPlacement(PossibleResult placement,
			Perspective perspective) {
		BoardI board = perspective.getBoard();
		PlayerStateI currentPlayer = perspective.getCurrentPlayer();
		BankerI banker = perspective.getBanker();
		int result = 0;
		switch (placement.getType()) {
		case GROW:
			result = getValueAfterGrow(placement, currentPlayer, banker, board);
			break;
		case FOUND:
			result = getValueAfterFound(placement, currentPlayer, banker, board);
			break;
		case MERGE:
			result = getValueAfterMerge(placement, currentPlayer, banker, board);
			break;
		default:
			result = getValueOther(currentPlayer, banker, board);
			break;
		}
		return result;
	}

	/**
	 * 
	 * @param placement
	 *            PossibleResult of the Tile the player wishes to place
	 * @param currentPlayer
	 *            current state of this Player
	 * @param banker
	 *            current state of Banker
	 * @param board
	 *            current state of Board
	 * @return the cash and stock value of this player after Grow
	 */
	private int getValueAfterMerge(PossibleResult placement,
			PlayerStateI currentPlayer, BankerI banker, BoardI board) {
		Map<HotelName, Integer> hotelChainSize = createHotelChainSize(board
				.getHotelChains().values());
		HotelName acquirer = (HotelName) ((MergeResult) placement).getLargest()
				.toArray()[0];
		Set<HotelName> mergingHotels = ((MergeResult) placement).getHotels();
		int size = 1;
		for (HotelName name : mergingHotels) {
			size += board.getHotelChainSize(name);
		}
		hotelChainSize.put(acquirer, size);
		return getValue(currentPlayer, banker, hotelChainSize);
	}

	/**
	 * 
	 * @param placement
	 *            PossibleResult of the Tile the player wishes to place
	 * @param currentPlayer
	 *            current state of this Player
	 * @param banker
	 *            current state of Banker
	 * @param board
	 *            current state of Board
	 * @return the cash and stock value of this player after Grow
	 */
	private int getValueAfterFound(PossibleResult placement,
			PlayerStateI currentPlayer, BankerI banker, BoardI board) {
		Map<HotelName, Integer> hotelChainSize = createHotelChainSize(board
				.getHotelChains().values());
		hotelChainSize.put(((FoundingResult) placement).getName(),
				((FoundingResult) placement).getAddedHotels().size() + 1);
		return getValue(currentPlayer, banker, hotelChainSize);
	}

	/**
	 * 
	 * @param currentPlayer
	 *            PlayerState of the current player
	 * @param banker
	 *            current state of Banker
	 * @param board
	 *            current state of Board
	 * @return the cash and stock value of this player
	 */
	private int getValueOther(PlayerStateI currentPlayer, BankerI banker,
			BoardI board) {
		Map<HotelName, Integer> hotelChainSize = createHotelChainSize(board
				.getHotelChains().values());
		return getValue(currentPlayer, banker, hotelChainSize);
	}

	/**
	 * 
	 * @param values
	 *            A Collection of HotelChainI formed on Board
	 * @return a Map of HotelName and it's current size
	 */
	private Map<HotelName, Integer> createHotelChainSize(
			Collection<HotelChainI> values) {
		Map<HotelName, Integer> hotelChainSize = new HashMap<HotelChainI.HotelName, Integer>();
		for (HotelChainI chain : values) {
			hotelChainSize.put(chain.getName(), chain.getSize());
		}
		return hotelChainSize;
	}

	/**
	 * 
	 * @param placement
	 *            PossibleResult of the Tile the player wishes to place
	 * @param currentPlayer
	 *            current state of this Player
	 * @param banker
	 *            current state of Banker
	 * @param board
	 *            current state of Board
	 * @return the cash and stock value of this player after Grow
	 */
	private int getValueAfterGrow(PossibleResult placement,
			PlayerStateI currentPlayer, BankerI banker, BoardI board) {
		Map<HotelName, Integer> hotelChainSize = createHotelChainSize(board
				.getHotelChains().values());
		HotelName growingHotel = ((GrowResult) placement).getName();
		hotelChainSize.put(growingHotel, hotelChainSize.get(growingHotel) + 1);
		return getValue(currentPlayer, banker, hotelChainSize);
	}

	/**
	 * 
	 * @param currentPlayer
	 *            current state of this Player
	 * @param banker
	 *            current state of Banker
	 * @param hotelChainSize
	 *            current size of Hotel's formed on Board
	 * @return the value(cash+stock) of this Player
	 */
	private int getValue(PlayerStateI currentPlayer, BankerI banker,
			Map<HotelName, Integer> hotelChainSize) {
		Map<HotelName, StockDescription> stocks = currentPlayer
				.getStockOptions();
		int value = currentPlayer.getMoney();
		for (StockDescription stock : stocks.values()) {
			int size = 0;
			HotelName name = stock.getName();
			if (hotelChainSize.get(name) != null)
				size = hotelChainSize.get(name);
			try {
				value += banker.getSharePrice(name, size);
			} catch (InvalidSharePriceException e) {
				continue;
			}
		}
		return value;
	}

	public PurchaseRequest choosePurchase(
			Map<HotelName, Integer> availableShares, BankerI banker, int cash,
			Map<HotelName, Integer> hotelChainSize) {
		// PurchaseRequest purchase = new PurchaseRequest();
		HotelName hotelName = checkForUnfoundedChain(availableShares, banker,
				cash, hotelChainSize);
		if (hotelName != null)
			try {
				return new PurchaseRequest(hotelName, hotelName);
			} catch (InvalidPurchaseRequestException e) {
				throw new AcquireRuntimeException("This will never happen");
			}

		return checkForCheapestStocks(availableShares, banker, cash,
				hotelChainSize);
	}

	/**
	 * 
	 * @param availableShares
	 *            available shares after PlaceRequest
	 * @param banker
	 *            current state of Banker
	 * @param cash
	 *            cash value of this Player
	 * @param hotelChainSize
	 *            Map of each HotelChain and it's size after PlaceRequest
	 * @return PurchaseRequest
	 */
	private PurchaseRequest checkForCheapestStocks(
			Map<HotelName, Integer> availableShares, BankerI banker, int cash,
			Map<HotelName, Integer> hotelChainSize) {
		int minCost = cash + 1;
		HotelName buyStock = null;
		int buyCount = 0;
		for (int count = PurchaseRequest.MAX_SHARES; count >= 1; count--) {
			for (HotelName name : HotelName.values()) {
				if (canBuyStock(availableShares, banker, cash, hotelChainSize,
						name, count)) {
					try {
						int cost = banker.getSharePrice(name,
								hotelChainSize.get(name))
								* count;
						if(cost < minCost){
							minCost = cost;
							buyStock = name;
							buyCount = count;
						}
					} catch (InvalidSharePriceException e) {
						throw new AcquireRuntimeException(
								"This will never happen");
					}
				}
			}
		}
		try
		{
		if(buyStock == null)
			return new PurchaseRequest();
		else if(buyCount == 2)
			return new PurchaseRequest(buyStock, buyStock);
		else
			return new PurchaseRequest(buyStock);
		}
		catch (InvalidPurchaseRequestException e) {
			throw new AcquireRuntimeException("This will never happen");
		}
	}

	/**
	 * 
	 * @param availableShares
	 *            available shares after PlaceRequest
	 * @param banker
	 *            current state of Banker
	 * @param cash
	 *            cash value of this Player
	 * @param hotelChainSize
	 *            Map of each HotelChain and it's size after PlaceRequest
	 * @return HotelName of the unfounded HotelChain if any as per priority
	 */
	private HotelName checkForUnfoundedChain(
			Map<HotelName, Integer> availableShares, BankerI banker, int cash,
			Map<HotelName, Integer> hotelChainSize) {

		Set<HotelName> hotels = new LinkedHashSet<HotelName>();
		hotels.add(HotelName.CONTINENTAL);
		hotels.add(HotelName.TOWER);
		hotels.add(HotelName.AMERICAN);
		hotels.add(HotelName.FESTIVAL);
		hotels.add(HotelName.IMPERIAL);
		for (HotelName name : hotels) {
			if (hotelChainSize.get(name) == 0
					&& canBuyStock(availableShares, banker, cash,
							hotelChainSize, name, PurchaseRequest.MAX_SHARES))
				return name;
		}
		return null;
	}

	/**
	 * 
	 * @param availableShares
	 *            available shares after PlaceRequest
	 * @param banker
	 *            current state of Banker
	 * @param cash
	 *            cash value of this Player
	 * @param hotelChainSize
	 *            Map of each HotelChain and it's size after PlaceRequest
	 * @param name
	 *            HotelName for which Player wish to buy Stocks
	 * @param count
	 *            number of shares Player wish to buy
	 * @return if buyable then true else false
	 */
	private boolean canBuyStock(Map<HotelName, Integer> availableShares,
			BankerI banker, int cash, Map<HotelName, Integer> hotelChainSize,
			HotelName name, int count) {
		int size = hotelChainSize.get(name);
		int sharePrice = 0;
		try {
			sharePrice = banker.getSharePrice(name, size);
		} catch (InvalidSharePriceException ex) {
			sharePrice = 0;
		}
		if (sharePrice > 0 && cash >= count * sharePrice
				&& availableShares.get(name) >= count)
			return true;
		return false;
	}

	public List<Boolean> keep(List<HotelName> labels) {
		List<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < labels.size(); i++) {
			result.add(true);
		}
		return result;
	}

}
