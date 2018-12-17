#include <mutex>

template <typename T>
class Atomic {
	std::mutex M;
	T data;
	public:
	Atomic(const T &other = T()) : data(other) {}
	Atomic(const Atomic &other) : Atomic(other.data) {}
	T operator=(const T &other) {
		M.lock();
		data = other;
		M.unlock();
		return data;
	}
	operator T() {
		M.lock();
		T res(data);
		M.unlock();
		return res;
	}
};

int main() {
	Atomic <int> a;
	a = 5;
	printf("a = %d\n", (int)a);
	Atomic <int> b(a + 3);
	printf("b = %d\n", (int)b);
	b = b + a;
	printf("b = %d\n", (int)b);
	b = b + 15;
	printf("b = %d\n", (int)b);
	return 0;
}