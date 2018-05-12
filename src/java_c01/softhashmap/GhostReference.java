package java_c01.softhashmap;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

public class GhostReference extends PhantomReference {
  private static final Collection currentRefs = new HashSet();
  private static final Field referent;

  static {
    try {
      referent = Reference.class.getDeclaredField("referent");
      referent.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException("Field \"referent\" not found");
    }
  }

  public GhostReference(Object referent, ReferenceQueue queue) {
    super(referent, queue);
    currentRefs.add(this);
  }

  public void clear() {
    currentRefs.remove(this);
    super.clear();
  }

  public Object getReferent() {
    try {
      return referent.get(this);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("referent should be accessible!");
    }
  }
}