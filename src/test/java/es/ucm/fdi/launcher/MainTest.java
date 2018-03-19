package es.ucm.fdi.launcher;

import org.junit.jupiter.api.Test;

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
  void errTest() throws Exception {
    Main.test(RES + "examples/err");
  }

}