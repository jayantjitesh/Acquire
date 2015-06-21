package acquire.testbed;

import acquire.testbed.request.RunRequest;

/**
 * Callback api for code that uses the parsing library.
 */
public interface ParseHandler {

  /**
   * Process the set up request
   * 
   * @param request Description of request.
   */
  void handleRun(RunRequest request);

}
