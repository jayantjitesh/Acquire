package acquire.testbed;

import org.w3c.dom.Document;
import acquire.basic.HotelChainI;
import acquire.basic.Location;
import acquire.basic.StockDescription;
import acquire.board.BoardI;
import acquire.board.inspect.FoundingResult;
import acquire.board.inspect.GrowResult;
import acquire.board.inspect.ImposibleResult;
import acquire.board.inspect.MergeResult;
import acquire.board.inspect.SingletonResult;
import acquire.state.StateI;
import acquire.state.perspective.EndPerspective;
import acquire.state.perspective.PlayerPerspective;
import acquire.state.player.PlayerState;

public interface AcquireSerilizer<T> {
  T serialize(BoardI board, Document doc);

  T serialize(HotelChainI hotelChain, Document doc);

  T serialize(Location location, Document doc);

  T serialize(StockDescription stockDescription, Document doc);

  T serialize(StateI state, Document doc);

  T serialize(PlayerPerspective perspective, Document doc);

  T serialize(EndPerspective endPerspective, Document doc);

  T serialize(PlayerState playerState, Document doc);

  T serialize(FoundingResult founding, Document doc);

  T serialize(GrowResult grow, Document doc);

  T serialize(ImposibleResult imposible, Document doc);

  T serialize(MergeResult merge, Document doc);

  T serialize(SingletonResult singleton, Document doc);

}
