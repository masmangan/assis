import java.io.Serializable;
import javax.swing.JPanel;

// external (JDK)
class A implements Serializable { }

// unresolved (unknown)
interface B extends BB { }

// external (JDK)
class C extends JPanel { }

// unresolved (unknown)
class D extends DD { }

// declared → declared
class E extends A { }

// declared → declared (but B has its own unresolved)
class F implements B { }