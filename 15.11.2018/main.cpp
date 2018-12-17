#include <thread>
#include <chrono>
#include <cstdio>

using namespace std;

template <typename T, class F>
class future {
	thread *t;
	T *data;
	future(const future&) = delete;
	public:
	future() {
		F temp;
		data = new T();
		t = new thread(temp, data);
	}
	future(future &&other) : t(other.t), data(other.data) {}
	T &get() {
		t->join();
		return *data;
	}
};

class IntFunc {
	public:
	void operator()(int *data) {
		this_thread::sleep_for(3s);
		*data = 15;
	}
};

typedef future<int, IntFunc> FuncFuture;

void foo(FuncFuture &&x) {
	printf("%d", x.get());
}

int main() {
	FuncFuture f;
	puts("Hello!");
	foo(move(f));
	return 0;
}