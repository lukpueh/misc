import java.lang.management.ManagementFactory;

class HelloWorld {

  public String attr;
  public int depth;
 
  static {
     System.loadLibrary("HelloWorld");
  }

  private static class LazyHolder {
      private static final HelloWorld INSTANCE = new HelloWorld();
  }

  public static HelloWorld getInstance() {
      return LazyHolder.INSTANCE;
  }

  public static void goJava() {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    String pid = name.split("@")[0];
    System.out.println(String.format("Child has gone Java - PID %s", pid));

    HelloWorld inst = HelloWorld.getInstance();
    System.out.println(inst.attr);
    inst.depth += 1;
    if (inst.depth < 3) {
      inst.goNative();
    }
      
  }
  private native void goNative();

  public static void main(String[] args) {
     HelloWorld inst = HelloWorld.getInstance();
     inst.attr = "Yes I am an attribute";
     inst.depth = 0;
     inst.goNative();
  }
 }