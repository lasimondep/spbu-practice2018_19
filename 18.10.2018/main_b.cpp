#include <cstdio>
#include <thread>
#include <algorithm>
#include <mutex>

using namespace std;

mutex M;
                                         
void foo(int*);

int n(100000);

int main() {
	srand(23917);
	int *a(new int[n]);
	for (int i(0); i < n; ++i)
		a[i] = rand();
	thread p1(&foo, a), p2(&foo, a);
	p1.join(), p2.join();
	for (int i(0); i < n - 1; ++i)
		if (a[i] > a[i + 1])
			printf("!!: a[%d] > a[%d] (%d > %d)\n", i, i + 1, a[i], a[i + 1]);
	return 0;
}

void foo(int *a) {
	M.lock();
	sort(a, a + n);
	M.unlock();
}