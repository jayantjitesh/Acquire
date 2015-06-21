package acquire.game;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import acquire.basic.HotelChainI.HotelName;
import acquire.exception.AcquireRuntimeException;
import acquire.exception.InvalidPurchaseListException;
import acquire.exception.InvalidPurchaseRequestException;
import acquire.protocol.request.PurchaseRequest;

public class ShareCombination {
  private static final Logger LOGGER = Logger.getLogger(ShareCombination.class);

  public static List<PurchaseRequest> generateCombinations() {
    List<PurchaseRequest> result = new ArrayList<PurchaseRequest>();
    // the initial vector
    ICombinatoricsVector<HotelName> initialVector = Factory
        .createVector(HotelName.values());

    result.addAll(createListOfPurchaseRequest(Factory
        .createMultiCombinationGenerator(initialVector, 1)));
    result.addAll(createListOfPurchaseRequest(Factory
        .createMultiCombinationGenerator(initialVector, 2)));
    // result.addAll(createListOfPurchaseRequest(Factory
    // .createMultiCombinationGenerator(initialVector, 3)));

    return result;
  }

  public static List<PurchaseRequest> createListOfPurchaseRequest(
      Generator<HotelName> gen) {
    List<PurchaseRequest> result = new ArrayList<PurchaseRequest>();
    for (ICombinatoricsVector<HotelName> iCombinatoricsVector : gen) {
      List<HotelName> labels = iCombinatoricsVector.getVector();
      try {
        PurchaseRequest request = PurchaseRequest.createPurchaseRequest(labels);
        result.add(request);
      } catch (InvalidPurchaseListException ex) {
        AcquireRuntimeException.logAndThrow(LOGGER,
            "We know this will not happen: ");
      } catch (InvalidPurchaseRequestException ex) {
        continue;
      }

    }
    return result;
  }
}
