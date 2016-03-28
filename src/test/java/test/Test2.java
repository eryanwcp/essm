package test;

/**
 * 多态示例.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-5-9 下午11:16:28 
 * @version 1.0
 */
public class Test2 {

	public static void main(String[] args) {
		A a = new AA();
		a.world();
		System.out.println(a.number);
		AA aa = new AA();
		aa.world();
		System.out.println(aa.number);
	}

}

abstract class A {
	
	int number = 1;
	public abstract void hello();
	
	public void world(){
		hello();
		System.out.println("A world()");
	}
}

class AA extends A {
	int number = 11;
	public void hello() {
		System.out.println("AA hello()");
	}

}
