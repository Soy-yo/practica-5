package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTest {

  private Event.Builder eb = new Implementation();

  @Test
  public void validIdTest1() {
    assertTrue(eb.isValid("v1"));
  }

  @Test
  public void validIdTest2() {
    assertTrue(eb.isValid("_v1"));
  }

  @Test
  public void validIdTest3() {
    // sin espacios
    assertFalse(eb.isValid("vehicle 1"));
  }

  @Test
  public void validIdTest4() {
    assertTrue(eb.isValid("vehicle_1"));
  }

  @Test
  public void validIdTest5() {
    // alfabeto inglés
    assertFalse(eb.isValid("vehicle_ñ"));
  }

  @Test
  public void validIdTest6() {
    // sin guiones
    assertFalse(eb.isValid("v-1"));
  }

  @Test
  public void validIdTest7() {
    assertTrue(eb.isValid("1234"));
  }

  @Test
  public void validIdTest8() {
    assertTrue(eb.isValid("vehicle"));
  }

  private class Implementation implements Event.Builder {

    @Override
    public Event parse(IniSection section) {
      throw new UnsupportedOperationException("Nothing to do");
    }

    @Override
    public String getEventName() {
      throw new UnsupportedOperationException("Nothing to do");
    }

    @Override
    public String getEventFileTemplate() {
      throw new UnsupportedOperationException("Nothing to do");
    }

  }

}