package com.youku.search.util;

public class Pair<A, B> {

	public A a;
	public B b;

	public Pair() {
	}

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof Pair) {
			Pair p = (Pair) obj;
			if (a == p.a || (a != null && a.equals(p.a))) {
				if (b == p.b || (b != null && b.equals(p.b))) {
					return true;
				}
			}
		}

		return false;
	}
}
