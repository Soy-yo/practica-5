package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class EventTest {

  private Event.Builder eb = new Implementation();

  @Test
  void validIdTest1() {
    assertTrue(eb.isValid("v1"));
  }

  @Test
  void validIdTest2() {
    assertTrue(eb.isValid("_v1"));
  }

  @Test
  void validIdTest3() {
    // sin espacios
    assertFalse(eb.isValid("vehicle 1"));
  }

  @Test
  void validIdTest4() {
    assertTrue(eb.isValid("vehicle_1"));
  }

  @Test
  void validIdTest5() {
    // alfabeto inglés
    assertFalse(eb.isValid("vehicle_ñ"));
  }

  @Test
  void validIdTest6() {
    // sin guiones
    assertFalse(eb.isValid("v-1"));
  }

  @Test
  void validIdTest7() {
    assertTrue(eb.isValid("1234"));
  }

  @Test
  void validIdTest8() {
    assertTrue(eb.isValid("vehicle"));
  }

  private class Implementation implements Event.Builder {
    @Override
    public Event parse(IniSection section) {
      throw new UnsupportedOperationException("Nothing to do");
    }
  }

}