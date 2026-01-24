package pb;

import pa.A;
import pa.Base;


interface B extends A { }      

class Child extends Base implements B { }   

enum E implements A {  }