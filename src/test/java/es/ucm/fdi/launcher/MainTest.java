package es.ucm.fdi.launcher;

import es.ucm.fdi.excepcions.SimulatorError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

  private static final String RES = "src/test/resources/";

  @Test
  void basicTest() throws Exception {
    Main.test(RES + "examples/basic");
  }

  @Test
  void advancedTest() throws Exception {
    Main.test(RES + "examples/advanced");
  }

  @Test
  void errTest() {
    assertThrows(SimulatorError.class, () -> Main.test(RES + "examples/err"));
  }

}